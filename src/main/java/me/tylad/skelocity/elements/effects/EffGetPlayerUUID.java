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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class EffGetPlayerUUID extends Effect {

    static {
        Skript.registerEffect(
                EffGetPlayerUUID.class,
                "get [(proxy|velocity)] uuid of %offlineplayer% and store it in %object%"
        );
    }

    private Expression<OfflinePlayer> target;
    private Variable<?> variable;

    @Override
    protected void execute(Event event) { }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        OfflinePlayer p = target.getSingle(event);
        if (p == null || p.getName() == null) return getNext();

        Player sender = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (sender == null) return getNext();

        TriggerItem next = getNext();
        Delay.addDelayedEvent(event);

        Skelocity.getProxyRequestService().fetch(
                "UUIDOther",
                () -> Bukkit.getScheduler().runTask(
                        Skelocity.getInstance(),
                        () -> {
                            String uuid = Skelocity.getLastUUIDOther();

                            variable.change(
                                    event,
                                    new Object[]{uuid},
                                    Changer.ChangeMode.SET
                            );

                            TriggerItem.walk(next, event);
                        }
                ),
                out -> {
                    out.writeUTF("UUIDOther");
                    out.writeUTF(p.getName());
                }
        );

        return null;
    }


    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        target = (Expression<OfflinePlayer>) exprs[0];

        if (!(exprs[1] instanceof Variable)) {
            Skript.error("You must store the UUID in a variable.");
            return false;
        }

        variable = (Variable<?>) exprs[1];
        getParser().setHasDelayBefore(Kleenean.TRUE);
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "toString of EffGetPlayerUUID";
    }
}
