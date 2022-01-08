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
package de.erethon.dungeonsxl.api.event.world;

import de.erethon.dungeonsxl.api.world.ResourceWorld;
import org.bukkit.event.Event;

/**
 * Superclass for events involving DungeonsXL resource worlds.
 *
 * @author Daniel Saukel
 */
public abstract class ResourceWorldEvent extends Event {

    protected ResourceWorld resource;

    protected ResourceWorldEvent(ResourceWorld resource) {
        this.resource = resource;
    }

    /**
     * Returns the resource world involved in this event.
     *
     * @return the resource world involved in this event.
     */
    public ResourceWorld getResource() {
        return resource;
    }

}
