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
package de.erethon.dungeonsxl.event.dplayer.instance.game;

import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.reward.Reward;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * @author Daniel Saukel
 */
public class DGamePlayerRewardEvent extends DGamePlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private List<Reward> rewards = new ArrayList<>();
    private List<Player> excludedPlayers = new ArrayList<>();

    public DGamePlayerRewardEvent(DGamePlayer dPlayer) {
        super(dPlayer);
        this.rewards = dPlayer.getDGroup().getRewards();
    }

    /**
     * @return the rewards
     */
    public List<Reward> getRewards() {
        return rewards;
    }

    /**
     * @param reward the reward to add
     */
    public void addRewards(Reward reward) {
        rewards.add(reward);
    }

    /**
     * @param reward the reward to remove
     */
    public void removeRewards(Reward reward) {
        rewards.remove(reward);
    }

    /**
     * @return the excludedPlayers
     */
    public List<Player> getExcludedPlayers() {
        return excludedPlayers;
    }

    /**
     * @param player the player to add
     */
    public void addExcludedPlayer(Player player) {
        excludedPlayers.add(player);
    }

    /**
     * @param player the player to remove
     */
    public void removeExcludedPlayer(Player player) {
        excludedPlayers.remove(player);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
