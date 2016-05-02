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
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ChatCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();

    public ChatCommand() {
        setCommand("chat");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessages.HELP_CMD_CHAT.getMessage());
        setPermission(DPermissions.CHAT.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DPlayer dPlayer = DPlayer.getByPlayer(player);

        if (dPlayer == null) {
            MessageUtil.sendMessage(player, DMessages.ERROR_JOIN_GROUP.getMessage());
            return;
        }

        if (dPlayer.isInDungeonChat()) {
            dPlayer.setInDungeonChat(false);
            MessageUtil.sendMessage(player, DMessages.CMD_CHAT_NORMAL_CHAT.getMessage());

        } else {
            dPlayer.setInDungeonChat(true);
            MessageUtil.sendMessage(player, DMessages.CMD_CHAT_DUNGEON_CHAT.getMessage());
        }
    }

}
