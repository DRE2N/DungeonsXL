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
package de.erethon.dungeonsxl.api.event.player;

import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.player.GlobalPlayer;
import java.util.List;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player gets his {@link Reward}s after finishing a game.
 * <p>
 * @see GlobalPlayer#setRewardItems(java.util.List)
 * @author Daniel Saukel
 */
public class GlobalPlayerRewardPayOutEvent extends GlobalPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final List<Reward> rewards;

    public GlobalPlayerRewardPayOutEvent(GlobalPlayer globalPlayer, List<Reward> rewards) {
        super(globalPlayer);
        this.rewards = rewards;
    }

    /**
     * Returns a list of the rewards the player will get.
     *
     * @return a list of the rewards the player will get
     */
    public List<Reward> getRewards() {
        return rewards;
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
        return getClass().getSimpleName() + "{player=" + globalPlayer + "; rewards=" + rewards + "}";
    }

}
