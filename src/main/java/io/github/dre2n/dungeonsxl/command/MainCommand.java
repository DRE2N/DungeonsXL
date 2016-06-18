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
package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.commons.command.BRCommand;
import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.compatibility.Internals;
import static io.github.dre2n.commons.util.messageutil.FatLetters.*;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

/**
 * @author Daniel Saukel
 */
public class MainCommand extends BRCommand {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public MainCommand() {
        setCommand("main");
        setHelp(DMessages.HELP_CMD_MAIN.getMessage());
        setPermission(DPermissions.MAIN.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        PluginManager plugins = Bukkit.getServer().getPluginManager();

        int maps = new File(plugin.getDataFolder() + "/maps").listFiles().length;
        int dungeons = new File(plugin.getDataFolder() + "/dungeons").listFiles().length;
        int loaded = plugin.getEditWorlds().size() + plugin.getGameWorlds().size();
        int players = plugin.getDPlayers().getDGamePlayers().size();
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
        MessageUtil.sendCenteredMessage(sender, "&b&l###### " + DMessages.CMD_MAIN_WELCOME.getMessage() + "&7 v" + plugin.getDescription().getVersion() + " &b&l######");
        MessageUtil.sendCenteredMessage(sender, DMessages.CMD_MAIN_LOADED.getMessage(String.valueOf(maps), String.valueOf(dungeons), String.valueOf(loaded), String.valueOf(players)));
        MessageUtil.sendCenteredMessage(sender, DMessages.CMD_MAIN_COMPATIBILITY.getMessage(String.valueOf(internals), vault, itemsxl));
        MessageUtil.sendCenteredMessage(sender, DMessages.CMD_MAIN_HELP.getMessage());
        MessageUtil.sendCenteredMessage(sender, "&7\u00a92012-2016 Frank Baumann & contributors; lcsd. under GPLv3.");
    }

}
