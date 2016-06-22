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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Saukel
 */
public class Dungeons {

    private List<Dungeon> dungeons = new ArrayList<>();

    public Dungeons() {
        File folder = new File(DungeonsXL.getInstance().getDataFolder() + "/dungeons");

        if (!folder.exists()) {
            folder.mkdir();
        }

        for (File file : folder.listFiles()) {
            dungeons.add(new Dungeon(file));
        }
    }

    /**
     * @param name
     * the name of the Dungeon
     * @return the Dungeon that has the name
     */
    public Dungeon getByName(String name) {
        for (Dungeon dungeon : dungeons) {
            if (dungeon.getName().equalsIgnoreCase(name)) {
                return dungeon;
            }
        }

        return null;
    }

    /**
     * @return the dungeons
     */
    public List<Dungeon> getDungeons() {
        return dungeons;
    }

    /**
     * @param name
     * the name of the Dungeon
     * @return the Dungeon that has the name
     */
    public Dungeon loadDungeon(String name) {
        Dungeon dungeon = new Dungeon(name);
        dungeons.add(dungeon);
        return dungeon;
    }

    /**
     * @param dungeon
     * the dungeon to add
     */
    public void addDungeon(Dungeon dungeon) {
        dungeons.add(dungeon);
    }

    /**
     * @param dungeon
     * the dungeon to remove
     */
    public void removeDungeon(Dungeon dungeon) {
        dungeons.remove(dungeon);
    }

}
