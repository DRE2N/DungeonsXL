/*
 * Copyright (C) 2012-2016 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.dungeonsxl.util;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

/**
 * @author Daniel Saukel
 */
public enum DColor {

    BLACK(ChatColor.BLACK, DyeColor.BLACK),
    DARK_GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY),
    LIGHT_GRAY(ChatColor.GRAY, DyeColor.SILVER),
    WHITE(ChatColor.WHITE, DyeColor.WHITE),
    DARK_GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN),
    LIGHT_GREEN(ChatColor.GREEN, DyeColor.LIME),
    CYAN(ChatColor.DARK_AQUA, DyeColor.CYAN),
    DARK_BLUE(ChatColor.DARK_BLUE, DyeColor.BLUE),
    LIGHT_BLUE(ChatColor.AQUA, DyeColor.LIGHT_BLUE),
    PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE),
    MAGENTA(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA),
    DARK_RED(ChatColor.DARK_RED, DyeColor.BROWN),
    LIGHT_RED(ChatColor.RED, DyeColor.RED),
    ORANGE(ChatColor.GOLD, DyeColor.ORANGE),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW),
    DEFAULT(ChatColor.BLUE, DyeColor.PINK);

    private ChatColor chat;
    private DyeColor dye;

    DColor(ChatColor chat, DyeColor dye) {
        this.chat = chat;
        this.dye = dye;
    }

    /**
     * @return the ChatColor
     */
    public ChatColor getChatColor() {
        return chat;
    }

    /**
     * @return the DyeColor
     */
    public DyeColor getDyeColor() {
        return dye;
    }

    /**
     * @return the RGB value
     */
    public int getRGBColor() {
        return dye.getColor().asRGB();
    }

    /**
     * @return the wool DV
     */
    public byte getWoolData() {
        return dye.getWoolData();
    }

}
