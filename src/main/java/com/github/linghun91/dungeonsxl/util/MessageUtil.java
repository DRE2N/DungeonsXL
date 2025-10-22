package com.github.linghun91.dungeonsxl.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Utility class for handling messages using Kyori Adventure.
 * Provides modern text component support with MiniMessage format.
 *
 * @author linghun91
 */
public final class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private static final String PREFIX = "<dark_gray>[<gold>DungeonsXL</gold>]</dark_gray> ";

    private MessageUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Parses a MiniMessage string to a Component.
     *
     * @param message The message with MiniMessage tags
     * @return The parsed component
     */
    public static Component parse(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(message);
    }

    /**
     * Parses a legacy color code string to a Component.
     *
     * @param message The message with legacy color codes (&)
     * @return The parsed component
     */
    public static Component parseLegacy(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        return LEGACY.deserialize(message);
    }

    /**
     * Sends a formatted message to a command sender.
     *
     * @param sender The recipient
     * @param message The message with MiniMessage tags
     */
    public static void send(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        sender.sendMessage(parse(PREFIX + message));
    }

    /**
     * Sends a raw component to a command sender.
     *
     * @param sender The recipient
     * @param component The component to send
     */
    public static void send(CommandSender sender, Component component) {
        if (component == null) {
            return;
        }
        sender.sendMessage(component);
    }

    /**
     * Sends an info message (gray color).
     *
     * @param sender The recipient
     * @param message The message
     */
    public static void info(CommandSender sender, String message) {
        send(sender, "<gray>" + message + "</gray>");
    }

    /**
     * Sends a success message (green color).
     *
     * @param sender The recipient
     * @param message The message
     */
    public static void success(CommandSender sender, String message) {
        send(sender, "<green>" + message + "</green>");
    }

    /**
     * Sends a warning message (yellow color).
     *
     * @param sender The recipient
     * @param message The message
     */
    public static void warning(CommandSender sender, String message) {
        send(sender, "<yellow>" + message + "</yellow>");
    }

    /**
     * Sends an error message (red color).
     *
     * @param sender The recipient
     * @param message The message
     */
    public static void error(CommandSender sender, String message) {
        send(sender, "<red>" + message + "</red>");
    }

    /**
     * Broadcasts a message to all online players.
     *
     * @param message The message with MiniMessage tags
     */
    public static void broadcast(String message) {
        Component component = parse(PREFIX + message);
        org.bukkit.Bukkit.getServer().broadcast(component);
    }

    /**
     * Broadcasts a component to all online players.
     *
     * @param component The component to broadcast
     */
    public static void broadcast(Component component) {
        org.bukkit.Bukkit.getServer().broadcast(component);
    }

    /**
     * Sends a title to a player.
     *
     * @param player The player
     * @param title The title message
     * @param subtitle The subtitle message
     * @param fadeIn Fade in time in ticks
     * @param stay Stay time in ticks
     * @param fadeOut Fade out time in ticks
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.showTitle(net.kyori.adventure.title.Title.title(
                parse(title),
                parse(subtitle),
                net.kyori.adventure.title.Title.Times.times(
                        java.time.Duration.ofMillis(fadeIn * 50L),
                        java.time.Duration.ofMillis(stay * 50L),
                        java.time.Duration.ofMillis(fadeOut * 50L)
                )
        ));
    }

    /**
     * Sends an action bar message to a player.
     *
     * @param player The player
     * @param message The message
     */
    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(parse(message));
    }

    /**
     * Replaces placeholders in a message.
     *
     * @param message The message template
     * @param placeholders Key-value pairs for replacement
     * @return The message with replaced placeholders
     */
    public static String replacePlaceholders(String message, String... placeholders) {
        if (message == null || placeholders.length % 2 != 0) {
            return message;
        }

        String result = message;
        for (int i = 0; i < placeholders.length; i += 2) {
            result = result.replace(placeholders[i], placeholders[i + 1]);
        }
        return result;
    }

    /**
     * Creates a separator line for chat.
     *
     * @param color The color of the separator
     * @return The separator component
     */
    public static Component separator(NamedTextColor color) {
        return Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", color);
    }

    /**
     * Creates a header with text centered between separators.
     *
     * @param title The header title
     * @param color The color scheme
     * @return The header component
     */
    public static Component header(String title, NamedTextColor color) {
        return Component.empty()
                .append(separator(color))
                .append(Component.newline())
                .append(Component.text(title, color, TextDecoration.BOLD))
                .append(Component.newline())
                .append(separator(color));
    }

    /**
     * Joins components with a separator.
     *
     * @param separator The separator component
     * @param components The components to join
     * @return The joined component
     */
    public static Component join(Component separator, List<Component> components) {
        return Component.join(separator, components);
    }

    /**
     * Converts a component to legacy text.
     *
     * @param component The component
     * @return The legacy text
     */
    public static String toLegacy(Component component) {
        return LEGACY.serialize(component);
    }

    /**
     * Strips all formatting from a message.
     *
     * @param message The message
     * @return Plain text without formatting
     */
    public static String stripFormatting(String message) {
        return LEGACY.serialize(parse(message)).replaceAll("§.", "");
    }
}
