/*
 * Copyright (C) 2012-2019 Frank Baumann
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
package de.erethon.dungeonsxl.util;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.compatibility.Version;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

/**
 * Links different color types together.
 *
 * @author Daniel Saukel
 */
public enum DColor {

    BLACK(ChatColor.BLACK, DyeColor.BLACK, VanillaItem.BLACK_WOOL),
    DARK_GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY, VanillaItem.GRAY_WOOL),
    LIGHT_GRAY(ChatColor.GRAY, DyeColor.valueOf(Version.isAtLeast(Version.MC1_13) ? "LIGHT_GRAY" : "SILVER"), VanillaItem.LIGHT_GRAY_WOOL),
    WHITE(ChatColor.WHITE, DyeColor.WHITE, VanillaItem.WHITE_WOOL),
    DARK_GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN, VanillaItem.GREEN_WOOL),
    LIGHT_GREEN(ChatColor.GREEN, DyeColor.LIME, VanillaItem.LIME_WOOL),
    CYAN(ChatColor.DARK_AQUA, DyeColor.CYAN, VanillaItem.CYAN_WOOL),
    DARK_BLUE(ChatColor.DARK_BLUE, DyeColor.BLUE, VanillaItem.BLUE_WOOL),
    LIGHT_BLUE(ChatColor.AQUA, DyeColor.LIGHT_BLUE, VanillaItem.LIGHT_BLUE_WOOL),
    PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE, VanillaItem.PURPLE_WOOL),
    MAGENTA(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA, VanillaItem.MAGENTA_WOOL),
    DARK_RED(ChatColor.DARK_RED, DyeColor.BROWN, VanillaItem.BROWN_WOOL),
    LIGHT_RED(ChatColor.RED, DyeColor.RED, VanillaItem.RED_WOOL),
    ORANGE(ChatColor.GOLD, DyeColor.ORANGE, VanillaItem.ORANGE_WOOL),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, VanillaItem.YELLOW_WOOL),
    PINK(ChatColor.BLUE, DyeColor.PINK, VanillaItem.PINK_WOOL);

    private ChatColor chat;
    private DyeColor dye;
    private VanillaItem woolMaterial;

    DColor(ChatColor chat, DyeColor dye, VanillaItem woolMaterial) {
        this.chat = chat;
        this.dye = dye;
        this.woolMaterial = woolMaterial;
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
     * @deprecated Use getDyeColor() or getMaterial() instead
     * @return the wool DV
     */
    @Deprecated
    public byte getWoolData() {
        return dye.getWoolData();
    }

    /**
     * @return the wool material
     */
    public VanillaItem getWoolMaterial() {
        return woolMaterial;
    }

    /**
     * @param color the DyeColor to check
     * @return the matching DColor or null
     */
    public static DColor getByDyeColor(DyeColor color) {
        for (DColor dColor : values()) {
            if (dColor.dye == color) {
                return dColor;
            }
        }
        return null;
    }

    /**
     * @param color the ChatColor to check
     * @return the matching DColor or null
     */
    public static DColor getByChatColor(ChatColor color) {
        for (DColor dColor : values()) {
            if (dColor.chat == color) {
                return dColor;
            }
        }
        return null;
    }

}
