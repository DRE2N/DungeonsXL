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
import static io.github.dre2n.dungeonsxl.command.DCommands.LEAVE;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class KickCommand extends BRCommand {

    public KickCommand() {
        setCommand("kick");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(DMessages.HELP_CMD_KICK.getMessage());
        setPermission(DPermissions.KICK.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = Bukkit.getPlayer(args[1]);

        if (player != null) {
            LEAVE.onExecute(new String[]{LEAVE.getCommand()}, player);
            MessageUtil.sendMessage(sender, DMessages.CMD_KICK_SUCCESS.getMessage(player.getName()));

        } else {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NO_SUCH_PLAYER.getMessage(args[1]));
        }
    }

}
