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
package de.erethon.dungeonsxl.event.dmob;

import de.erethon.dungeonsxl.mob.DMob;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * @author Daniel Saukel
 */
public class DMobDeathEvent extends DMobEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private EntityDeathEvent bukkitEvent;

    public DMobDeathEvent(DMob dMob, EntityDeathEvent bukkitEvent) {
        super(dMob);
        this.bukkitEvent = bukkitEvent;
    }

    /**
     * @return the bukkitEvent
     */
    public EntityDeathEvent getBukkitEvent() {
        return bukkitEvent;
    }

    /**
     * @param bukkitEvent the bukkitEvent to set
     */
    public void setBukkitEvent(EntityDeathEvent bukkitEvent) {
        this.bukkitEvent = bukkitEvent;
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
