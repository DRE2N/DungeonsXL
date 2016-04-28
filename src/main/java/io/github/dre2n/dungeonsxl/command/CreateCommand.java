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
import io.github.dre2n.commons.config.MessageConfig;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class CreateCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();
    protected static MessageConfig messageConfig = plugin.getMessageConfig();

    public CreateCommand() {
        setMinArgs(1);
        setMaxArgs(1);
        setCommand("create");
        setHelp(messageConfig.getMessage(DMessages.HELP_CMD_CREATE));
        setPermission("dxl.create");
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        String name = args[1];

        if (sender instanceof ConsoleCommandSender) {
            if (name.length() <= 15) {
                // Msg create
                MessageUtil.log(plugin, messageConfig.getMessage(DMessages.LOG_NEW_DUNGEON));
                MessageUtil.log(plugin, messageConfig.getMessage(DMessages.LOG_GENERATE_NEW_WORLD));

                // Create World
                EditWorld editWorld = new EditWorld();
                editWorld.generate();
                editWorld.setMapName(name);
                editWorld.save();
                editWorld.delete();

                // MSG Done
                MessageUtil.log(plugin, messageConfig.getMessage(DMessages.LOG_WORLD_GENERATION_FINISHED));

            } else {
                MessageUtil.sendMessage(sender, messageConfig.getMessage(DMessages.ERROR_NAME_TO_LONG));
            }

        } else if (sender instanceof Player) {
            Player player = (Player) sender;

            if (DPlayer.getByPlayer(player) != null) {
                MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.ERROR_LEAVE_DUNGEON));
                return;
            }

            if (name.length() <= 15) {
                // Msg create
                MessageUtil.log(plugin, messageConfig.getMessage(DMessages.LOG_NEW_DUNGEON));
                MessageUtil.log(plugin, messageConfig.getMessage(DMessages.LOG_GENERATE_NEW_WORLD));

                // Create World
                EditWorld editWorld = new EditWorld();
                editWorld.generate();
                editWorld.setMapName(name);

                // MSG Done
                MessageUtil.log(plugin, messageConfig.getMessage(DMessages.LOG_WORLD_GENERATION_FINISHED));

                // Tp Player
                new DPlayer(player, editWorld.getWorld(), true);

            } else {
                MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.ERROR_NAME_TO_LONG));
            }
        }
    }

}
