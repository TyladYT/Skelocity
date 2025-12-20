package me.tylad.skelocity;

import me.tylad.skelocity.commands.SkelocityCommand;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Skelocity extends JavaPlugin implements PluginMessageListener {

    // Stores
    private static final Map<String, Integer> serverCounts = new ConcurrentHashMap<>();
    private static final Map<String, String> lastKnownServers = new ConcurrentHashMap<>();
    private static final Map<String, String[]> playerLists = new ConcurrentHashMap<>();
    private static final Map<String, String> lastKnownIPs = new ConcurrentHashMap<>();
    private static int totalProxyCount = 0;
    private static Skelocity instance;
    private static ProxyRequestService proxyRequestService;
    private static String[] serverList = new String[0];
    private static String lastUUIDOther;

    private File file;

    SkriptAddon addon;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("[Skelocity] You are running the FIRST version of Skelocity!");
        Bukkit.getLogger().info("[Skelocity] Please report any bugs at https://discord.gg/khpfQKhxrd <3");
        instance = this;

        int pluginId = 27750;
        Metrics metrics = new Metrics(this, pluginId);

        // Load config file
        file = new File(Skelocity.getInstance().getDataFolder(), "config.yml");
        if (!file.exists()){
            Skelocity.getInstance().saveResource("config.yml", false);
        }
        YamlConfiguration config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addon = Skript.registerAddon(this);

        try {
            addon.loadClasses("me.tylad.skelocity", "elements");
        } catch (IOException error) {
            error.printStackTrace();
        }

        proxyRequestService = new ProxyRequestService();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);


        getCommand("skelocity").setExecutor(new SkelocityCommand());
        Bukkit.getLogger().info("[Skelocity] Skelocity has been enabled.");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) return;

        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(message))) {
            String sub = in.readUTF();
            String key = null;

            if (sub.equals("PlayerCount")) {
                String server = in.readUTF();
                int count = in.readInt();

                if (server.equalsIgnoreCase("ALL")) {
                    totalProxyCount = count;
                } else {
                    serverCounts.put(server.toLowerCase(), count);
                }

                key = "PlayerCount:" + server.toLowerCase();

            } else if (sub.equals("GetServer")) {
                String server = in.readUTF();
                lastKnownServers.put(player.getName(), server);
                key = "GetServer:" + player.getName();

            } else if (sub.equals("GetPlayerServer")) {
                String username = in.readUTF();
                String server = in.readUTF();
                lastKnownServers.put(username, server);
                key = "GetServer:" + username;

            } else if (sub.equals("PlayerList")) {
                String server = in.readUTF();
                String[] players = in.readUTF().split(", ");
                playerLists.put(server.toLowerCase(), players);
                key = "PlayerList:" + server.toLowerCase();
            } else if (sub.equals("GetServers")) {
                String[] servers = in.readUTF().split(", ");
                serverList = servers;
                key = "GetServers";
            } else if (sub.equals("IPOther")) {
                Bukkit.getLogger().info("Received IPOther response");
                String name = in.readUTF();
                String ip = in.readUTF();
                Bukkit.getLogger().info("IPOther: " + name + " -> " + ip);
                lastKnownIPs.put(name.toLowerCase(), ip);
                key = "IPOther:" + name.toLowerCase();
            }
            else if (sub.equals("UUIDOther")) {
                lastUUIDOther = in.readUTF();
                key = "UUIDOther";
            }



            if (key != null) {
                proxyRequestService.complete(key);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static int getServerCount(String serverName) {
        return serverCounts.getOrDefault(serverName.toLowerCase(), 0);
    }

    public static int getProxyCount() {
        return totalProxyCount;
    }

    public static Skelocity getInstance() {
        return instance;
    }

    public static void setPlayerServer(String playerName, String serverName) {
        lastKnownServers.put(playerName, serverName);
    }

    public static String getPlayerServer(String playerName) {
        return lastKnownServers.getOrDefault(playerName, "Unknown");
    }

    public static String[] getPlayerList(String server) {
        return playerLists.getOrDefault(server, new String[0]);
    }

    public static ProxyRequestService getProxyRequestService() {
        return proxyRequestService;
    }

    public static String[] getServers() {
        return serverList;
    }

    public static String getPlayerIP(String name) {
        return lastKnownIPs.get(name.toLowerCase());
    }

    public static String getLastUUIDOther() {
        return lastUUIDOther;
    }


}
