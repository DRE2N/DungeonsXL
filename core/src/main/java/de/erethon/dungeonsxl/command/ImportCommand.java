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

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.misc.FileUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.world.DResourceWorld;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ImportCommand extends DCommand {

    public ImportCommand(DungeonsXL plugin) {
        super(plugin);
        setMinArgs(1);
        setMaxArgs(1);
        setCommand("import");
        setHelp(DMessage.CMD_IMPORT_HELP.getMessage());
        setPermission(DPermission.IMPORT.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        File target = new File(DungeonsXL.MAPS, args[1]);
        File source = new File(Bukkit.getWorldContainer(), args[1]);

        if (!source.exists()) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_MAP.getMessage(args[1]));
            return;
        }

        if (target.exists()) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NAME_IN_USE.getMessage(args[1]));
            return;
        }

        World world = Bukkit.getWorld(args[1]);
        if (world != null) {
            world.save();
        }

        MessageUtil.log(plugin, "&6Creating new map.");
        MessageUtil.log(plugin, "&6Importing world...");

        FileUtil.copyDir(source, target, "playerdata", "stats");

        DResourceWorld resource = new DResourceWorld(plugin, args[1]);
        if (world.getEnvironment() != Environment.NORMAL) {
            resource.getConfig(true).setWorldEnvironment(world.getEnvironment());
            resource.getConfig().save();
        }
        instances.addResource(resource);
        MessageUtil.sendMessage(sender, DMessage.CMD_IMPORT_SUCCESS.getMessage(args[1]));
    }

}
