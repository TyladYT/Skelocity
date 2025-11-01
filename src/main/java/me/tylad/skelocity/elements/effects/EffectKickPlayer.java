package me.tylad.skelocity.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.tylad.skelocity.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EffectKickPlayer extends Effect {

    static {
        Skript.registerEffect(EffectKickPlayer.class, "(proxy|velocity) kick %player% [(with reason|due to) %-string%]");
    }

    private Expression<Player> playerExpr;
    private Expression<String> reason;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        this.playerExpr = (Expression<Player>) expressions[0];
        this.reason = (Expression<String>) expressions[1];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "velocity kick " + playerExpr.toString(event, debug) + (reason != null ? " with reason " + reason.toString(event, debug) : "");
    }

    @Override
    protected void execute(Event event) {
        Player target = playerExpr.getSingle(event);
        String reasonMsg = (reason != null && reason.getSingle(event) != null) ? reason.getSingle(event) : "No reason specified";

        if (target == null || Bukkit.getOnlinePlayers().isEmpty()) return;

        Player sender = Bukkit.getOnlinePlayers().iterator().next();

        try {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArray);

            String legacyReason = TextUtils.componentToLegacy(reasonMsg);

            out.writeUTF("KickPlayer");
            out.writeUTF(target.getName());
            out.writeUTF(legacyReason);

            sender.sendPluginMessage(Bukkit.getPluginManager().getPlugin("Skelocity"), "BungeeCord", byteArray.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
