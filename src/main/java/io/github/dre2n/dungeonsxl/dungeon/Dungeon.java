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
package io.github.dre2n.dungeonsxl.dungeon;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DungeonConfig;
import java.io.File;

/**
 * @author Daniel Saukel
 */
public class Dungeon {

    private String name;
    private DungeonConfig config;

    public Dungeon(File file) {
        this.name = file.getName().replaceAll(".yml", "");
        this.config = new DungeonConfig(file);
    }

    public Dungeon(String name) {
        this.name = name;

        File file = new File(DungeonsXL.getInstance().getDataFolder() + "/dungeons", name + ".yml");
        if (file.exists()) {
            this.config = new DungeonConfig(file);
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the config
     */
    public DungeonConfig getConfig() {
        return config;
    }

    /**
     * @return if this dungeon has multiple floors
     */
    public boolean isMultiFloor() {
        return config != null;
    }

}
