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
package io.github.dre2n.dungeonsxl.task;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.announcer.Announcer;
import io.github.dre2n.dungeonsxl.announcer.Announcers;
import io.github.dre2n.dungeonsxl.player.DInstancePlayer;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class AnnouncerTask extends BukkitRunnable {

    private List<Announcer> announcers;
    int index;

    public AnnouncerTask(Announcers announcers) {
        this.announcers = announcers.getAnnouncers();
        index = 0;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(DungeonsXL.getInstance().getDPlayers().getByPlayer(player) instanceof DInstancePlayer)) {
                announcers.get(index).send(player);
            }
        }

        index++;
        if (index == announcers.size()) {
            index = 0;
        }
    }

}
