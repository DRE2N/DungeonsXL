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
package de.erethon.dungeonsxl.command;

import static de.erethon.commons.chat.FatLetter.*;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.compatibility.CompatibilityHandler;
import de.erethon.commons.compatibility.Internals;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DPermission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

/**
 * @author Daniel Saukel
 */
public class MainCommand extends DCommand {

    public MainCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("main");
        setHelp(DMessage.CMD_MAIN_HELP.getMessage());
        setPermission(DPermission.MAIN.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        PluginManager plugins = Bukkit.getServer().getPluginManager();

        int maps = DungeonsXL.MAPS.listFiles().length - 1;
        int dungeons = DungeonsXL.DUNGEONS.listFiles().length;
        int loaded = instances.getEditWorlds().size() + instances.getGameWorlds().size();
        int players = dPlayers.getDGamePlayers().size();
        Internals internals = CompatibilityHandler.getInstance().getInternals();
        String vault = "";
        if (plugins.getPlugin("Vault") != null) {
            vault = plugins.getPlugin("Vault").getDescription().getVersion();
        }
        String itemsxl = "";
        if (plugins.getPlugin("ItemsXL") != null) {
            itemsxl = plugins.getPlugin("ItemsXL").getDescription().getVersion();
        }

        MessageUtil.sendCenteredMessage(sender, "&4" + D[0] + "&f" + X[0] + L[0]);
        MessageUtil.sendCenteredMessage(sender, "&4" + D[1] + "&f" + X[1] + L[1]);
        MessageUtil.sendCenteredMessage(sender, "&4" + D[2] + "&f" + X[2] + L[2]);
        MessageUtil.sendCenteredMessage(sender, "&4" + D[3] + "&f" + X[3] + L[3]);
        MessageUtil.sendCenteredMessage(sender, "&4" + D[4] + "&f" + X[4] + L[4]);
        MessageUtil.sendCenteredMessage(sender, "&b&l###### " + DMessage.CMD_MAIN_WELCOME.getMessage() + "&7 v" + plugin.getDescription().getVersion() + " &b&l######");
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_MAIN_LOADED.getMessage(String.valueOf(maps), String.valueOf(dungeons), String.valueOf(loaded), String.valueOf(players)));
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_MAIN_COMPATIBILITY.getMessage(String.valueOf(internals), vault, itemsxl));
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_MAIN_HELP_INFO.getMessage());
        MessageUtil.sendCenteredMessage(sender, "&7\u00a92012-2020 Frank Baumann & contributors; lcsd. under GPLv3.");
    }

}
