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

public class ExpressionProxyCount extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExpressionProxyCount.class, Number.class, ExpressionType.SIMPLE, "[the] [total] [(proxy|velocity)] proxy player count");
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "proxy player count";
    }

    @Override
    @Nullable
    protected Number[] get(Event event) {
        Player sender = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (sender == null) return new Number[]{0};

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytes);
            out.writeUTF("PlayerCount");
            out.writeUTF("ALL");
            sender.sendPluginMessage(Skelocity.getInstance(), "BungeeCord", bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Number[]{Skelocity.getProxyCount()};
    }
}
