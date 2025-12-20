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

public class EffGetPlayerIP extends Effect {

    static {
        Skript.registerEffect(
                EffGetPlayerIP.class,
                "get [(proxy|velocity)] ip of %offlineplayer% and store it in %object%"
        );
    }

    private Expression<OfflinePlayer> target;
    private Variable<?> variable;

    @Override
    protected void execute(Event event) { }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        OfflinePlayer targetPlayer = target.getSingle(event);
        if (targetPlayer == null || targetPlayer.getName() == null) return getNext();

        Player sender = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (sender == null) return getNext();

        String name = targetPlayer.getName();
        String key = "IPOther:" + name.toLowerCase();

        TriggerItem next = getNext();
        Delay.addDelayedEvent(event);

        Skelocity.getProxyRequestService().fetch(
                key,
                () -> Bukkit.getScheduler().runTask(
                        Skelocity.getInstance(),
                        () -> {
                            String ip = Skelocity.getPlayerIP(name);

                            variable.change(
                                    event,
                                    new Object[]{ip},
                                    Changer.ChangeMode.SET
                            );

                            TriggerItem.walk(next, event);
                        }
                ),
                out -> {
                    out.writeUTF("IPOther");
                    out.writeUTF(name);
                }
        );

        return null;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        target = (Expression<OfflinePlayer>) exprs[0];

        if (!(exprs[1] instanceof Variable)) {
            Skript.error("You must store the IP in a variable.");
            return false;
        }

        variable = (Variable<?>) exprs[1];
        getParser().setHasDelayBefore(Kleenean.TRUE);
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "toString of EffGetIP";
    }
}
