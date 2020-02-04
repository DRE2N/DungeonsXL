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
package de.erethon.dungeonsxl.sign;

import de.erethon.caliburn.loottable.LootTable;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public abstract class ChestSign extends DSign {

    protected Block chest;

    protected ItemStack[] chestContent;
    protected LootTable lootTable;

    protected ChestSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    /* Getters and setters */
    /**
     * @return the chest contents
     */
    public ItemStack[] getChestContents() {
        if (chestContent == null) {
            checkChest();
        }
        return chestContent;
    }

    /**
     * @param items the items to set as chest contents
     */
    public void setChestContents(ItemStack[] items) {
        chestContent = items;
    }

    /**
     * @return the custom loot table
     */
    public LootTable getLootTable() {
        return lootTable;
    }

    /**
     * @param lootTable the loot table to set
     */
    public void setLootTable(LootTable lootTable) {
        this.lootTable = lootTable;
    }

    /* Actions */
    /**
     * Checks for a chest next to the sign and sets the reward to its contents.
     */
    protected void checkChest() {
        Block sign = getSign().getBlock();
        for (int i = -1; i <= 1; i++) {
            Block xRelative = sign.getRelative(i, 0, 0);
            Block yRelative = sign.getRelative(0, i, 0);
            Block zRelative = sign.getRelative(0, 0, i);

            if (xRelative.getState() instanceof Container) {
                if (chestContent == null) {
                    chestContent = ((Container) xRelative.getState()).getInventory().getContents();
                }
                chest = xRelative;

            } else if (yRelative.getState() instanceof Container) {
                if (chestContent == null) {
                    chestContent = ((Container) yRelative.getState()).getInventory().getContents();
                }
                chest = yRelative;

            } else if (zRelative.getState() instanceof Container) {
                if (chestContent == null) {
                    chestContent = ((Container) zRelative.getState()).getInventory().getContents();
                }
                chest = zRelative;
            }
        }
    }

}
