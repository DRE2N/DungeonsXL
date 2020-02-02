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
package de.erethon.dungeonsxl.event.reward;

import de.erethon.dungeonsxl.reward.Reward;
import org.bukkit.event.Event;

/**
 * @author Daniel Saukel
 */
public abstract class RewardEvent extends Event {

    protected Reward reward;

    public RewardEvent(Reward reward) {
        this.reward = reward;
    }

    /**
     * @return the reward
     */
    public Reward getReward() {
        return reward;
    }

    /**
     * @param reward the reward to set
     */
    public void setReward(Reward reward) {
        this.reward = reward;
    }

}
