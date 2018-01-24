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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.dungeonsxl.loottable.DLootTable;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import io.github.dre2n.dungeonsxl.world.block.RewardChest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ChestSign extends DSign {

    private DSignType type = DSignTypeDefault.CHEST;

    private Block chest;

    private double moneyReward;
    private int levelReward;
    private ItemStack[] chestContent;
    private DLootTable lootTable;

    public ChestSign(Sign sign, String[] lines, DGameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    /* Getters and setters */
    /**
     * @return the money reward
     */
    public double getMoneyReward() {
        return moneyReward;
    }

    /**
     * @param amount
     * the amount to set
     */
    public void setMoneyReward(double amount) {
        moneyReward = amount;
    }

    /**
     * @return the level reward
     */
    public int getLevelReward() {
        return levelReward;
    }

    /**
     * @param amount
     * the amount to set
     */
    public void setLevelReward(int amount) {
        levelReward = amount;
    }

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
     * @param items
     * the items to set as chest contents
     */
    public void setItemReward(ItemStack[] items) {
        chestContent = items;
    }

    /**
     * @return the custom loot table
     */
    public DLootTable getLootTable() {
        return lootTable;
    }

    /**
     * @param lootTable
     * the loot table to set
     */
    public void setLootTable(DLootTable lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public DSignType getType() {
        return type;
    }

    /* Actions */
    /**
     * Checks for a chest next to the sign and sets the reward to its contents.
     */
    public void checkChest() {
        Block sign = getSign().getBlock();
        for (int i = -1; i <= 1; i++) {
            Block xRelative = sign.getRelative(i, 0, 0);
            Block yRelative = sign.getRelative(0, i, 0);
            Block zRelative = sign.getRelative(0, 0, i);

            if (xRelative.getType() == Material.CHEST) {
                if (chestContent == null) {
                    chestContent = ((Chest) xRelative.getState()).getBlockInventory().getContents();
                }
                chest = xRelative;

            } else if (yRelative.getType() == Material.CHEST) {
                if (chestContent == null) {
                    chestContent = ((Chest) yRelative.getState()).getBlockInventory().getContents();
                }
                chest = yRelative;

            } else if (zRelative.getType() == Material.CHEST) {
                if (chestContent == null) {
                    chestContent = ((Chest) zRelative.getState()).getBlockInventory().getContents();
                }
                chest = zRelative;
            }
        }
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        if (!lines[1].isEmpty()) {
            String[] attributes = lines[1].split(",");
            if (attributes.length >= 1) {
                moneyReward = NumberUtil.parseDouble(attributes[0]);
            }
            if (attributes.length >= 2) {
                levelReward = NumberUtil.parseInt(attributes[1]);
            }
        }

        if (!lines[2].isEmpty()) {
            lootTable = plugin.getDLootTables().getByName(lines[2]);
        }

        if (chest == null) {
            checkChest();
        }

        if (chest != null) {
            ItemStack[] itemReward = chestContent;
            if (lootTable != null) {
                List<ItemStack> list = new LinkedList<>(Arrays.asList(chestContent));
                list.addAll(lootTable.generateLootList());
                itemReward = list.toArray(new ItemStack[list.size()]);
            }

            getGameWorld().addGameBlock(new RewardChest(chest, moneyReward, levelReward, itemReward));
            getSign().getBlock().setType(Material.AIR);

        } else {
            markAsErroneous();
        }
    }

}
