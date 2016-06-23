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
package io.github.dre2n.dungeonsxl.task;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class WorldUnloadTask extends BukkitRunnable {

    DungeonsXL plugin = DungeonsXL.getInstance();

    @Override
    public void run() {
        for (GameWorld gameWorld : plugin.getWorlds().getGameWorlds()) {
            if (gameWorld.getWorld().getPlayers().isEmpty()) {
                if (DGamePlayer.getByWorld(gameWorld.getWorld()).isEmpty()) {
                    gameWorld.delete();
                }
            }
        }

        for (EditWorld editWorld : plugin.getWorlds().getEditWorlds()) {
            if (editWorld.getWorld().getPlayers().isEmpty()) {
                editWorld.delete(true);
            }
        }
    }

}
