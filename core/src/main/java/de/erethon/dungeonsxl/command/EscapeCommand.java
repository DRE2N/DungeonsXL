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
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.world.DEditWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Milan Albrecht, Daniel Saukel
 */
public class EscapeCommand extends DCommand {

    public EscapeCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("escape");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.CMD_ESCAPE_HELP.getMessage());
        setPermission(DPermission.ESCAPE.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DEditPlayer dPlayer = DEditPlayer.getByPlayer(player);

        if (DGamePlayer.getByPlayer(player) != null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_DUNGEON.getMessage());

        } else if (dPlayer != null) {
            dPlayer.escape();

            DEditWorld editWorld = DEditWorld.getByWorld(dPlayer.getWorld());
            if (editWorld == null) {
                return;
            }

            if (editWorld.getWorld().getPlayers().isEmpty()) {
                editWorld.delete(false);
            }

        } else {
            DGroup dGroup = DGroup.getByPlayer(player);
            if (dGroup != null) {
                dGroup.removePlayer(player);
                MessageUtil.sendMessage(player, DMessage.CMD_LEAVE_SUCCESS.getMessage());
                return;
            }
            MessageUtil.sendMessage(player, DMessage.ERROR_NOT_IN_DUNGEON.getMessage());
        }
    }

}
