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
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import java.io.File;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class CreateCommand extends BRCommand {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public CreateCommand() {
        setMinArgs(1);
        setMaxArgs(1);
        setCommand("create");
        setHelp(DMessages.HELP_CMD_CREATE.getMessage());
        setPermission(DPermissions.CREATE.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        String name = args[1];

        if (new File(plugin.getDataFolder(), "/maps/" + name).exists()) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NAME_IN_USE.getMessage(name));
            return;
        }

        if (name.length() > 15) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NAME_TO_LONG.getMessage());
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            // Msg create
            MessageUtil.log(plugin, DMessages.LOG_NEW_DUNGEON.getMessage());
            MessageUtil.log(plugin, DMessages.LOG_GENERATE_NEW_WORLD.getMessage());

            // Create World
            EditWorld editWorld = new EditWorld();
            editWorld.generate();
            editWorld.setMapName(name);
            editWorld.save();
            editWorld.delete();

            // MSG Done
            MessageUtil.log(plugin, DMessages.LOG_WORLD_GENERATION_FINISHED.getMessage());

        } else if (sender instanceof Player) {
            Player player = (Player) sender;

            if (DGamePlayer.getByPlayer(player) != null) {
                MessageUtil.sendMessage(player, DMessages.ERROR_LEAVE_DUNGEON.getMessage());
                return;
            }

            // Msg create
            MessageUtil.log(plugin, DMessages.LOG_NEW_DUNGEON.getMessage());
            MessageUtil.log(plugin, DMessages.LOG_GENERATE_NEW_WORLD.getMessage());

            // Create World
            EditWorld editWorld = new EditWorld();
            editWorld.generate();
            editWorld.setMapName(name);

            // MSG Done
            MessageUtil.log(plugin, DMessages.LOG_WORLD_GENERATION_FINISHED.getMessage());

            // Tp Player
            new DEditPlayer(player, editWorld.getWorld());
        }
    }

}
