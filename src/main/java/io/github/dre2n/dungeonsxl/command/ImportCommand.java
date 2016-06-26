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
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.world.ResourceWorld;
import io.github.dre2n.itemsxl.util.commons.util.FileUtil;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ImportCommand extends BRCommand {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public ImportCommand() {
        setMinArgs(1);
        setMaxArgs(1);
        setCommand("import");
        setHelp(DMessages.HELP_CMD_IMPORT.getMessage());
        setPermission(DPermissions.IMPORT.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        File target = new File(DungeonsXL.MAPS, args[1]);
        File source = new File(Bukkit.getWorldContainer(), args[1]);

        if (!source.exists()) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NO_SUCH_MAP.getMessage(args[1]));
            return;
        }

        if (target.exists()) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NAME_IN_USE.getMessage(args[1]));
            return;
        }

        World world = Bukkit.getWorld(args[1]);
        if (world != null) {
            world.save();
        }

        MessageUtil.log(plugin, DMessages.LOG_NEW_MAP.getMessage());
        MessageUtil.log(plugin, DMessages.LOG_IMPORT_WORLD.getMessage());

        FileUtil.copyDirectory(source, target, new String[]{"playerdata", "stats"});

        new ResourceWorld(args[1]);

        MessageUtil.sendMessage(sender, DMessages.CMD_IMPORT_SUCCESS.getMessage(args[1]));
    }

}
