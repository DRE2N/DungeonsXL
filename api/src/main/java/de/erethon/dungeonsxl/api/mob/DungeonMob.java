/*
 * Copyright (C) 2015-2026 Daniel Saukel
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

import de.erethon.xlib.mob.ExMob;
import java.util.Collection;
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
     * Returns the XLib representation of the mob or null if it is spawned by an external plugin.
     *
     * @return the XLib representation of the mob or null if it is spawned by an external plugin
     */
    ExMob getType();

    /**
     * Returns the {@link MobSet} the mob is automatically added to for being of the associated type.
     * <p>
     * E.g. for a zombie, this returns a set with the {@link MobSet#getId() ID} "ZOMBIE".
     *
     * @return the {@link MobSet} the mob is automatically added to for being of the associated typ
     */
    MobSet getTypeMobSet();

    /**
     * Returns a Collection of the {@link MobSet}s that this mob is a part of.
     * <p>
     * This includes the {@link #getTypeMobSet() type set} and the {@link de.erethon.dungeonsxl.api.world.GameWorld#getAllMobSet() generic set}.
     *
     * @return a Collection of the {@link MobSet}s that this mob is a part of,
     */
    Collection<MobSet> getMobSets();

    /**
     * Adds a {@link MobSet} to this mob.
     *
     * @param mobSet the mob set to add
     * @throws IllegalArgumentException if the mob set is not registered in the {@link de.erethon.dungeonsxl.api.world.GameWorld}
     * @return if adding the mob was successful
     */
    boolean addMobSet(MobSet mobSet);

    /**
     * Removes a {@link MobSet} from this mob.
     *
     * @param mobSet the mob set to remove
     * @return if removing the mob was successful
     */
    boolean removeMobSet(MobSet mobSet);

}
