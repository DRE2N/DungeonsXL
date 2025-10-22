package com.github.linghun91.dungeonsxl.util;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Utility class for modern message handling using Kyori Adventure
 * @author linghun91
 */
public class MessageUtil {
    
    private static final DungeonsXL plugin = DungeonsXL.getInstance();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = 
        LegacyComponentSerializer.legacyAmpersand();
    
    /**
     * Send a message to a command sender
     */
    public static void send(CommandSender sender, String message) {
        if (sender == null || message == null) return;
        sender.sendMessage(parse(message));
    }
    
    /**
     * Send a message with prefix
     */
    public static void sendPrefixed(CommandSender sender, String message) {
        String prefix = plugin.getMessageConfig().getMessage("prefix");
        send(sender, prefix + message);
    }
    
    /**
     * Send a localized message
     */
    public static void sendMessage(CommandSender sender, String key, Object... args) {
        String message = plugin.getMessageConfig().getMessage(key);
        if (args.length > 0) {
            message = String.format(message, args);
        }
        sendPrefixed(sender, message);
    }
    
    /**
     * Send success message
     */
    public static void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.GREEN));
    }
    
    /**
     * Send error message
     */
    public static void sendError(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.RED));
    }
    
    /**
     * Send warning message
     */
    public static void sendWarning(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message, NamedTextColor.YELLOW));
    }
    
    /**
     * Send action bar message
     */
    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(parse(message));
    }
    
    /**
     * Send title
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.showTitle(
            net.kyori.adventure.title.Title.title(
                parse(title),
                parse(subtitle),
                net.kyori.adventure.title.Title.Times.times(
                    java.time.Duration.ofMillis(fadeIn * 50L),
                    java.time.Duration.ofMillis(stay * 50L),
                    java.time.Duration.ofMillis(fadeOut * 50L)
                )
            )
        );
    }
    
    /**
     * Parse legacy color codes and MiniMessage
     */
    public static Component parse(String message) {
        if (message == null) return Component.empty();
        
        // First convert legacy codes to MiniMessage format
        String converted = message
            .replace("&0", "<black>")
            .replace("&1", "<dark_blue>")
            .replace("&2", "<dark_green>")
            .replace("&3", "<dark_aqua>")
            .replace("&4", "<dark_red>")
            .replace("&5", "<dark_purple>")
            .replace("&6", "<gold>")
            .replace("&7", "<gray>")
            .replace("&8", "<dark_gray>")
            .replace("&9", "<blue>")
            .replace("&a", "<green>")
            .replace("&b", "<aqua>")
            .replace("&c", "<red>")
            .replace("&d", "<light_purple>")
            .replace("&e", "<yellow>")
            .replace("&f", "<white>")
            .replace("&k", "<obfuscated>")
            .replace("&l", "<bold>")
            .replace("&m", "<strikethrough>")
            .replace("&n", "<underlined>")
            .replace("&o", "<italic>")
            .replace("&r", "<reset>");
        
        try {
            return MINI_MESSAGE.deserialize(converted);
        } catch (Exception e) {
            return Component.text(message);
        }
    }
    
    /**
     * Broadcast message to all players
     */
    public static void broadcast(String message) {
        Component component = parse(message);
        plugin.getServer().broadcast(component);
    }
    
    /**
     * Format location
     */
    public static String formatLocation(org.bukkit.Location loc) {
        if (loc == null) return "Unknown";
        return String.format("%s: %.1f, %.1f, %.1f",
            loc.getWorld() != null ? loc.getWorld().getName() : "?",
            loc.getX(), loc.getY(), loc.getZ());
    }
}
