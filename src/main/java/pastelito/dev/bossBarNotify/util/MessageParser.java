package pastelito.dev.bossBarNotify.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.util.regex.Pattern;

public final class MessageParser {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private static final Pattern MINIMESSAGE_TAG = Pattern.compile("<[^>]+>");

    private MessageParser() {
    }

    public static String parse(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        if (MINIMESSAGE_TAG.matcher(input).find()) {
            try {
                Component component = MINI_MESSAGE.deserialize(input);
                return LEGACY.serialize(component);
            } catch (Exception ignored) {
            }
        }

        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
