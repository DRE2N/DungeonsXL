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

import de.erethon.dungeonsxl.api.player.EditPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player stops editing a dungeon map.
 *
 * @author Daniel Saukel
 */
public class EditPlayerLeaveEvent extends EditPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private boolean escape;
    private boolean unloadIfEmpty;

    public EditPlayerLeaveEvent(EditPlayer editPlayer, boolean escape, boolean unloadIfEmpty) {
        super(editPlayer);
        this.escape = escape;
        this.unloadIfEmpty = unloadIfEmpty;
    }

    /**
     * Returns false if the edit world is saved, true if not.
     *
     * @return false if the edit world is saved, true if not
     */
    public boolean isEscape() {
        return escape;
    }

    /**
     * Returns if the instance shall be unloaded when it is empty after the player left.
     *
     * @return if the instance shall be unloaded when it is empty after the player left
     */
    public boolean getUnloadIfEmpty() {
        return unloadIfEmpty;
    }

    /**
     * Sets if the instance shall be unloaded when it is empty after the player left.
     *
     * @param unloadIfEmpty if the instance shall be unloaded when it is empty after the player left
     */
    public void setUnloadIfEmpty(boolean unloadIfEmpty) {
        this.unloadIfEmpty = unloadIfEmpty;
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
        return getClass().getSimpleName() + "{player=" + globalPlayer + "; escape=" + escape + "; unloadIfEmpty=" + unloadIfEmpty + "}";
    }

}
