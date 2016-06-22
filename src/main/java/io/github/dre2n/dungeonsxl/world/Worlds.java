/*
 * Copyright (C) 2012-2016 Frank Baumann
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
package io.github.dre2n.dungeonsxl.world;

import java.io.File;
import java.util.Set;

/**
 * @author Daniel Saukel
 */
public class Worlds {

    /*private Set<ResourceWorld> resourceWorlds;

    public Worlds(File folder) {
        for (File file : folder.listFiles()) {
            resourceWorlds.add(new ResourceWorld());
        }
    }*/

    @Deprecated
    public static boolean exists(String name) {
        for (File world : io.github.dre2n.dungeonsxl.DungeonsXL.MAPS.listFiles()) {
            if (world.isDirectory() && world.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

}
