/*
 * Copyright (C) 2014-2022 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api.event.group;

import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.player.GamePlayer;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a group collects a reward.
 * <p>
 * In the default implementation, this happens when a player opens a reward chest.
 *
 * @author Daniel Saukel
 */
public class GroupCollectRewardEvent extends GroupEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private GamePlayer collector;
    private Reward reward;

    public GroupCollectRewardEvent(PlayerGroup group, GamePlayer collector, Reward reward) {
        super(group);
        this.collector = collector;
        this.reward = reward;
    }

    /**
     * Returns the player who collected the reward.
     * <p>
     * Note that this may be null if addons add a way to give rewards that cannot be attributed to one collector.
     *
     * @return the player who collected the reward
     */
    public GamePlayer getCollector() {
        return collector;
    }

    /**
     * Returns the reward the group collected.
     *
     * @return the reward the group collected
     */
    public Reward getReward() {
        return reward;
    }

    /**
     * Sets the reward the group collected.
     *
     * @param reward the reward
     */
    public void setReward(Reward reward) {
        this.reward = reward;
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{group=" + group + "; collector=" + collector + "; reward=" + reward + "}";
    }

}
