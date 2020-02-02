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
package de.erethon.dungeonsxl.player;

import de.erethon.commons.player.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class RespawnTask extends BukkitRunnable {

    private Player player;
    private Location location;

    public RespawnTask(Player player, Location location) {
        this.location = location;
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            return;
        }
        if (player.getWorld() != location.getWorld() || player.getLocation().distance(location) > 2) {
            PlayerUtil.secureTeleport(player, location);
        }
    }

}
