/*
 * Copyright (C) 2014-2023 Daniel Saukel
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
package de.erethon.dungeonsxl.api.event.trigger;

import de.erethon.dungeonsxl.api.trigger.Trigger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Fired when a {@link Trigger} is created.
 *
 * @author Daniel Saukel
 */
public class TriggerRegistrationEvent extends TriggerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public TriggerRegistrationEvent(Trigger trigger) {
        super(trigger);
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
        return getClass().getSimpleName() + "{trigger=" + trigger + "}";
    }

}
