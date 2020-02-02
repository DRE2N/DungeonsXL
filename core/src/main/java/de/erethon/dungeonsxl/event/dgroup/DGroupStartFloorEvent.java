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
package de.erethon.dungeonsxl.event.dgroup;

import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * @author Daniel Saukel
 */
public class DGroupStartFloorEvent extends DGroupEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private DGameWorld gameWorld;

    public DGroupStartFloorEvent(DGroup dGroup, DGameWorld gameWorld) {
        super(dGroup);
        this.gameWorld = gameWorld;
    }

    /**
     * @return the gameWorld
     */
    public DGameWorld getGameWorld() {
        return gameWorld;
    }

    /**
     * @param gameWorld the gameWorld to set
     */
    public void setGameWorld(DGameWorld gameWorld) {
        this.gameWorld = gameWorld;
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
