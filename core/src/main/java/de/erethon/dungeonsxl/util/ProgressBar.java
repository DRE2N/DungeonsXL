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
package de.erethon.dungeonsxl.util;

import de.erethon.commons.chat.MessageUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * A boss bar based progress bar.
 *
 * @author Daniel Saukel
 */
public class ProgressBar extends BukkitRunnable {

    public static final String BAR = "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588";

    private List<UUID> players = new ArrayList<>();
    private int seconds;
    private int secondsLeft;

    public ProgressBar(Collection<Player> players, int seconds) {
        for (Player player : players) {
            this.players.add(player.getUniqueId());
        }
        this.seconds = seconds;
        this.secondsLeft = seconds;
    }

    public ProgressBar(Player player, int seconds) {
        this.players.add(player.getUniqueId());
        this.seconds = seconds;
        this.secondsLeft = seconds;
    }

    /**
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }

    /**
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    @Override
    public void run() {
        int i = (int) Math.round(((double) secondsLeft / (double) seconds) * 10);
        StringBuilder bar = new StringBuilder(BAR);
        bar.insert(10 - i, ChatColor.DARK_RED.toString());
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                MessageUtil.sendActionBarMessage(player, ChatColor.GREEN.toString() + bar.toString());
            }
        }

        if (secondsLeft == 0) {
            onFinish();
            cancel();
        } else {
            secondsLeft--;
        }
    }

    /**
     * Method to override to set actions when no seconds are left.
     */
    public void onFinish() {
    }

    /**
     * Sends the progress bar to a player
     *
     * @param plugin the plugin instance
     * @return the scheduled BukkitTask
     */
    public BukkitTask send(Plugin plugin) {
        return runTaskTimer(plugin, 0L, 20L);
    }

}
