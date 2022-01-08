/*
 * Copyright (C) 2012-2022 Frank Baumann
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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class InviteCommand extends DCommand {

    public InviteCommand(DungeonsXL plugin) {
        super(plugin);
        setMinArgs(2);
        setMaxArgs(2);
        setCommand("invite");
        setHelp(DMessage.CMD_INVITE_HELP.getMessage());
        setPermission(DPermission.INVITE.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        ResourceWorld resource = plugin.getMapRegistry().get(args[2]);
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

        if (resource != null) {
            if (player != null) {
                resource.addInvitedPlayer(player);
                MessageUtil.sendMessage(sender, DMessage.CMD_INVITE_SUCCESS.getMessage(args[1], args[2]));

            } else {
                MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_PLAYER.getMessage(args[2]));
            }

        } else {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_MAP.getMessage(args[2]));
        }
    }

}
