/*
 * Copyright (C) 2012-2022 Frank Baumann
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

import de.erethon.dungeonsxl.util.commons.compatibility.Version;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author Daniel Saukel
 */
public class ContainerAdapter {

    public static boolean isValidContainer(Block block) {
        if (Version.isAtLeast(Version.MC1_12_1)) {
            return block.getState() instanceof Container;
        } else {
            return block.getState() instanceof Chest;
        }
    }

    public static boolean isValidContainer(Inventory inventory) {
        if (Version.isAtLeast(Version.MC1_12_1)) {
            return inventory.getHolder() instanceof Container;
        } else {
            return inventory.getHolder() instanceof Chest;
        }
    }

    public static Block getHolderBlock(InventoryHolder holder) {
        if (Version.isAtLeast(Version.MC1_12_1)) {
            return ((Container) holder).getBlock();
        } else {
            return ((Chest) holder).getBlock();
        }
    }

    public static Inventory getBlockInventory(Block block) {
        if (Version.isAtLeast(Version.MC1_12_1)) {
            return ((Container) block.getState()).getInventory();
        } else {
            return ((Chest) block.getState()).getInventory();
        }
    }

}
