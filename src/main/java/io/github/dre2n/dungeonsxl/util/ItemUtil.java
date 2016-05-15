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

import io.github.dre2n.commons.util.NumberUtil;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
@Deprecated
public class ItemUtil {

    public static List<ItemStack> fromConfig(ConfigurationSection configSectionClasses) {
        List<String> items = configSectionClasses.getStringList("items");
        CopyOnWriteArrayList<ItemStack> itemStacks = new CopyOnWriteArrayList<>();

        for (String item : items) {
            String[] itemSplit = item.split(",");
            if (itemSplit.length > 0) {
                int itemId = 0, itemData = 0, itemSize = 1, itemLvlEnchantment = 1;
                Enchantment itemEnchantment = null;
                // Check Id & Data
                String[] idAndData = itemSplit[0].split("/");
                itemId = NumberUtil.parseInt(idAndData[0]);

                if (idAndData.length > 1) {
                    itemData = NumberUtil.parseInt(idAndData[1]);
                }

                // Size
                if (itemSplit.length > 1) {
                    itemSize = NumberUtil.parseInt(itemSplit[1]);
                }
                // Enchantment
                if (itemSplit.length > 2) {
                    String[] enchantmentSplit = itemSplit[2].split("/");

                    itemEnchantment = Enchantment.getByName(enchantmentSplit[0]);

                    if (enchantmentSplit.length > 1) {
                        itemLvlEnchantment = NumberUtil.parseInt(enchantmentSplit[1]);
                    }
                }

                // Add Item to Stacks
                ItemStack itemStack = new ItemStack(itemId, itemSize, (short) itemData);
                if (itemEnchantment != null) {
                    itemStack.addEnchantment(itemEnchantment, itemLvlEnchantment);
                }
                itemStacks.add(itemStack);
            }
        }

        return itemStacks;
    }

}
