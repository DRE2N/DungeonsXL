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

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.block.RewardChest;
import java.util.Arrays;
import java.util.List;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class RewardChestSign extends ChestSign {

    private double moneyReward;
    private int levelReward;

    public RewardChestSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    /* Getters and setters */
    /**
     * @return the money reward
     */
    public double getMoneyReward() {
        return moneyReward;
    }

    /**
     * @param amount the amount to set
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
     * @param amount the amount to set
     */
    public void setLevelReward(int amount) {
        levelReward = amount;
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.REWARD_CHEST;
    }

    /* Actions */
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
            lootTable = plugin.getCaliburn().getLootTable(lines[2]);
        }

        checkChest();
        if (chest != null) {
            getSign().getBlock().setType(VanillaItem.AIR.getMaterial());
        } else {
            getSign().getBlock().setType(VanillaItem.CHEST.getMaterial());
            chest = getSign().getBlock();
        }

        List<ItemStack> list = null;
        if (lootTable != null) {
            list = lootTable.generateLootList();
        }
        if (chestContent != null) {
            if (list != null) {
                list.addAll(Arrays.asList(chestContent));
            } else {
                list = Arrays.asList(chestContent);
            }
        }
        if (list == null) {
            return;
        }

        getGameWorld().addGameBlock(new RewardChest(plugin, chest, moneyReward, levelReward, list.toArray(new ItemStack[list.size()])));
    }

}
