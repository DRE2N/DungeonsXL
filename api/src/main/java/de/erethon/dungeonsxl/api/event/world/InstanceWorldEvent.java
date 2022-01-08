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

import de.erethon.dungeonsxl.api.world.InstanceWorld;
import org.bukkit.World;
import org.bukkit.event.Event;

/**
 * Superclass for events involving DungeonsXL instances.
 *
 * @author Daniel Saukel
 */
public abstract class InstanceWorldEvent extends Event {

    protected InstanceWorld instance;

    protected InstanceWorldEvent(InstanceWorld instance) {
        this.instance = instance;
    }

    /**
     * Returns the instance involved in this event.
     *
     * @return the instance involved in this event
     */
    public InstanceWorld getInstance() {
        return instance;
    }

    /**
     * Returns the Bukkit world involved in this event.
     *
     * @return the Bukkit world involved in this event.
     */
    public World getBukkitWorld() {
        return instance.getWorld();
    }

}
