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
import de.erethon.dungeonsxl.player.DPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class ResourcePackCommand extends DCommand {

    public ResourcePackCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("resourcepack");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(DMessage.CMD_RESOURCE_PACK_HELP.getMessage());
        setPermission(DPermission.RESOURCE_PACK.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;

        if (args[1].equalsIgnoreCase("reset")) {
            // Placeholder to reset to default
            player.setResourcePack("http://google.com");
            return;
        }

        String url = (String) config.getResourcePacks().get(args[1]);
        if (url == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_RESOURCE_PACK.getMessage(args[1]));
            return;
        }

        player.setResourcePack(url);
    }

}
