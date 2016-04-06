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
import io.github.dre2n.dungeonsxl.config.MessageConfig;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class BreakCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();
    protected static MessageConfig messageConfig = plugin.getMessageConfig();

    public BreakCommand() {
        setCommand("break");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(messageConfig.getMessage(Messages.HELP_CMD_BREAK));
        setPermission("dxl.break");
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DGlobalPlayer dGlobalPlayer = plugin.getDPlayers().getByPlayer(player);

        if (dGlobalPlayer.isInBreakMode()) {
            dGlobalPlayer.setInBreakMode(false);
            MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.CMD_BREAK_PROTECTED_MODE));

        } else {
            dGlobalPlayer.setInBreakMode(true);
            MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.CMD_BREAK_BREAK_MODE));
        }
    }

}
