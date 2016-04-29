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
import io.github.dre2n.commons.config.MessageConfig;
import io.github.dre2n.commons.util.UUIDUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import org.bukkit.command.CommandSender;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class UninviteCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();
    protected static MessageConfig messageConfig = plugin.getMessageConfig();

    public UninviteCommand() {
        setCommand("uninvite");
        setMinArgs(2);
        setMaxArgs(2);
        setHelp(messageConfig.getMessage(DMessages.HELP_CMD_UNINVITE));
        setPermission(DPermissions.UNINVITE.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        if (EditWorld.removeInvitedPlayer(args[2], UUIDUtil.getUniqueIdFromName(args[1]), args[1])) {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(DMessages.CMD_UNINVITE_SUCCESS, args[1], args[2]));

        } else {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(DMessages.ERROR_DUNGEON_NOT_EXIST, args[2]));
        }
    }

}
