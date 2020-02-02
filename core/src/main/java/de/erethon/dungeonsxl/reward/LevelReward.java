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
package de.erethon.dungeonsxl.reward;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class LevelReward extends Reward {

    private RewardType type = RewardTypeDefault.LEVEL;

    private int levels;

    public LevelReward(DungeonsXL plugin) {
        super(plugin);
    }

    /**
     * @return the levels
     */
    public int getLevels() {
        return levels;
    }

    /**
     * @param levels the levels to add
     */
    public void addLevels(int levels) {
        this.levels += levels;
    }

    /**
     * @param levels the levels to set
     */
    public void setLevels(int levels) {
        this.levels = levels;
    }

    @Override
    public void giveTo(Player player) {
        if (levels == 0) {
            return;
        }

        player.setLevel(player.getLevel() + levels);
        MessageUtil.sendMessage(player, DMessage.REWARD_GENERAL.getMessage(levels + " levels"));
    }

    @Override
    public RewardType getType() {
        return type;
    }

}
