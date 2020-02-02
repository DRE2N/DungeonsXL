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
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.config.MainConfig.BackupMode;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.world.DEditWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class SaveCommand extends DCommand {

    public SaveCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("save");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.CMD_SAVE_HELP.getMessage());
        setPermission(DPermission.SAVE.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DEditWorld editWorld = DEditWorld.getByWorld(player.getWorld());
        if (editWorld != null) {
            BackupMode backupMode = config.getBackupMode();
            if (backupMode == BackupMode.ON_SAVE || backupMode == BackupMode.ON_DISABLE_AND_SAVE) {
                editWorld.getResource().backup();
            }

            editWorld.save();
            MessageUtil.sendMessage(player, DMessage.CMD_SAVE_SUCCESS.getMessage());

        } else {
            MessageUtil.sendMessage(player, DMessage.ERROR_NOT_IN_DUNGEON.getMessage());
        }
    }

}
