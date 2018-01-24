/*
 * Copyright (C) 2017 Daniel Saukel
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

import io.github.dre2n.commons.item.ItemUtil;
import io.github.dre2n.dungeonsxl.config.DMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

/**
 * @author Daniel Saukel
 */
public class GUIButton {

    /* Raw skulls */
    public static final ItemStack LEFT = ItemUtil.setSkullOwner(LegacyUtil.RAW_PLAYER_HEAD, "69b9a08d-4e89-4878-8be8-551caeacbf2a", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ViZjkwNzQ5NGE5MzVlOTU1YmZjYWRhYjgxYmVhZmI5MGZiOWJlNDljNzAyNmJhOTdkNzk4ZDVmMWEyMyJ9fX0=");
    public static final ItemStack RIGHT = ItemUtil.setSkullOwner(LegacyUtil.RAW_PLAYER_HEAD, "15f49744-9b61-46af-b1c3-71c6261a0d0e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==");

    /* GUI buttons */
    public static final ItemStack NEXT_PAGE = setDisplayName(RIGHT, DMessage.MISC_NEXT_PAGE.getMessage());
    public static final ItemStack PREVIOUS_PAGE = setDisplayName(LEFT, DMessage.MISC_PREVIOUS_PAGE.getMessage());
    public static final ItemStack PLACEHOLDER = setDisplayName(LegacyUtil.RAW_PLACEHOLDER, ChatColor.RESET.toString());
    ;

    /* Blank items that show meta stuff by default */
    public static final ItemStack GUI_SWORD = new ItemStack(Material.IRON_SWORD);
    public static final ItemStack GUI_WATER_BOTTLE = new ItemStack(Material.POTION);

    static {
        ItemMeta swordMeta = GUI_SWORD.getItemMeta();
        swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        GUI_SWORD.setItemMeta(swordMeta);

        PotionMeta watMeta = (PotionMeta) GUI_WATER_BOTTLE.getItemMeta();
        watMeta.setBasePotionData(new PotionData(PotionType.WATER));
        watMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        GUI_WATER_BOTTLE.setItemMeta(watMeta);
    }

    public static ItemStack setDisplayName(ItemStack itemStack, String name) {
        itemStack = itemStack.clone();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
