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
package de.erethon.dungeonsxl.dungeon;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.world.DResourceWorld;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Dungeon instance manager.
 *
 * @author Daniel Saukel
 */
public class DungeonCache {

    private DungeonsXL plugin;

    private List<Dungeon> dungeons = new ArrayList<>();

    public DungeonCache(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    public void init(File folder) {
        if (!folder.exists()) {
            folder.mkdir();
        }

        for (File file : folder.listFiles()) {
            Dungeon dungeon = new Dungeon(plugin, file);

            if (dungeon.isSetupCorrect()) {
                dungeons.add(dungeon);

            } else {
                MessageUtil.log(plugin, "&4The setup of dungeon &6" + file.getName()
                        + "&4 is incorrect. See https://github.com/DRE2N/DungeonsXL/wiki/dungeon-configuration for reference.");
            }
        }
    }

    /**
     * @param name the name of the Dungeon
     * @return the Dungeon that has the name
     */
    public Dungeon getByName(String name) {
        return getByName(name, false);
    }

    /**
     * @param name       the name of the Dungeon
     * @param artificial if artificial Dungeons shall be included in the check
     * @return the Dungeon that has the name
     */
    public Dungeon getByName(String name, boolean artificial) {
        for (Dungeon dungeon : dungeons) {
            if (dungeon.getName().equalsIgnoreCase(name)) {
                return dungeon;
            }
        }

        if (artificial) {
            DResourceWorld resource = plugin.getDWorldCache().getResourceByName(name);
            if (resource != null) {
                return new Dungeon(plugin, resource);
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
     * @param name the name of the Dungeon
     * @return the Dungeon that has the name
     */
    public Dungeon loadDungeon(String name) {
        Dungeon dungeon = new Dungeon(plugin, Dungeon.getFileFromName(name));
        dungeons.add(dungeon);
        return dungeon;
    }

    /**
     * @param dungeon the dungeon to add
     */
    public void addDungeon(Dungeon dungeon) {
        dungeons.add(dungeon);
    }

    /**
     * @param dungeon the dungeon to remove
     */
    public void removeDungeon(Dungeon dungeon) {
        dungeons.remove(dungeon);
    }

}
