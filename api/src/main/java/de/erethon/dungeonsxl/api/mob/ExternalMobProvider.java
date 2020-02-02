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

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Other plugins / libraries that can handle and spawn mobs.
 *
 * @author Daniel Saukel
 */
public interface ExternalMobProvider {

    /**
     * Returns the identifier used on mob signs to spawn mobs from this provider.
     *
     * @return the identifier used on mob signs to spawn mobs from this provider
     */
    String getIdentifier();

    /**
     * Returns the raw console spawn command of the provider.
     * <p>
     * This method is necessary for the default implementation of {@link #getCommand(String, String, double, double, double)}.
     *
     * @return the raw console spawn command of the provider
     */
    String getRawCommand();

    /**
     * Returns the console spawn command of the provider with values replaced to spawn the mob represented by the given String.
     * <p>
     * The default implementation uses %mob%, %world%, %x%, %y% and %z% as placeholders and alternatively %block_x% etc. if values without decimals are needed.
     * <p>
     * This method is used in the default implementation of {@link #summon(String, org.bukkit.Location)}.
     *
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
     * Summons the mob.
     * <p>
     * The default implementation requires {@link #getCommand(String, String, double, double, double)} to be implemented.
     *
     * @param mob      the mob identifier
     * @param location the location where the mob will be spawned
     */
    default void summon(String mob, Location location) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), getCommand(mob, location.getWorld().getName(), location.getX(), location.getY(), location.getZ()));
    }

}
