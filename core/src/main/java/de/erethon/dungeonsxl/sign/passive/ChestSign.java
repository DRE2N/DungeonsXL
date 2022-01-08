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
package de.erethon.dungeonsxl.sign.passive;

import de.erethon.caliburn.loottable.LootTable;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.sign.Passive;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.util.ContainerAdapter;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public abstract class ChestSign extends Passive {

    protected Block chest;

    protected ItemStack[] chestContent;
    protected LootTable lootTable;

    protected ChestSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    public ItemStack[] getChestContents() {
        if (chestContent == null) {
            checkChest();
        }
        return chestContent;
    }

    public void setChestContents(ItemStack[] items) {
        chestContent = items;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void setLootTable(LootTable lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public boolean isOnDungeonInit() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isSetToAir() {
        return false;
    }

    @Override
    public boolean validate() {
        return true;
    }

    /**
     * Checks for a chest next to the sign and sets the reward to its contents.
     */
    protected void checkChest() {
        Block sign = getSign().getBlock();
        for (int i = -1; i <= 1; i++) {
            Block xRelative = sign.getRelative(i, 0, 0);
            Block yRelative = sign.getRelative(0, i, 0);
            Block zRelative = sign.getRelative(0, 0, i);

            if (ContainerAdapter.isValidContainer(xRelative)) {
                if (chestContent == null) {
                    chestContent = ContainerAdapter.getBlockInventory(xRelative).getContents();
                }
                chest = xRelative;

            } else if (ContainerAdapter.isValidContainer(yRelative)) {
                if (chestContent == null) {
                    chestContent = ContainerAdapter.getBlockInventory(yRelative).getContents();
                }
                chest = yRelative;

            } else if (ContainerAdapter.isValidContainer(zRelative)) {
                if (chestContent == null) {
                    chestContent = ContainerAdapter.getBlockInventory(zRelative).getContents();
                }
                chest = zRelative;
            }
        }
    }

}
