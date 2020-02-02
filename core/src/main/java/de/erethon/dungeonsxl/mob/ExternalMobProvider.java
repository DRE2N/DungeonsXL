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
package de.erethon.dungeonsxl.mob;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Implement this to create custom ExternalMobProviders.
 *
 * @author Daniel Saukel
 */
public interface ExternalMobProvider {

    /**
     * @return the name of the provider plugin
     */
    String getIdentifier();

    /**
     * @return the raw command without replaced variables
     */
    String getRawCommand();

    /**
     * @param mob   the mob identifier
     * @param world the game world
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @return the command with replaced variables
     */
    default String getCommand(String mob, String world, double x, double y, double z) {
        return getRawCommand().replace("%mob%", mob).replace("%world%", world)
                .replace("%x%", String.valueOf(x)).replace("%y%", String.valueOf(y)).replace("%z%", String.valueOf(z))
                .replace("%block_x%", String.valueOf(Location.locToBlock(x)))
                .replace("%block_y%", String.valueOf(Location.locToBlock(y)))
                .replace("%block_z%", String.valueOf(Location.locToBlock(z)));
    }

    /**
     * @param mob      the mob identifier
     * @param location the location where the mob will be spawned
     */
    default void summon(String mob, Location location) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), getCommand(mob, location.getWorld().getName(), location.getX(), location.getY(), location.getZ()));
    }

}
