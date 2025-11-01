package me.tylad.skelocity.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TextUtils {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();

    public static String componentToLegacy(String input) {
        if (input == null || input.isEmpty()) return "";

        if (input.contains("§") || input.contains("&")) {
            return input.replace('&', '§');
        }

        try {
            Component component = mm.deserialize(input);
            return legacy.serialize(component);
        } catch (Exception e) {
            return input;
        }
    }
}