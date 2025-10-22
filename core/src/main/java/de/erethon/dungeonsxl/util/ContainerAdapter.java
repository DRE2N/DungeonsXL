/*
 * Copyright (C) 2012-2023 Frank Baumann
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

import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Modern Paper API implementation without version checks.
 * Updated for Paper 1.21.8
 *
 * @author Daniel Saukel
 */
public class ContainerAdapter {

    public static boolean isValidContainer(Block block) {
        return block.getState() instanceof Container;
    }

    public static boolean isValidContainer(Inventory inventory) {
        return inventory.getHolder() instanceof Container;
    }

    public static Block getHolderBlock(InventoryHolder holder) {
        return ((Container) holder).getBlock();
    }

    public static Inventory getBlockInventory(Block block) {
        return ((Container) block.getState()).getInventory();
    }
}
