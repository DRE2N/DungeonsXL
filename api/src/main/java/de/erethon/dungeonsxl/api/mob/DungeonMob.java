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
package de.erethon.dungeonsxl.api.mob;

import de.erethon.caliburn.mob.ExMob;
import org.bukkit.entity.LivingEntity;

/**
 * Wrapper for a mob spawned in a dungeon.
 *
 * @author Daniel Saukel
 */
public interface DungeonMob {

    /**
     * Returns the entity that is wrapped by this object.
     *
     * @return the entity that is wrapped by this object
     */
    LivingEntity getEntity();

    /**
     * Returns the Caliburn representation of the mob or null if it is spawned by an external plugin.
     *
     * @return the Caliburn representation of the mob or null if it is spawned by an external plugin
     */
    ExMob getType();

    /**
     * Returns if the mob is spawned by an external plugin.
     *
     * @return if the mob is spawned by an external plugin
     */
    default boolean isExternalMob() {
        return getType() == null;
    }

    /**
     * Returns the String used to identify this mob for example in the context of triggers.
     *
     * @return the String used to identify this mob for example in the context of triggers
     */
    String getTriggerId();

}
