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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.dungeonsxl.reward.RewardChest;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.Material;
import org.bukkit.block.Sign;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ChestSign extends DSign {

    private DSignType type = DSignTypeDefault.CHEST;

    // Variables
    private double moneyReward;
    private int levelReward;

    public ChestSign(Sign sign, String[] lines, GameWorld gameWorld) {
        super(sign, lines, gameWorld);
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

        for (int i = -1; i <= 1; i++) {
            if (getSign().getBlock().getRelative(i, 0, 0).getType() == Material.CHEST) {
                new RewardChest(getSign().getBlock().getRelative(i, 0, 0), getGameWorld(), moneyReward, levelReward);
            }

            if (getSign().getBlock().getRelative(0, 0, i).getType() == Material.CHEST) {
                new RewardChest(getSign().getBlock().getRelative(0, 0, i), getGameWorld(), moneyReward, levelReward);
            }

            if (getSign().getBlock().getRelative(0, i, 0).getType() == Material.CHEST) {
                new RewardChest(getSign().getBlock().getRelative(0, i, 0), getGameWorld(), moneyReward, levelReward);
            }
        }

        getSign().getBlock().setType(Material.AIR);
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
