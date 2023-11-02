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
import org.bukkit.event.Event;

/**
 * Superclass for events involving triggers.
 *
 * @author Daniel Saukel
 */
public abstract class TriggerEvent extends Event {

    protected Trigger trigger;

    protected TriggerEvent(Trigger trigger) {
        this.trigger = trigger;
    }

    /**
     * Returns the Trigger involved in this event.
     *
     * @return the trigger involved in this event
     */
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * Sets the trigger involved in this event to the given value.
     *
     * @param trigger the trigger to set
     */
    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

}
