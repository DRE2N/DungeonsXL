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

import io.github.dre2n.commons.config.MessageConfig;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerKickEvent;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class TimeIsRunningTask extends BukkitRunnable {

    MessageConfig messageConfig = DungeonsXL.getInstance().getMessageConfig();

    private DGroup dGroup;
    private int time;
    private int timeLeft;

    public TimeIsRunningTask(DGroup dGroup, int time) {
        this.dGroup = dGroup;
        this.time = time;
        this.timeLeft = time;
    }

    @Override
    public void run() {
        timeLeft--;

        String color = ChatColor.GREEN.toString();

        try {
            color = (double) timeLeft / (double) time > 0.25 ? ChatColor.GREEN.toString() : ChatColor.DARK_RED.toString();

        } catch (ArithmeticException exception) {
            color = ChatColor.DARK_RED.toString();

        } finally {
            for (Player player : dGroup.getPlayers()) {
                MessageUtil.sendActionBarMessage(player, DMessages.PLAYER_TIME_LEFT.getMessage(color, String.valueOf(timeLeft)));

                DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
                if (timeLeft > 0) {
                    continue;
                }

                DPlayerKickEvent dPlayerKickEvent = new DPlayerKickEvent(dPlayer, DPlayerKickEvent.Cause.TIME_EXPIRED);
                Bukkit.getServer().getPluginManager().callEvent(dPlayerKickEvent);

                if (!dPlayerKickEvent.isCancelled()) {
                    MessageUtil.broadcastMessage(DMessages.PLAYER_TIME_KICK.getMessage(player.getName()));
                    dPlayer.leave();
                    if (Game.getByDGroup(dGroup).getRules().getKeepInventoryOnEscape()) {
                        dPlayer.applyRespawnInventory();
                    }
                }

                cancel();
            }
        }

    }

}
