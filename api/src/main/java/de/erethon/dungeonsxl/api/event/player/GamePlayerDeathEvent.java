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

import de.erethon.dungeonsxl.api.player.GamePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player dies in a dungeon. This is also fired when a player does not technically die because the deathScreen rule prevented the death.
 *
 * @author Daniel Saukel
 */
public class GamePlayerDeathEvent extends GamePlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private boolean keepInventory;
    private int lostLives;

    public GamePlayerDeathEvent(GamePlayer gamePlayer, boolean keepInventory, int lostLives) {
        super(gamePlayer);
        this.lostLives = lostLives;
        this.keepInventory = keepInventory;
    }

    /**
     * If the player's state - including his inventory, EXP etc. - is kept.
     *
     * @return if the player's state is kept
     */
    public boolean isInventoryKept() {
        return keepInventory;
    }

    /**
     * Sets if the player's state - including his inventory, EXP etc. - is kept.
     *
     * @param keepInventory if the player's state is kept
     */
    public void setInventoryKept(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    /**
     * Returns the amount of lives the player loses.
     *
     * @return the amount of lives the player loses
     */
    public int getLostLives() {
        return lostLives;
    }

    /**
     * Sets the amount of lives the player loses.
     *
     * @param lostLives the lives the player loses
     */
    public void setLostLives(int lostLives) {
        this.lostLives = lostLives;
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
        return getClass().getSimpleName() + "{player=" + globalPlayer + "; keepInventory=" + keepInventory + "; lostLives=" + lostLives + "}";
    }

}
