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
package io.github.dre2n.dungeonsxl.util.worldloader;

import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * A custom thread safe world loader.
 *
 * @author Daniel Saukel
 */
public class WorldLoader {

    static InternalsProvider internals;

    static {
        switch (CompatibilityHandler.getInstance().getInternals()) {
            case v1_10_R1:
                internals = new v1_10_R1();
                break;
        }
    }

    /**
     * @param creator
     * the WorldCreator which stores the information to create the new world
     * @return
     * the new World
     */
    public static World createWorld(WorldCreator creator) {
        return internals.createWorld(creator);
    }

}
