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

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
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

    public RewardChestSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    public double getMoneyReward() {
        return moneyReward;
    }

    public void setMoneyReward(double amount) {
        moneyReward = amount;
    }

    public int getLevelReward() {
        return levelReward;
    }

    public void setLevelReward(int amount) {
        levelReward = amount;
    }

    @Override
    public String getName() {
        return "RewardChest";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".rewardchest";
    }

    @Override
    public void initialize() {
        if (!getLine(1).isEmpty()) {
            String[] attributes = getLine(1).split(",");
            if (attributes.length >= 1) {
                moneyReward = NumberUtil.parseDouble(attributes[0]);
            }
            if (attributes.length >= 2) {
                levelReward = NumberUtil.parseInt(attributes[1]);
            }
        }

        if (!getLine(2).isEmpty()) {
            lootTable = api.getCaliburn().getLootTable(getLine(2));
        }

        checkChest();
        if (chest != null) {
            setToAir();
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

        ((DGameWorld) getGameWorld()).addGameBlock(new RewardChest((DungeonsXL) api, chest, moneyReward, levelReward, list.toArray(new ItemStack[list.size()])));
    }

}
