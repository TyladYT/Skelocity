package me.tylad.skelocity.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import me.tylad.skelocity.utils.TextUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EffectBroadcastGlobalMessage extends Effect {

    static {
        Skript.registerEffect(EffectBroadcastGlobalMessage.class, "(proxy|velocity|global|network) broadcast %string%");
    }

    private Expression<String> message;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        this.message = (Expression<String>) expressions[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "broadcast global proxy message " + message.toString(event, debug);
    }

    @Override
    protected void execute(Event event) {
        String msg = message.getSingle(event);
        if (msg == null || Bukkit.getOnlinePlayers().isEmpty()) return;

        Player sender = Bukkit.getOnlinePlayers().iterator().next();

        try {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArray);

            String legacyString = TextUtils.componentToLegacy(msg);

            out.writeUTF("Message");
            out.writeUTF("ALL"); // send to all players across the proxy
            out.writeUTF(legacyString);

            sender.sendPluginMessage(
                    Bukkit.getPluginManager().getPlugin("Skelocity"), "BungeeCord", byteArray.toByteArray()
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
