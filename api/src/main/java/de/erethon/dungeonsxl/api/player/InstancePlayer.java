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
package de.erethon.dungeonsxl.api.player;

import org.bukkit.World;

/**
 * Represents a player in an instance.
 * <p>
 * All players in a world instantiated by DungeonsXL, have one wrapper object that is an instance of InstancePlayer.
 *
 * @author Daniel Saukel
 */
public interface InstancePlayer extends GlobalPlayer {

    /**
     * The world of the instance, where the player is supposed to be.
     *
     * @return the world of the instance
     */
    World getWorld();

    /**
     * Makes the player leave his group and dungeon.
     */
    void leave();

}
