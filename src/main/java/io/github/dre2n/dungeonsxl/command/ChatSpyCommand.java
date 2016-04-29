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
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ChatSpyCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();
    protected static MessageConfig messageConfig = plugin.getMessageConfig();

    public ChatSpyCommand() {
        setCommand("chatspy");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(messageConfig.getMessage(DMessages.HELP_CMD_CHATSPY));
        setPermission(DPermissions.CHAT_SPY.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DGlobalPlayer dGlobalPlayer = plugin.getDPlayers().getByPlayer(player);

        if (dGlobalPlayer.isInChatSpyMode()) {
            dGlobalPlayer.setInChatSpyMode(false);
            MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.CMD_CHATSPY_STOPPED));

        } else {
            dGlobalPlayer.setInChatSpyMode(true);
            MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.CMD_CHATSPY_START));
        }
    }

}
