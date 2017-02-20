/*
 * Copyright (C) 2012-2017 Frank Baumann
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
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerLeaveDGroupEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.instance.game.DGamePlayerEscapeEvent;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.player.DEditPlayer;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DInstancePlayer;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class LeaveCommand extends BRCommand {

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
        DGlobalPlayer dPlayer = DungeonsXL.getDPlayers().getByPlayer(player);
        Game game = Game.getByPlayer(player);

        if (game != null && game.isTutorial()) {
            MessageUtil.sendMessage(player, DMessages.ERROR_NO_LEAVE_IN_TUTORIAL.getMessage());
            return;
        }

        DGroup dGroup = DGroup.getByPlayer(player);

        if (dGroup == null && !(dPlayer instanceof DEditPlayer)) {
            MessageUtil.sendMessage(player, DMessages.ERROR_JOIN_GROUP.getMessage());
            return;
        }

        if (dPlayer instanceof DGamePlayer) {
            DGamePlayerEscapeEvent dPlayerEscapeEvent = new DGamePlayerEscapeEvent((DGamePlayer) dPlayer);
            Bukkit.getPluginManager().callEvent(dPlayerEscapeEvent);
            if (dPlayerEscapeEvent.isCancelled()) {
                return;
            }
        }

        DPlayerLeaveDGroupEvent dPlayerLeaveDGroupEvent = new DPlayerLeaveDGroupEvent(dPlayer, dGroup);
        Bukkit.getPluginManager().callEvent(dPlayerLeaveDGroupEvent);
        if (dPlayerLeaveDGroupEvent.isCancelled()) {
            return;
        }

        if (dPlayer instanceof DInstancePlayer) {
            ((DInstancePlayer) dPlayer).leave();
        } else {
            dGroup.removePlayer(player);
        }

        MessageUtil.sendMessage(player, DMessages.CMD_LEAVE_SUCCESS.getMessage());
    }

}
