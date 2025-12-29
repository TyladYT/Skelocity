package me.tylad.skelocity.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
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

public class EffProxyMessage extends Effect {

    static {
        Skript.registerEffect(
                EffProxyMessage.class,
                "send [(proxy|velocity)] message %string% to %offlineplayer%"
        );
    }

    private Expression<String> message;
    private Expression<OfflinePlayer> target;

    @Override
    protected void execute(Event event) {
        String msg = message.getSingle(event);
        OfflinePlayer targetPlayer = target.getSingle(event);

        if (msg == null || targetPlayer == null) return;

        String name = targetPlayer.getName();
        if (name == null) return;

        Player sender = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (sender == null) return;

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytes);

            out.writeUTF("Message");
            out.writeUTF(name);
            out.writeUTF(msg);

            sender.sendPluginMessage(
                    Skelocity.getInstance(),
                    "BungeeCord",
                    bytes.toByteArray()
            );
        } catch (IOException ignored) { }
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        message = (Expression<String>) exprs[0];
        target = (Expression<OfflinePlayer>) exprs[1];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "send proxy message";
    }
}
