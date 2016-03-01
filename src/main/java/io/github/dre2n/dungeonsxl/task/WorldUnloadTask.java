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
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class WorldUnloadTask extends BukkitRunnable {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();

    @Override
    public void run() {
        for (GameWorld gameWorld : plugin.getGameWorlds()) {
            if (gameWorld.getWorld().getPlayers().isEmpty()) {
                if (DPlayer.getByWorld(gameWorld.getWorld()).isEmpty()) {
                    gameWorld.delete();
                }
            }
        }

        for (EditWorld editWorld : plugin.getEditWorlds()) {
            if (editWorld.getWorld().getPlayers().isEmpty()) {
                editWorld.delete();
            }
        }
    }

}
