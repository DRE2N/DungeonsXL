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
import io.github.dre2n.dungeonsxl.config.MessageConfig;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

/**
 * @author Daniel Saukel
 */
public class MainCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();
    protected static MessageConfig messageConfig = plugin.getMessageConfig();

    public MainCommand() {
        setCommand("main");
        setHelp(messageConfig.getMessage(Messages.HELP_CMD_MAIN));
        setPermission("dxl.main");
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        PluginManager plugins = Bukkit.getServer().getPluginManager();

        int maps = new File(plugin.getDataFolder() + "/maps").listFiles().length;
        int dungeons = new File(plugin.getDataFolder() + "/dungeons").listFiles().length;
        int loaded = plugin.getEditWorlds().size() + plugin.getGameWorlds().size();
        int players = plugin.getDPlayers().size();
        Internals internals = CompatibilityHandler.getInstance().getInternals();
        String vault = "";
        if (plugins.getPlugin("Vault") != null) {
            vault = plugins.getPlugin("Vault").getDescription().getVersion();
        }
        String mythicMobs = "";
        if (plugins.getPlugin("MythicMobs") != null) {
            mythicMobs = plugins.getPlugin("MythicMobs").getDescription().getVersion();
        }

        MessageUtil.sendCenteredMessage(sender, "&4" + D[0] + "&f" + X[0] + L[0]);
        MessageUtil.sendCenteredMessage(sender, "&4" + D[1] + "&f" + X[1] + L[1]);
        MessageUtil.sendCenteredMessage(sender, "&4" + D[2] + "&f" + X[2] + L[2]);
        MessageUtil.sendCenteredMessage(sender, "&4" + D[3] + "&f" + X[3] + L[3]);
        MessageUtil.sendCenteredMessage(sender, "&4" + D[4] + "&f" + X[4] + L[4]);
        MessageUtil.sendCenteredMessage(sender, "&b&l###### " + messageConfig.getMessage(Messages.CMD_MAIN_WELCOME) + "&7 v" + plugin.getDescription().getVersion() + " &b&l######");
        MessageUtil.sendCenteredMessage(sender, messageConfig.getMessage(Messages.CMD_MAIN_LOADED, String.valueOf(maps), String.valueOf(dungeons), String.valueOf(loaded), String.valueOf(players)));
        MessageUtil.sendCenteredMessage(sender, messageConfig.getMessage(Messages.CMD_MAIN_COMPATIBILITY, String.valueOf(internals), vault, mythicMobs));
        MessageUtil.sendCenteredMessage(sender, messageConfig.getMessage(Messages.CMD_MAIN_HELP));
        MessageUtil.sendCenteredMessage(sender, "&7\u00a92012-2016 Frank Baumann & contributors; lcsd. under GPLv3.");
    }

}
