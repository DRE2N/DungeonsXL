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
import org.bukkit.event.HandlerList;

/**
 * Fired when a player starts editing a dungeon map.
 *
 * @author Daniel Saukel
 */
public class EditPlayerEditEvent extends EditPlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private boolean newlyLoaded;

    public EditPlayerEditEvent(EditPlayer editPlayer, boolean newlyLoaded) {
        super(editPlayer);
        this.newlyLoaded = newlyLoaded;
    }

    /**
     * Returns true if the edit world was not instantiated before the player edited it and false if it was.
     *
     * @return true if the edit world was not instantiated before the player edited it and false if it was
     */
    public boolean isNewlyLoaded() {
        return newlyLoaded;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{player=" + globalPlayer + "; newlyLoaded=" + newlyLoaded + "}";
    }

}
