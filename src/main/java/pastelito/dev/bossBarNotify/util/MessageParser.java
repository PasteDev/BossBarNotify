package pastelito.dev.bossBarNotify.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.util.regex.Pattern;

public final class MessageParser {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private static final LegacyComponentSerializer LEGACY_AMPERSAND = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();
    private static final Pattern MINIMESSAGE_TAG = Pattern.compile("<[^>]+>");

    private MessageParser() {
    }

    public static Component parseComponent(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        if (MINIMESSAGE_TAG.matcher(input).find()) {
            try {
                return MINI_MESSAGE.deserialize(input);
            } catch (Exception ignored) {
            }
        }

        return LEGACY_AMPERSAND.deserialize(ChatColor.translateAlternateColorCodes('&', input));
    }

    public static String parse(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        return LEGACY.serialize(parseComponent(input));
    }
}
