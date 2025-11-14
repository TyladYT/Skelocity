package me.tylad.skelocity.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.tylad.skelocity.Skelocity;

public class ExpressionServerCount extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExpressionServerCount.class, Number.class, ExpressionType.COMBINED, "[the] [total] player count of [(proxy|velocity)] server %string%");
    }

    private Expression<String> server;

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        this.server = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "player count of server " + server;
    }

    @Override
    @Nullable
    protected Number[] get(Event event) {
        String targetServer = this.server.getSingle(event);
        if (targetServer == null) return new Number[]{0};

        Player sender = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (sender == null) return new Number[]{0};

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytes);
            out.writeUTF("PlayerCount");
            out.writeUTF(targetServer);

            Thread.sleep(1);

            sender.sendPluginMessage(Skelocity.getInstance(), "BungeeCord", bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new Number[]{Skelocity.getServerCount(targetServer)};
    }
}
