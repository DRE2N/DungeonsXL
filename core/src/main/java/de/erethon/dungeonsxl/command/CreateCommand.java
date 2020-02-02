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
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.world.DEditWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import java.io.File;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class CreateCommand extends DCommand {

    public CreateCommand(DungeonsXL plugin) {
        super(plugin);
        setMinArgs(1);
        setMaxArgs(1);
        setCommand("create");
        setHelp(DMessage.CMD_CREATE_HELP.getMessage());
        setPermission(DPermission.CREATE.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        String name = args[1];

        if (new File(DungeonsXL.MAPS, name).exists()) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NAME_IN_USE.getMessage(name));
            return;
        }

        if (name.length() > 15) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NAME_TOO_LONG.getMessage());
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            // Msg create
            MessageUtil.log(plugin, "&6Creating new map.");
            MessageUtil.log(plugin, "&6Generating new world...");

            // Create World
            DResourceWorld resource = new DResourceWorld(plugin, name);
            instances.addResource(resource);
            DEditWorld editWorld = resource.generate();
            editWorld.save();
            editWorld.delete();

            // MSG Done
            MessageUtil.log(plugin, "&6World generation finished.");

        } else if (sender instanceof Player) {
            Player player = (Player) sender;

            if (DGamePlayer.getByPlayer(player) != null) {
                MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_DUNGEON.getMessage());
                return;
            }

            // Msg create
            MessageUtil.log(plugin, "&6Creating new map.");
            MessageUtil.log(plugin, "&6Generating new world...");

            // Create World
            DResourceWorld resource = new DResourceWorld(plugin, name);
            instances.addResource(resource);
            DEditWorld editWorld = resource.generate();

            // MSG Done
            MessageUtil.log(plugin, "&6World generation finished.");

            // Tp Player
            new DEditPlayer(plugin, player, editWorld);
        }
    }

}
