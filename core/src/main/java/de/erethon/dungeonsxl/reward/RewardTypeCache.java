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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RewardType instance manager.
 *
 * @author Daniel Saukel
 */
public class RewardTypeCache {

    private List<RewardType> types = new ArrayList<>();

    public RewardTypeCache() {
        types.addAll(Arrays.asList(RewardTypeDefault.values()));
    }

    /**
     * @param identifier the identifier to check
     * @return the reward type which has the identifier
     */
    public RewardType getByIdentifier(String identifier) {
        for (RewardType type : types) {
            if (type.getIdentifier().equals(identifier)) {
                return type;
            }
        }

        return null;
    }

    /**
     * @return the reward types
     */
    public List<RewardType> getRewards() {
        return types;
    }

    /**
     * @param type the reward type to add
     */
    public void addReward(RewardType type) {
        types.add(type);
    }

    /**
     * @param type the reward type to remove
     */
    public void removeReward(RewardType type) {
        types.remove(type);
    }

}
