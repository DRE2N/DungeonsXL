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
import de.erethon.dungeonsxl.event.dplayer.DPlayerLeaveDGroupEvent;
import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerEscapeEvent;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DEditPlayer;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DInstancePlayer;
import de.erethon.dungeonsxl.player.DPermission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class LeaveCommand extends DCommand {

    public LeaveCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("leave");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.CMD_LEAVE_HELP.getMessage());
        setPermission(DPermission.LEAVE.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DGlobalPlayer dPlayer = dPlayers.getByPlayer(player);
        Game game = Game.getByPlayer(player);

        if (game != null && game.isTutorial()) {
            MessageUtil.sendMessage(player, DMessage.ERROR_NO_LEAVE_IN_TUTORIAL.getMessage());
            return;
        }

        DGroup dGroup = DGroup.getByPlayer(player);

        if (dGroup == null && !(dPlayer instanceof DEditPlayer)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_JOIN_GROUP.getMessage());
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

        MessageUtil.sendMessage(player, DMessage.CMD_LEAVE_SUCCESS.getMessage());
    }

}
