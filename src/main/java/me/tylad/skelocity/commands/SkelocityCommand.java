package me.tylad.skelocity.commands;

import me.tylad.skelocity.Skelocity;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// #00A7FF

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SkelocityCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)){
            commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<#00A7FF><bold>Skelocity</bold> <gray>› This command can only be executed by players!"));
            return true;
        }

        String usageString = "/skelocity <argument>";

        if (args.length == 0){
            player.sendMessage(MiniMessage.miniMessage().deserialize("<#00A7FF><bold>Skelocity</bold> <gray>› " + usageString));
        } else {
            String option = args[0];

            if (Objects.equals(option, "docs")){
                player.sendMessage(MiniMessage.miniMessage().deserialize("<#00A7FF><bold>Skelocity</bold> <gray>› <#488aff><click:open_url:'https://skripthub.net/docs/'>ℹ Click here to view the Skelocity documentation\n<gray>→ (https://skripthub.net/docs/)</click>"));
            } else if (Objects.equals(option, "info")){
                player.sendMessage(MiniMessage.miniMessage().deserialize("<#00A7FF><bold>Skelocity</bold> <gray>› <#488aff>" + Skelocity.getInstance().getPluginMeta().getDescription()));
            } else if (Objects.equals(option, "compatibility")) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<#00A7FF><bold>Skelocity</bold> <gray>› <#488aff>ℹ Compatibility Notes\n\n<#46AFD4>→ All features have been tested and work on Velocity server <reset> <reset> <reset> <#46AFD4>software, Skelocity should also work for Bungeecord.\n\n<#46AFD4>→ Notice something odd with Skelocity? Report it <click:open_url:'https://discord.gg/khpfQKhxrd'><#89CFF0><hover:show_text:'<#46AFD4>Click here to join our discord and report an issue with Skelocity.\nAny bug reports are genuinely appreciated :)'>here<#46AFD4>! ❤\n<gray>→ (https://discord.gg/khpfQKhxrd)</hover></click>"));
            } else if (Objects.equals(option, "contributors")) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<#00A7FF><bold>Skelocity</bold> <gray>› <#488aff>ℹ Contribution Credits\n\n<#46AFD4>→ Tylad (creator)\n→ DJDisaster"));
            } else {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<#00A7FF><bold>Skelocity</bold> <gray>› <#FFA6A6>That argument doesn't exist! Choose another..."));
            }
        }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1){
            return Arrays.asList("info", "docs", "compatibility", "contributors");
        }

        return new ArrayList<>();
    }
}