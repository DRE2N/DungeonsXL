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
package de.erethon.dungeonsxl.api.event.mob;

import de.erethon.dungeonsxl.api.mob.DungeonMob;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a {@link DungeonMob} dies.
 *
 * @author Daniel Saukel
 */
public class DungeonMobDeathEvent extends DungeonMobEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public DungeonMobDeathEvent(DungeonMob mob) {
        super(mob);
    }

    /**
     * Returns the player who killed the mob or null if the cause of its death was not a player.
     *
     * @return the player who killed the mob or null if the cause of its death was not a player
     */
    public Player getKiller() {
        if (mob.getEntity() == null) {
            return null;
        }
        return mob.getEntity().getKiller();
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
        return getClass().getSimpleName() + "{mob=" + mob + "}";
    }

}
