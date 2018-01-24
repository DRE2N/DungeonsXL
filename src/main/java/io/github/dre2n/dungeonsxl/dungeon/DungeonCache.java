/*
 * Copyright (C) 2012-2018 Frank Baumann
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

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.dungeonsxl.config.DMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Dungeon instance manager.
 *
 * @author Daniel Saukel
 */
public class DungeonCache {

    private List<Dungeon> dungeons = new ArrayList<>();

    public DungeonCache(File folder) {
        if (!folder.exists()) {
            folder.mkdir();
        }

        for (File file : folder.listFiles()) {
            Dungeon dungeon = new Dungeon(file);

            if (dungeon.isSetupCorrect()) {
                dungeons.add(dungeon);

            } else {
                MessageUtil.log(DMessage.LOG_ERROR_DUNGEON_SETUP.getMessage(file.getName()));
            }
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
        Dungeon dungeon = new Dungeon(Dungeon.getFileFromName(name));
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
