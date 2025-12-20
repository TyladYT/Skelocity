package me.tylad.skelocity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public final class ProxyRequestService {

    private final Map<String, Queue<Runnable>> waiters = new HashMap<>();
    private final Set<String> inFlight = new HashSet<>();

    public void fetch(String key, Runnable resume, Payload payload) {
        waiters.computeIfAbsent(key, k -> new ArrayDeque<>()).add(resume);

        if (inFlight.contains(key)) return;
        inFlight.add(key);

        Player sender = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (sender == null) {
            finish(key);
            return;
        }

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytes);
            payload.write(out);
            sender.sendPluginMessage(
                    Skelocity.getInstance(),
                    "BungeeCord",
                    bytes.toByteArray()
            );
        } catch (IOException e) {
            finish(key);
        }
    }

    public void complete(String key) {
        finish(key);
    }

    private void finish(String key) {
        inFlight.remove(key);
        Queue<Runnable> q = waiters.remove(key);
        if (q != null) {
            while (!q.isEmpty()) {
                q.poll().run();
            }
        }
    }

    @FunctionalInterface
    public interface Payload {
        void write(DataOutputStream out) throws IOException;
    }
}
