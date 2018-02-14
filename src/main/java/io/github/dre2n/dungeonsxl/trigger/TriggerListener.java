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
package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Milan Albrecht, Daniel Saukel
 */
public class TriggerListener implements Listener {

    @EventHandler
    public void onRedstoneEvent(final BlockRedstoneEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (DGameWorld gameWorld : DungeonsXL.getInstance().getDWorlds().getGameWorlds()) {
                    if (event.getBlock().getWorld() == gameWorld.getWorld()) {
                        RedstoneTrigger.updateAll(gameWorld);
                    }
                }
            }
        }.runTaskLater(DungeonsXL.getInstance(), 1);
    }

}
