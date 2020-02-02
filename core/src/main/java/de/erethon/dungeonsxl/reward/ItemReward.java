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

import de.erethon.dungeonsxl.DungeonsXL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class ItemReward extends Reward {

    private RewardType type = RewardTypeDefault.ITEM;

    private List<ItemStack> items = new ArrayList<>();

    public ItemReward(DungeonsXL plugin) {
        super(plugin);
    }

    /* Getters and setters */
    /**
     * @return the reward items
     */
    public List<ItemStack> getItems() {
        return items;
    }

    /**
     * @param items the reward items to set
     */
    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    /**
     * @param items the reward items to add
     */
    public void addItems(ItemStack... items) {
        this.items.addAll(Arrays.asList(items));
    }

    /**
     * @param items the reward items to remove
     */
    public void removeItems(ItemStack... items) {
        this.items.addAll(Arrays.asList(items));
    }

    @Override
    public RewardType getType() {
        return type;
    }

    /* Actions */
    @Override
    public void giveTo(Player player) {
        plugin.getDPlayerCache().getByPlayer(player).setRewardItems(items);
    }

}
