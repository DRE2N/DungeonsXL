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
import de.erethon.commons.config.CommonMessage;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DEditPlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DInstancePlayer;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.world.DEditWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class EditCommand extends DCommand {

    public EditCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("edit");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(DMessage.CMD_EDIT_HELP.getMessage());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        String mapName = args[1];

        if (!instances.exists(mapName)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_NO_SUCH_DUNGEON.getMessage(mapName));
            return;
        }

        DResourceWorld resource = instances.getResourceByName(mapName);
        if (resource == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_MAP.getMessage(mapName));
            return;
        }

        if (!resource.isInvitedPlayer(player) && !DPermission.hasPermission(player, DPermission.EDIT)) {
            MessageUtil.sendMessage(player, CommonMessage.CMD_NO_PERMISSION.getMessage());
            return;
        }

        DEditWorld editWorld = resource.instantiateAsEditWorld(false);
        if (editWorld == null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_TOO_MANY_INSTANCES.getMessage());
            return;
        }

        DGroup dGroup = DGroup.getByPlayer(player);
        DGlobalPlayer dPlayer = dPlayers.getByPlayer(player);

        if (dPlayer instanceof DInstancePlayer) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_DUNGEON.getMessage());
            return;
        }

        if (dGroup != null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_GROUP.getMessage());
            return;
        }

        new DEditPlayer(plugin, player, editWorld);
    }

}
