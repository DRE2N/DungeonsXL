/*
 * Copyright (C) 2012-2018 Frank Baumann
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

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.command.DRECommand;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.player.DPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class ResourcePackCommand extends DRECommand {

    public ResourcePackCommand() {
        setCommand("resourcepack");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(DMessage.HELP_CMD_RESOURCE_PACK.getMessage());
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

        String url = (String) DungeonsXL.getInstance().getMainConfig().getResourcePacks().get(args[1]);
        if (url == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_RESOURCE_PACK.getMessage(args[1]));
            return;
        }

        player.setResourcePack(url);
    }

}
