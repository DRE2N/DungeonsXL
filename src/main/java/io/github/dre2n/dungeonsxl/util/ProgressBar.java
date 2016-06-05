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
package io.github.dre2n.dungeonsxl.util;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Daniel Saukel
 */
public class ProgressBar extends BukkitRunnable {

    public static final String BAR = "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588";

    private Set<Player> players = new HashSet<>();
    private int seconds;
    private int secondsLeft;

    public ProgressBar(Set<Player> players, int seconds) {
        this.players = players;
        this.seconds = seconds;
        this.secondsLeft = seconds;
    }

    public ProgressBar(Player player, int seconds) {
        this.players.add(player);
        this.seconds = seconds;
        this.secondsLeft = seconds;
    }

    @Override
    public void run() {
        int i = (int) Math.round(((double) secondsLeft / (double) seconds) * 10);
        StringBuilder bar = new StringBuilder(BAR);
        bar.insert(10 - i, ChatColor.DARK_RED.toString());
        for (Player player : players) {
            MessageUtil.sendActionBarMessage(player, ChatColor.GREEN.toString() + bar.toString());
        }

        if (secondsLeft == 0) {
            cancel();
        } else {
            secondsLeft--;
        }
    }

    /**
     * Send the progress bar to a player
     */
    public static BukkitTask sendProgressBar(Player player, int seconds) {
        return new ProgressBar(player, seconds).runTaskTimer(DungeonsXL.getInstance(), 0, 20);
    }

    /**
     * Send the progress bar to multiple players
     */
    public static BukkitTask sendProgressBar(Set<Player> players, int seconds) {
        return new ProgressBar(players, seconds).runTaskTimer(DungeonsXL.getInstance(), 0, 20);
    }

}
