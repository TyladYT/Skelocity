package me.tylad.skelocity.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tylad.skelocity.Skelocity;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ExpressionFindPlayer extends SimpleExpression<String> {

    private static final ConcurrentHashMap<String, CompletableFuture<String>> waitingRequests = new ConcurrentHashMap<>();
    private Expression<OfflinePlayer> playerExpr;

    static {
        Skript.registerExpression(ExpressionFindPlayer.class, String.class, ExpressionType.SIMPLE, "[the] [(sk|v)elocity] server of %player%");
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        playerExpr = (Expression<OfflinePlayer>) exprs[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "server %player% is on";
    }

    @Override
    @Nullable
    protected String[] get(Event event) {
        Player target = playerExpr.getSingle(event).getPlayer();
        if (target == null) return null;

        Player sender = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (sender == null) return null;

        CompletableFuture<String> future = new CompletableFuture<>();
        waitingRequests.put(target.getName(), future);

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytes);

            out.writeUTF("GetPlayerServer");
            out.writeUTF(target.getName());

            sender.sendPluginMessage(Skelocity.getInstance(), "BungeeCord", bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String knownServer = Skelocity.getPlayerServer(target.getName());
        return new String[]{knownServer};
    }

    public static void handleServerResponse(String playerName, String serverName) {
        Skelocity.setPlayerServer(playerName, serverName);

        CompletableFuture<String> future = waitingRequests.remove(playerName);
        if (future != null) {
            future.complete(serverName);
        }
    }
}
