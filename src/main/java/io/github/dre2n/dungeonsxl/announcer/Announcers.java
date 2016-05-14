/*
 * Copyright (C) 2016 Daniel Saukel
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
package io.github.dre2n.dungeonsxl.announcer;

import io.github.dre2n.commons.util.FileUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.task.AnnouncerTask;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

/**
 * @author Daniel Saukel
 */
public class Announcers {

    private List<Announcer> announcers = new ArrayList<>();

    public Announcers(File file) {
        if (file.isDirectory()) {
            for (File script : FileUtil.getFilesForFolder(file)) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(script);

                String name = script.getName().substring(0, script.getName().length() - 4);
                String identifier = config.getString("identifier");
                List<String> description = config.getStringList("description");
                List<String> worlds = null;
                if (config.contains("worlds")) {
                    worlds = config.getStringList("worlds");
                }
                boolean multiFloor = config.getBoolean("multiFloor");
                short maxGroupsPerGame = (short) config.getInt("maxGroupsPerGame");
                int maxPlayersPerGroup = config.getInt("maxPlayersPerGroup");

                announcers.add(new Announcer(name, description, worlds, identifier, multiFloor, maxGroupsPerGame, maxPlayersPerGroup));
            }
        }
    }

    /**
     * @return the announcer that has the name
     */
    public Announcer getByName(String name) {
        for (Announcer announcer : announcers) {
            if (announcer.getName().equals(name)) {
                return announcer;
            }
        }

        return null;
    }

    /**
     * @return the announcer that has the GUI
     */
    public Announcer getByGUI(Inventory gui) {
        for (Announcer announcer : announcers) {
            if ((ChatColor.DARK_RED + announcer.getName()).equals(gui.getTitle())) {
                return announcer;
            }
        }

        return null;
    }

    /**
     * @return the announcers
     */
    public List<Announcer> getAnnouncers() {
        return announcers;
    }

    /**
     * @param announcer
     * the Announcer to add
     */
    public void addAnnouncer(Announcer announcer) {
        announcers.add(announcer);
    }

    /**
     * @param announcer
     * the Announcer to remove
     */
    public void removeAnnouncer(Announcer announcer) {
        announcers.remove(announcer);
    }

}
