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
package de.erethon.dungeonsxl.util;

import de.erethon.commons.compatibility.CompatibilityHandler;
import de.erethon.commons.compatibility.Version;
import de.erethon.commons.misc.EnumUtil;
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
    public static boolean is1_13 = CompatibilityHandler.getInstance().getVersion().useNewMaterials();

    public static Material WOODEN_SWORD = Material.valueOf(is1_13 ? "WOODEN_SWORD" : "WOOD_SWORD");
    public static Material GOLDEN_HELMET = Material.valueOf(is1_13 ? "GOLDEN_HELMET" : "GOLD_HELMET");
    public static Material GOLDEN_CESTPLATE = Material.valueOf(is1_13 ? "GOLDEN_CHESTPLATE" : "GOLD_CHESTPLATE");
    public static Material GOLDEN_LEGGINGS = Material.valueOf(is1_13 ? "GOLDEN_LEGGINGS" : "GOLD_LEGGINGS");
    public static Material GOLDEN_BOOTS = Material.valueOf(is1_13 ? "GOLDEN_BOOTS" : "GOLD_BOOTS");
    public static Material WRITABLE_BOOK = Material.valueOf(is1_13 ? "WRITABLE_BOOK" : "BOOK_AND_QUILL");
    public static Material LEGACY_WOOL = EnumUtil.getEnum(Material.class, "WOOL");
    public static Material LEGACY_SIGN_POST = EnumUtil.getEnum(Material.class, "SIGN_POST");

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
