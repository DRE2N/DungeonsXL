/*
 * Copyright (C) 2012-2020 Frank Baumann
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

import de.erethon.dungeonsxl.DungeonsXL;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Deprecated
public enum GUIUtil {

    X1(4),
    X2(3, 5),
    X3(2, 4, 6),
    X4(1, 3, 5, 7),
    X5(0, 2, 4, 6, 8),
    X6(1, 2, 3, 5, 6, 7),
    X7(1, 2, 3, 4, 5, 6, 7),
    X8(0, 1, 2, 3, 5, 6, 7, 8),
    X9(0, 1, 2, 3, 4, 5, 6, 7, 8);

    private int[] slot;

    private GUIUtil(int... slot) {
        this.slot = slot;
    }

    /**
     * @param count the amount of buttons
     */
    public static GUIUtil getRowShapeByButtonCount(int count) {
        if (count >= 1 && count <= 9) {
            for (GUIUtil shape : values()) {
                if (shape.slot.length == count) {
                    return shape;
                }
            }
        }

        return null;
    }

    /**
     * @param plugin  the plugin instance
     * @param title   the title for the GUI
     * @param buttons the buttons to use for the GUI
     * @return an Inventory that can easily be shown to a Player
     */
    public static Inventory createGUI(DungeonsXL plugin, String title, ItemStack... buttons) {
        return createGUI(plugin, title, Arrays.asList(buttons));
    }

    /**
     * @param plugin  the plugin instance
     * @param title   the GUI title, may contain color codes
     * @param buttons the buttons to use for the GUI
     * @return an Inventory that can easily be shown to a Player
     */
    public static Inventory createGUI(DungeonsXL plugin, String title, List<ItemStack> buttons) {
        int rows = (int) Math.ceil(((double) buttons.size()) / 9);
        Inventory gui = plugin.getServer().createInventory(null, rows * 9, ChatColor.translateAlternateColorCodes('&', title));

        int slot = 0;
        int row = 0;
        int i = 0;
        GUIUtil shape = null;

        for (ItemStack button : buttons) {
            if (row + 1 == rows) {
                if (shape == null) {
                    shape = getRowShapeByButtonCount(buttons.size() - i);
                }

                gui.setItem(shape.slot[slot] + row * 9, button);
                slot++;

            } else {
                gui.setItem(X9.slot[slot] + row * 9, button);

                slot++;
                if (slot == 9) {
                    slot = 0;
                    row++;
                }
            }
            i++;
        }

        plugin.getGUIs().add(gui);
        return gui;
    }

}
