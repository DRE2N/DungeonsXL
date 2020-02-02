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

import de.erethon.dungeonsxl.DungeonsXL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class SecureModeTask extends BukkitRunnable {

    private DungeonsXL plugin;

    public SecureModeTask(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            DGlobalPlayer dGlobalPlayer = plugin.getDPlayerCache().getByPlayer(player);
            if (dGlobalPlayer == null) {
                dGlobalPlayer = new DGlobalPlayer(plugin, player);
            }

            if (!(dGlobalPlayer instanceof DInstancePlayer)) {
                if (player.getWorld().getName().startsWith("DXL_Game_") | player.getWorld().getName().startsWith("DXL_Edit_") && !DPermission.hasPermission(player, DPermission.INSECURE)) {
                    player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
            }
        }
    }

}
