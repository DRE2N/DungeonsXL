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
import io.github.dre2n.commons.util.UUIDUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.MessageConfig;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import org.bukkit.command.CommandSender;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class InviteCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();
    protected static MessageConfig messageConfig = plugin.getMessageConfig();

    public InviteCommand() {
        setMinArgs(2);
        setMaxArgs(2);
        setCommand("invite");
        setHelp(messageConfig.getMessage(Messages.HELP_CMD_INVITE));
        setPermission("dxl.invite");
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        if (EditWorld.addInvitedPlayer(args[2], UUIDUtil.getUniqueIdFromName(args[1]))) {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.CMD_INVITE_SUCCESS, args[1], args[2]));

        } else {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_DUNGEON_NOT_EXIST, args[2]));
        }
    }

}
