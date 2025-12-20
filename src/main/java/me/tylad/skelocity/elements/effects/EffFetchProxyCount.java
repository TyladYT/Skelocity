package me.tylad.skelocity.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.classes.Changer;
import ch.njol.util.Kleenean;
import me.tylad.skelocity.Skelocity;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class EffFetchProxyCount extends Effect {

    static {
        Skript.registerEffect(
                EffFetchProxyCount.class,
                "fetch [the] [(proxy|velocity)] proxy player count [on [server] %-string%] and store it in %object%"
        );
    }

    private Expression<String> server;
    private Variable<?> variable;

    @Override
    protected void execute(Event event) { }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        debug(event, true);

        TriggerItem next = getNext();
        Delay.addDelayedEvent(event);

        String targetServer = server != null && server.getSingle(event) != null
                ? server.getSingle(event).toLowerCase()
                : "all";

        String key = "PlayerCount:" + targetServer;

        Skelocity.getProxyRequestService().fetch(
                key,
                () -> Bukkit.getScheduler().runTask(
                        Skelocity.getInstance(),
                        () -> {
                            int value = targetServer.equals("all")
                                    ? Skelocity.getProxyCount()
                                    : Skelocity.getServerCount(targetServer);

                            variable.change(
                                    event,
                                    new Object[]{value},
                                    Changer.ChangeMode.SET
                            );
                            TriggerItem.walk(next, event);
                        }
                ),
                out -> {
                    out.writeUTF("PlayerCount");
                    out.writeUTF(targetServer.equals("all") ? "ALL" : targetServer);
                }
        );

        return null;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        server = (Expression<String>) exprs[0];

        if (!(exprs[1] instanceof Variable)) {
            Skript.error("You must store the proxy count in a variable.");
            return false;
        }

        variable = (Variable<?>) exprs[1];
        getParser().setHasDelayBefore(Kleenean.TRUE);
        return true;
    }


    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "fetch proxy count";
    }
}
