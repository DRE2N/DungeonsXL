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
package io.github.dre2n.dungeonsxl.player;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.dungeonsxl.game.GameType;
import io.github.dre2n.dungeonsxl.world.DEditWorld;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import io.github.dre2n.dungeonsxl.world.DInstanceWorld;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class CreateDInstancePlayerTask extends BukkitRunnable {

    public static final String BAR = "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588";

    private UUID player;
    private DInstanceWorld instance;
    private GameType ready;

    private int i = 12;

    public CreateDInstancePlayerTask(Player player, DInstanceWorld instance) {
        this.player = player.getUniqueId();
        this.instance = instance;
    }

    public CreateDInstancePlayerTask(Player player, DInstanceWorld instance, GameType ready) {
        this.player = player.getUniqueId();
        this.instance = instance;
        this.ready = ready;
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null || !player.isOnline()) {
            cancel();
            return;
        }

        if (instance.exists()) {
            if (instance instanceof DGameWorld) {
                DGamePlayer gamePlayer = new DGamePlayer(player, (DGameWorld) instance);
                if (ready != null) {
                    gamePlayer.ready(ready);
                }

            } else if (instance instanceof DEditWorld) {
                new DEditPlayer(player, (DEditWorld) instance);
            }

            cancel();
            return;
        }

        StringBuilder bar = new StringBuilder(BAR);
        int pos = i;
        if (bar.length() - pos < 0) {
            pos = bar.length();
        }
        bar.insert(bar.length() - pos, ChatColor.GREEN.toString());

        pos = i - 2;
        if (pos > 0) {
            bar.insert(bar.length() - pos, ChatColor.DARK_RED.toString());
        }

        MessageUtil.sendActionBarMessage(player, ChatColor.DARK_RED + bar.toString());

        i--;
        if (i == 0) {
            i = 12;
        }
    }

}
