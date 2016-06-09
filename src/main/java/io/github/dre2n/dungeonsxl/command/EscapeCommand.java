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
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DEditPlayer;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Milan Albrecht, Daniel Saukel
 */
public class EscapeCommand extends BRCommand {

    public EscapeCommand() {
        setCommand("escape");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessages.HELP_CMD_ESCAPE.getMessage());
        setPermission(DPermissions.ESCAPE.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DEditPlayer dPlayer = DEditPlayer.getByPlayer(player);

        if (DGamePlayer.getByPlayer(player) != null) {
            MessageUtil.sendMessage(player, DMessages.ERROR_LEAVE_DUNGEON.getMessage());

        } else if (dPlayer != null) {
            dPlayer.escape();

            EditWorld editWorld = EditWorld.getByWorld(dPlayer.getWorld());
            if (editWorld == null) {
                return;
            }

            if (editWorld.getWorld().getPlayers().isEmpty()) {
                editWorld.deleteNoSave();
            }

        } else {
            DGroup dGroup = DGroup.getByPlayer(player);
            if (dGroup != null) {
                dGroup.removePlayer(player);
                MessageUtil.sendMessage(player, DMessages.CMD_LEAVE_SUCCESS.getMessage());
                return;
            }
            MessageUtil.sendMessage(player, DMessages.ERROR_NOT_IN_DUNGEON.getMessage());
        }
    }

}
