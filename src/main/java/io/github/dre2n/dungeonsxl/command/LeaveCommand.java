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
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerEscapeEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerLeaveDGroupEvent;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DInstancePlayer;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class LeaveCommand extends BRCommand {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public LeaveCommand() {
        setCommand("leave");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessages.HELP_CMD_LEAVE.getMessage());
        setPermission(DPermissions.LEAVE.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;

        if (!(plugin.getDPlayers().getByPlayer(player) instanceof DInstancePlayer)) {
            MessageUtil.sendMessage(player, DMessages.ERROR_NOT_IN_DUNGEON.getMessage());
            return;
        }

        DInstancePlayer dPlayer = (DInstancePlayer) plugin.getDPlayers().getByPlayer(player);

        if (GameWorld.getByWorld(player.getWorld()) != null) {
            if (GameWorld.getByWorld(player.getWorld()).isTutorial()) {
                MessageUtil.sendMessage(player, DMessages.ERROR_NO_LEAVE_IN_TUTORIAL.getMessage());
                return;
            }
        }

        if (dPlayer != null) {
            DGroup dGroup = DGroup.getByPlayer(player);

            DPlayerEscapeEvent dPlayerEscapeEvent = new DPlayerEscapeEvent(dPlayer);
            plugin.getServer().getPluginManager().callEvent(dPlayerEscapeEvent);
            DPlayerLeaveDGroupEvent dPlayerLeaveDGroupEvent = new DPlayerLeaveDGroupEvent(dPlayer, dGroup);
            plugin.getServer().getPluginManager().callEvent(dPlayerLeaveDGroupEvent);

            if (dPlayerEscapeEvent.isCancelled() || dPlayerLeaveDGroupEvent.isCancelled()) {
                return;
            }

            dPlayer.leave();
            MessageUtil.sendMessage(player, DMessages.CMD_LEAVE_SUCCESS.getMessage());

        } else {
            DGroup dGroup = DGroup.getByPlayer(player);
            if (dGroup != null) {
                dGroup.removePlayer(player);
                MessageUtil.sendMessage(player, DMessages.CMD_LEAVE_SUCCESS.getMessage());
            }
        }
    }

}
