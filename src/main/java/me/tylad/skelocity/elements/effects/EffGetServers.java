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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class EffGetServers extends Effect {

    static {
        Skript.registerEffect(
                EffGetServers.class,
                "get [the] [(proxy|velocity)] servers and store them in %objects%"
        );
    }

    private Variable<?> variable;

    @Override
    protected void execute(Event event) { }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        debug(event, true);

        Player sender = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (sender == null) return getNext();

        TriggerItem next = getNext();
        Delay.addDelayedEvent(event);

        Skelocity.getProxyRequestService().fetch(
                "GetServers",
                () -> Bukkit.getScheduler().runTask(
                        Skelocity.getInstance(),
                        () -> {
                            variable.change(
                                    event,
                                    Skelocity.getServers(),
                                    Changer.ChangeMode.SET
                            );
                            TriggerItem.walk(next, event);
                        }
                ),
                out -> out.writeUTF("GetServers")
        );

        return null;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        if (!(exprs[0] instanceof Variable)) {
            Skript.error("You must store the servers in a list variable.");
            return false;
        }

        variable = (Variable<?>) exprs[0];

        if (!variable.isList()) {
            Skript.error("You must store the servers in a list variable..");
            return false;
        }

        getParser().setHasDelayBefore(Kleenean.TRUE);
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "get servers";
    }
}
