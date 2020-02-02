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
package de.erethon.dungeonsxl.api.world;

import org.bukkit.block.Block;

/**
 * A raw resource world instance to edit the dungeon map. There is never more than one edit world per resource world.
 * <p>
 * An edit world is not equal to a {@link de.erethon.dungeonsxl.api.dungeon.Dungeon}.
 *
 * @author Daniel Saukel
 */
public interface EditWorld extends InstanceWorld {

    /**
     * Registers the block as a {@link de.erethon.dungeonsxl.api.sign.DungeonSign} sothat it can later be saved persistently.
     *
     * @param block a DungeonSign block
     */
    void registerSign(Block block);

    /**
     * Saves the sign data and overrides the resource with the changes.
     */
    void save();

    @Override
    default void delete() {
        delete(true);
    }

    /**
     * Deletes this edit instance.
     *
     * @param save whether this world should be {@link #save()}ed
     */
    void delete(boolean save);

}
