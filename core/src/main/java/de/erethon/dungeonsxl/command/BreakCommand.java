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
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class BreakCommand extends DCommand {

    public BreakCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("break");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.CMD_BREAK_HELP.getMessage());
        setPermission(DPermission.BREAK.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DGlobalPlayer dGlobalPlayer = dPlayers.getByPlayer(player);

        if (dGlobalPlayer.isInBreakMode()) {
            dGlobalPlayer.setInBreakMode(false);
            MessageUtil.sendMessage(sender, DMessage.CMD_BREAK_PROTECTED_MODE.getMessage());

        } else {
            dGlobalPlayer.setInBreakMode(true);
            MessageUtil.sendMessage(sender, DMessage.CMD_BREAK_BREAK_MODE.getMessage());
        }
    }

}
