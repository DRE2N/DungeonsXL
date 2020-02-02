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
package de.erethon.dungeonsxl.world;

import de.erethon.dungeonsxl.DungeonsXL;
import java.util.Set;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class WorldUnloadTask extends BukkitRunnable {

    private DungeonsXL plugin;

    public WorldUnloadTask(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Set<DInstanceWorld> instances = plugin.getDWorldCache().getInstances();
        for (DInstanceWorld instance : instances.toArray(new DInstanceWorld[instances.size()])) {
            if (instance.exists()) {
                if (instance.getPlayers().isEmpty()) {
                    instance.delete();
                }
            }
        }
    }

}
