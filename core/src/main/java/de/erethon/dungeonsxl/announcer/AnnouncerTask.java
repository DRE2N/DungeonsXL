/*
 * Copyright (C) 2012-2021 Frank Baumann
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
import de.erethon.dungeonsxl.api.player.GlobalPlayer;
import de.erethon.dungeonsxl.api.player.InstancePlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import java.util.List;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class AnnouncerTask extends BukkitRunnable {

    private DungeonsXL plugin;

    private List<Announcer> announcers;
    private int index;

    public AnnouncerTask(DungeonsXL plugin) {
        this.plugin = plugin;

        this.announcers = plugin.getAnnouncerCache().getAnnouncers();
        index = 0;
    }

    @Override
    public void run() {
        Announcer announcer = announcers.get(index);
        List<String> worlds = announcer.getWorlds();
        for (GlobalPlayer dPlayer : plugin.getPlayerCache()) {
            if (!(dPlayer instanceof InstancePlayer) && ((DGlobalPlayer) dPlayer).isAnnouncerEnabled()) {
                if (worlds.isEmpty() || worlds.contains(dPlayer.getPlayer().getWorld().getName())) {
                    announcer.send(dPlayer.getPlayer());
                }
            }
        }

        index++;
        if (index == announcers.size()) {
            index = 0;
        }
    }

}
