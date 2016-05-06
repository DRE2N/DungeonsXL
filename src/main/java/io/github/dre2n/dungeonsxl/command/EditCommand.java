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
import io.github.dre2n.dungeonsxl.player.DEditPlayer;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class EditCommand extends BRCommand {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public EditCommand() {
        setCommand("edit");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(DMessages.HELP_CMD_EDIT.getMessage());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;

        String mapName = args[1];
        EditWorld editWorld = EditWorld.load(mapName);
        DGroup dGroup = DGroup.getByPlayer(player);
        DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);

        if (!(EditWorld.isInvitedPlayer(mapName, player.getUniqueId(), player.getName()) || DPermissions.hasPermission(player, DPermissions.EDIT))) {
            MessageUtil.sendMessage(player, DMessages.ERROR_NO_PERMISSIONS.getMessage());
            return;
        }

        if (dPlayer != null) {
            MessageUtil.sendMessage(player, DMessages.ERROR_LEAVE_DUNGEON.getMessage());
            return;
        }

        if (dGroup != null) {
            MessageUtil.sendMessage(player, DMessages.ERROR_LEAVE_GROUP.getMessage());
            return;
        }

        if (editWorld == null) {
            MessageUtil.sendMessage(player, DMessages.ERROR_DUNGEON_NOT_EXIST.getMessage(mapName));
            return;
        }

        new DEditPlayer(player, editWorld.getWorld());

    }

}
