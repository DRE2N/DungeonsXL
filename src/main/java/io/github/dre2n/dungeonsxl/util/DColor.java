/*
 * Copyright (C) 2012-2018 Frank Baumann
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

import io.github.dre2n.commons.misc.EnumUtil;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;

/**
 * Links different color types together.
 *
 * @author Daniel Saukel
 */
public enum DColor {

    BLACK(ChatColor.BLACK, DyeColor.BLACK, "BLACK_WOOL"),
    DARK_GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY, "GRAY_WOOL"),
    LIGHT_GRAY(ChatColor.GRAY, DyeColor.SILVER, "LIGHT_GRAY_WOOL"),
    WHITE(ChatColor.WHITE, DyeColor.WHITE, "WHITE_WOOL"),
    DARK_GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN, "GREEN_WOOL"),
    LIGHT_GREEN(ChatColor.GREEN, DyeColor.LIME, "LIME_WOOL"),
    CYAN(ChatColor.DARK_AQUA, DyeColor.CYAN, "CYAN_WOOL"),
    DARK_BLUE(ChatColor.DARK_BLUE, DyeColor.BLUE, "BLUE_WOOL"),
    LIGHT_BLUE(ChatColor.AQUA, DyeColor.LIGHT_BLUE, "LIGHT_BLUE_WOOL"),
    PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE, "PURPLE_WOOL"),
    MAGENTA(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA, "MAGENTA_WOOL"),
    DARK_RED(ChatColor.DARK_RED, DyeColor.BROWN, "BROWN_WOOL"),
    LIGHT_RED(ChatColor.RED, DyeColor.RED, "RED_WOOL"),
    ORANGE(ChatColor.GOLD, DyeColor.ORANGE, "ORANGE_WOOL"),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, "YELLOW_WOOL"),
    DEFAULT(ChatColor.BLUE, DyeColor.PINK, "PINK_WOOL");

    private ChatColor chat;
    private DyeColor dye;
    private Material woolMaterial;

    DColor(ChatColor chat, DyeColor dye, String woolMaterial) {
        this.chat = chat;
        this.dye = dye;
        if (EnumUtil.isValidEnum(Material.class, woolMaterial)) {
            this.woolMaterial = Material.valueOf(woolMaterial);
        } else {
            this.woolMaterial = LegacyUtil.LEGACY_WOOL;
        }
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
    public Material getWoolMaterial() {
        return woolMaterial;
    }

    /**
     * @param color
     * the DyeColor to check
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
     * @param color
     * the ChatColor to check
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
