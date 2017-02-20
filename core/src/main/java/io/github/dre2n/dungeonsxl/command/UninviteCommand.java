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
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.world.DResourceWorld;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class UninviteCommand extends BRCommand {

    public UninviteCommand() {
        setCommand("uninvite");
        setMinArgs(2);
        setMaxArgs(2);
        setHelp(DMessages.HELP_CMD_UNINVITE.getMessage());
        setPermission(DPermissions.UNINVITE.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        DResourceWorld resource = DungeonsXL.getDWorlds().getResourceByName(args[2]);
        if (resource == null) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_DUNGEON_NOT_EXIST.getMessage(args[2]));
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        if (resource.removeInvitedPlayer(player)) {
            MessageUtil.sendMessage(sender, DMessages.CMD_UNINVITE_SUCCESS.getMessage(args[1], args[2]));
        }
    }

}
