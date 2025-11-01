package me.tylad.skelocity.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tylad.skelocity.Skelocity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ExpressionProxyPlayers extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExpressionProxyPlayers.class, String.class, ExpressionType.COMBINED, "[all] (proxy|velocity) players [on [server] %-string%]");
    }

    private Expression<String> server;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        this.server = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "proxy players on server " + (server != null ? server.toString(e, debug) : "ALL");
    }

    @Override
    @Nullable
    protected String[] get(Event event) {
        String targetServer = (server != null && server.getSingle(event) != null) ? server.getSingle(event) : "ALL";

        Player sender = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (sender == null) return new String[0];

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytes);

            out.writeUTF("PlayerList");
            out.writeUTF(targetServer);

            sender.sendPluginMessage(Skelocity.getInstance(), "BungeeCord", bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Skelocity.getPlayerList(targetServer);
    }
}
