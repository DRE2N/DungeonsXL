/*
 * Copyright (C) 2012-2018 Frank Baumann
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

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.command.DRECommand;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.announcer.Announcer;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DInstancePlayer;
import io.github.dre2n.dungeonsxl.player.DPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class JoinCommand extends DRECommand {

    public JoinCommand() {
        setCommand("join");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(DMessage.HELP_CMD_JOIN.getMessage());
        setPermission(DPermission.JOIN.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        DGlobalPlayer player = DungeonsXL.getInstance().getDPlayers().getByPlayer((Player) sender);
        if (player instanceof DInstancePlayer) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_LEAVE_GAME.getMessage());
            return;
        }

        Announcer announcer = DungeonsXL.getInstance().getAnnouncers().getByName(args[1]);
        if (announcer != null) {
            announcer.showGUI((Player) sender);
        }
    }

}
