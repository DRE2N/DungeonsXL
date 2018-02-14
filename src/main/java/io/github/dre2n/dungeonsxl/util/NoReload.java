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
package io.github.dre2n.dungeonsxl.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;

/**
 * NoReload 1.0
 *
 * @author Daniel Saukel
 */
public class NoReload implements Listener {

    Plugin plugin;

    /**
     * @param plugin
     * the plugin that integrates NoReload
     */
    public NoReload(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String pl = plugin.getDescription().getName().toUpperCase();
        String cmd = event.getMessage().toUpperCase();
        if (cmd.contains("PLUGMAN") && cmd.contains(pl)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "The plugin " + pl + " does not support insecure loading! Restart your server instead.");
        }
    }

    @EventHandler
    void onConsoleCommand(ServerCommandEvent event) {
        String pl = plugin.getDescription().getName().toUpperCase();
        String cmd = event.getCommand().toUpperCase();
        if (cmd.contains("PLUGMAN") && cmd.contains(pl)) {
            event.setCancelled(true);
            event.getSender().sendMessage(ChatColor.RED + "The plugin " + pl + " does not support insecure loading! Restart your server instead.");
        }
    }

}
