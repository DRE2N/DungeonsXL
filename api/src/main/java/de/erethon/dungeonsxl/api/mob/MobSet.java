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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.LivingEntity;

/**
 * Mobs spawned through a mob sign are added to mob sets. This allows for all mobs in a set to be referred to together with the ID of the mob.
 *
 * @author Daniel Saukel
 */
public class MobSet {

    /**
     * Identifier of the generic set that includes all mobs in a {@link de.erethon.dungeonsxl.api.world.GameWorld}.
     */
    public static final String ALL = "ALL";

    private String id;
    private List<LivingEntity> spawned = new ArrayList<>();
    private int reserved;
    private int killed;

    /**
     * Use {@link de.erethon.dungeonsxl.api.world.GameWorld#getOrCreateMobSet(String)} to instantiate a MobSet.
     *
     * @param id the set's identifier.
     */
    public MobSet(String id) {
        this.id = id;
    }

    // TODO: JavaDocs
    public String getId() {
        return id;
    }

    public int getSizeSpawned() {
        return spawned.size();
    }

    public int getSizeReserved() {
        return reserved;
    }

    public void allocate(int amount) {
        reserved += amount;
    }

    public int getKilled() {
        return killed;
    }

    public void spawn(LivingEntity entity) {
        spawned.add(entity);
    }

    public void kill(LivingEntity entity) {
        spawned.remove(entity);
        killed++;
    }

    public boolean checkTrigger(double quota) {
        return killed >= reserved * quota;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", reserved=" + reserved + "}";
    }

}
