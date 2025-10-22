package com.github.linghun91.dungeonsxl.api.player;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * 16 team colors
 * @author linghun91
 */
public enum GroupColor {
    DARK_BLUE(NamedTextColor.DARK_BLUE),
    LIGHT_RED(NamedTextColor.RED),
    YELLOW(NamedTextColor.YELLOW),
    LIGHT_GREEN(NamedTextColor.GREEN),
    PURPLE(NamedTextColor.DARK_PURPLE),
    ORANGE(NamedTextColor.GOLD),
    BLACK(NamedTextColor.BLACK),
    LIGHT_BLUE(NamedTextColor.BLUE),
    DARK_GREEN(NamedTextColor.DARK_GREEN),
    DARK_RED(NamedTextColor.DARK_RED),
    LIGHT_GRAY(NamedTextColor.GRAY),
    CYAN(NamedTextColor.AQUA),
    MAGENTA(NamedTextColor.LIGHT_PURPLE),
    DARK_GRAY(NamedTextColor.DARK_GRAY),
    PINK(NamedTextColor.LIGHT_PURPLE),
    WHITE(NamedTextColor.WHITE);

    private final TextColor color;

    GroupColor(TextColor color) {
        this.color = color;
    }

    public TextColor getColor() {
        return color;
    }
}
