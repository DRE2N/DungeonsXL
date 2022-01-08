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

import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a group finishs a dungeon floor.
 *
 * @author Daniel Saukel
 */
public class GroupFinishFloorEvent extends GroupEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private GameWorld finished;
    private ResourceWorld next;

    public GroupFinishFloorEvent(PlayerGroup group, GameWorld finished, ResourceWorld next) {
        super(group);
        this.finished = finished;
        this.next = next;
    }

    /**
     * Returns the game world that was just finished.
     *
     * @return the game world that was just finished
     */
    public GameWorld getFinished() {
        return finished;
    }

    /**
     * Returns the resource world of the next floor.
     *
     * @return the resource world of the next floor
     */
    public ResourceWorld getNext() {
        return next;
    }

    /**
     * Sets the next floor to load.
     * <p>
     * If one has already been loaded because another group finished the floor earlier, this will not do anything.
     *
     * @param next the next floor to load
     */
    public void setNext(ResourceWorld next) {
        this.next = next;
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
        return getClass().getSimpleName() + "{group=" + group + "; finished=" + finished + "; next=" + next + "}";
    }

}
