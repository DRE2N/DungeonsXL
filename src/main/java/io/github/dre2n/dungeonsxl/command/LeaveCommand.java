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
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerEscapeEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerLeaveDGroupEvent;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class LeaveCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();
    protected static MessageConfig messageConfig = plugin.getMessageConfig();

    public LeaveCommand() {
        setCommand("leave");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(messageConfig.getMessage(DMessages.HELP_CMD_LEAVE));
        setPermission("dxl.leave");
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DPlayer dPlayer = DPlayer.getByPlayer(player);

        if (GameWorld.getByWorld(player.getWorld()) != null) {
            if (GameWorld.getByWorld(player.getWorld()).isTutorial()) {
                MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.ERROR_NO_LEAVE_IN_TUTORIAL));
                return;
            }
        }

        if (dPlayer != null) {
            DGroup dGroup = DGroup.getByPlayer(player);

            DPlayerEscapeEvent dPlayerEscapeEvent = new DPlayerEscapeEvent(dPlayer);
            DPlayerLeaveDGroupEvent dPlayerLeaveDGroupEvent = new DPlayerLeaveDGroupEvent(dPlayer, dGroup);

            if (dPlayerEscapeEvent.isCancelled() || dPlayerLeaveDGroupEvent.isCancelled()) {
                return;
            }

            dPlayer.leave();
            MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.CMD_LEAVE_SUCCESS));

        } else {
            DGroup dGroup = DGroup.getByPlayer(player);
            if (dGroup != null) {
                dGroup.removePlayer(player);
                MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.CMD_LEAVE_SUCCESS));
                return;
            }

            MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.ERROR_NOT_IN_DUNGEON));
        }
    }

}
