/*
 * Copyright (C) 2012-2017 Frank Baumann
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
package io.github.dre2n.dungeonsxl.reward;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class ItemReward extends Reward {

    private RewardType type = RewardTypeDefault.ITEM;

    private List<ItemStack> items = new ArrayList<>();

    /* Getters and setters */
    /**
     * @return the reward items
     */
    public ItemStack[] getItems() {
        return items.toArray(new ItemStack[items.size()]);
    }

    /**
     * @param items
     * the reward items to set
     */
    public void setItems(ItemStack[] items) {
        this.items = Arrays.asList(items);
    }

    /**
     * @param items
     * the reward items to add
     */
    public void addItems(ItemStack... items) {
        this.items.addAll(Arrays.asList(items));
    }

    /**
     * @param items
     * the reward items to remove
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
        if (items.size() <= 54) {
            new DLootInventory(player, getItems());

        } else {
            new DLootInventory(player, items.subList(0, 54).toArray(new ItemStack[54]));
            DungeonsXL.getInstance().getDPlayers().getByPlayer(player).setRewardItems(new CopyOnWriteArrayList<>(items.subList(54, items.size())));
            MessageUtil.sendMessage(player, DMessage.ERROR_TOO_MANY_REWARDS.getMessage());
        }
    }

}
