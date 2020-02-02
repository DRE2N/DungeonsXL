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
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.world.DResourceWorld;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class UninviteCommand extends DCommand {

    public UninviteCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("uninvite");
        setMinArgs(2);
        setMaxArgs(2);
        setHelp(DMessage.CMD_UNINVITE_HELP.getMessage());
        setPermission(DPermission.UNINVITE.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        DResourceWorld resource = instances.getResourceByName(args[2]);
        if (resource == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_MAP.getMessage(args[2]));
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        if (resource.removeInvitedPlayer(player)) {
            MessageUtil.sendMessage(sender, DMessage.CMD_UNINVITE_SUCCESS.getMessage(args[1], args[2]));
        }
    }

}
