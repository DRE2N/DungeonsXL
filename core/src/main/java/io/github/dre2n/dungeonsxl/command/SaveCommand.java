/*
 * Copyright (C) 2012-2017 Frank Baumann
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
import io.github.dre2n.dungeonsxl.config.MainConfig;
import io.github.dre2n.dungeonsxl.config.MainConfig.BackupMode;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.world.DEditWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class SaveCommand extends BRCommand {

    MainConfig mainConfig = DungeonsXL.getMainConfig();

    public SaveCommand() {
        setCommand("save");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessages.HELP_CMD_SAVE.getMessage());
        setPermission(DPermissions.SAVE.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DEditWorld editWorld = DEditWorld.getByWorld(player.getWorld());
        if (editWorld != null) {
            BackupMode backupMode = mainConfig.getBackupMode();
            if (backupMode == BackupMode.ON_SAVE || backupMode == BackupMode.ON_DISABLE_AND_SAVE) {
                editWorld.getResource().backup(mainConfig.areTweaksEnabled());
            }

            editWorld.save();
            MessageUtil.sendMessage(player, DMessages.CMD_SAVE_SUCCESS.getMessage());

        } else {
            MessageUtil.sendMessage(player, DMessages.ERROR_NOT_IN_DUNGEON.getMessage());
        }
    }

}
