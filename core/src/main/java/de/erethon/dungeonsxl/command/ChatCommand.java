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
import de.erethon.dungeonsxl.player.DEditPlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ChatCommand extends DCommand {

    public ChatCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("chat");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.CMD_CHAT_HELP.getMessage());
        setPermission(DPermission.CHAT.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DGlobalPlayer dPlayer = dPlayers.getByPlayer(player);

        if (DGroup.getByPlayer(player) == null && !(dPlayer instanceof DEditPlayer)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_JOIN_GROUP.getMessage());
            return;
        }

        dPlayer.setInGroupChat(!dPlayer.isInGroupChat());
        MessageUtil.sendMessage(player, (dPlayer.isInGroupChat() ? DMessage.CMD_CHAT_DUNGEON_CHAT : DMessage.CMD_CHAT_NORMAL_CHAT).getMessage());
    }

}
