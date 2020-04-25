/*
 * Copyright (C) 2014-2020 Daniel Saukel
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
package de.erethon.dungeonsxl.api.event.world;

import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a {@link ResourceWorld} is instantiated.
 *
 * @author Daniel Saukel
 */
public class ResourceWorldInstantiateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private ResourceWorld resourceWorld;
    private Dungeon dungeon;

    public ResourceWorldInstantiateEvent(ResourceWorld resourceWorld, Dungeon dungeon) {
        this.resourceWorld = resourceWorld;
        this.dungeon = dungeon;
    }

    /**
     * Returns the resource world that is to be instantiated.
     *
     * @return the resource world that is to be instantiated
     */
    public ResourceWorld getResourceWorld() {
        return resourceWorld;
    }

    /**
     * Returns the dungeon as a part of which the instance is loaded.
     *
     * @return the dungeon as a part of which the instance is loaded
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * Returns if the loaded instance will be an edit world.
     *
     * @return if the loaded instance will be an edit world
     */
    public boolean isEditInstance() {
        return dungeon == null;
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
