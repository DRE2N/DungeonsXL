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
package de.erethon.dungeonsxl.sign.windup;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class CommandTask extends BukkitRunnable {

    private Player player;
    private boolean wasOp;
    private CommandSign sign;
    private CommandScript script;
    private CommandSender sender;
    private boolean papi;

    private int k;

    public CommandTask(CommandSign sign, boolean papi) {
        this.sign = sign;
        this.script = sign.getScript();
        this.papi = papi;
    }

    public void setSender(CommandSender sender, boolean wasOp) {
        this.sender = sender;
        this.wasOp = wasOp;
    }

    @Override
    public void run() {
        if (!player.hasPermission(script.getPermission(player))) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return;
        }

        String command = script.getCommands().get(k++).replace("%player%", sender.getName()).replace("%player_name%", sender.getName());
        if (papi) {
            Bukkit.getServer().dispatchCommand(sender, PlaceholderAPI.setPlaceholders(player, command));
        } else {
            Bukkit.getServer().dispatchCommand(sender, command);
        }

        if (k >= script.getCommands().size()) {
            sign.deactivate();
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        if (sender != Bukkit.getConsoleSender()) {
            sender.setOp(wasOp);
        }
    }

}
