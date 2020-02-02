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
package de.erethon.dungeonsxl.announcer;

import de.erethon.dungeonsxl.DungeonsXL;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.InventoryView;

/**
 * Announcer instance manager.
 *
 * @author Daniel Saukel
 */
public class AnnouncerCache {

    private DungeonsXL plugin;

    private List<Announcer> announcers = new ArrayList<>();

    public AnnouncerCache(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    public void init(File folder) {
        if (!folder.exists()) {
            folder.mkdir();
        }
        for (File file : folder.listFiles()) {
            addAnnouncer(new Announcer(plugin, file));
        }
        if (!announcers.isEmpty()) {
            new AnnouncerTask(plugin).runTaskTimer(plugin, plugin.getMainConfig().getAnnouncmentInterval(), plugin.getMainConfig().getAnnouncmentInterval());
        }
        Bukkit.getPluginManager().registerEvents(new AnnouncerListener(plugin), plugin);
    }

    /**
     * @param name the name
     * @return the announcer that has the name
     */
    public Announcer getByName(String name) {
        for (Announcer announcer : announcers) {
            if (announcer.getName().equalsIgnoreCase(name)) {
                return announcer;
            }
        }

        return null;
    }

    /**
     * @param gui the gui
     * @return the announcer that has the GUI
     */
    public Announcer getByGUI(InventoryView gui) {
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
     * @param announcer the Announcer to add
     */
    public void addAnnouncer(Announcer announcer) {
        announcers.add(announcer);
    }

    /**
     * @param announcer the Announcer to remove
     */
    public void removeAnnouncer(Announcer announcer) {
        announcers.remove(announcer);
    }

}
