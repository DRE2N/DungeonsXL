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

import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.compatibility.Version;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * Methods for backwards compatibility
 *
 * @author Daniel Saukel
 */
@Deprecated
public class LegacyUtil {

    public static boolean is1_9 = Version.andHigher(Version.MC1_9).contains(CompatibilityHandler.getInstance().getVersion());
    public static boolean is1_13 = false/*Version.andHigher(Version.MC1_13).contains(CompatibilityHandler.getInstance().getVersion())*/;

    public static Material WOODEN_SWORD = is1_13 ? Material.valueOf("WOODEN_SWORD") : Material.valueOf("WOOD_SWORD");
    public static Material GOLDEN_HELMET = is1_13 ? Material.valueOf("GOLDEN_HELMET") : Material.valueOf("GOLD_HELMET");
    public static Material GOLDEN_CESTPLATE = is1_13 ? Material.valueOf("GOLDEN_CHESTPLATE") : Material.valueOf("GOLD_CHESTPLATE");
    public static Material GOLDEN_LEGGINGS = is1_13 ? Material.valueOf("GOLDEN_LEGGINGS") : Material.valueOf("GOLD_LEGGINGS");
    public static Material GOLDEN_BOOTS = is1_13 ? Material.valueOf("GOLDEN_BOOTS") : Material.valueOf("GOLD_BOOTS");
    public static Material WRITABLE_BOOK = is1_13 ? Material.valueOf("WRITABLE_BOOK") : Material.valueOf("BOOK_AND_QUILL");
    public static Material LEGACY_WOOL = Material.valueOf("WOOL");
    public static Material LEGACY_SIGN_POST = Material.valueOf("SIGN_POST");
    public static ItemStack RAW_PLACEHOLDER = is1_13 ? new ItemStack(Material.valueOf("BLACK_STAINED_GLASS_PANE")) : new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 15);
    public static ItemStack RAW_PLAYER_HEAD = is1_13 ? new ItemStack(Material.valueOf("PLAYER_HEAD")) : new ItemStack(Material.valueOf("SKULL"), 1, (short) 3);

    public static boolean isSign(Block block) {
        if (is1_13) {
            return block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN;
        } else {
            return block.getType() == LEGACY_SIGN_POST || block.getType() == Material.WALL_SIGN;
        }
    }

    public static boolean isBed(Material material) {
        return material.name().endsWith("_BED") || material.name().equals("BED_BLOCK") || material.name().equals("BED");
    }

    public static ItemStack createColoredWool(DColor color) {
        if (is1_13) {
            return new ItemStack(color.getWoolMaterial());
        } else {
            return new ItemStack(LEGACY_WOOL, 1, color.getWoolData());
        }
    }

    public static void setBlockWoolColor(Block block, DColor color) {
        if (is1_13) {
            block.setType(color.getWoolMaterial());
        } else {
            block.setTypeIdAndData(35, color.getWoolData(), false);
        }
    }

    public static Material getMaterial(int id) {
        return Material.getMaterial(id);
    }

}
