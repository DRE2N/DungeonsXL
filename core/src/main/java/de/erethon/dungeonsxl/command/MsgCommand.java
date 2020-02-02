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
import de.erethon.dungeonsxl.world.DEditWorld;
import de.erethon.dungeonsxl.world.WorldConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class MsgCommand extends DCommand {

    public MsgCommand(DungeonsXL plugin) {
        super(plugin);
        setMinArgs(-1);
        setMaxArgs(-1);
        setCommand("msg");
        setHelp(DMessage.CMD_MSG_HELP.getMessage());
        setPermission(DPermission.MESSAGE.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DEditWorld editWorld = DEditWorld.getByWorld(player.getWorld());

        if (editWorld == null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_NOT_IN_DUNGEON.getMessage());
            return;
        }

        if (args.length <= 1) {
            displayHelp(player);
            return;
        }

        try {
            int id = Integer.parseInt(args[1]);

            WorldConfig config = editWorld.getResource().getConfig(true);

            if (args.length == 2) {
                String msg = config.getMessage(id);

                if (msg != null) {
                    MessageUtil.sendMessage(player, ChatColor.WHITE + msg);

                } else {
                    MessageUtil.sendMessage(player, DMessage.ERROR_MSG_ID_NOT_EXIST.getMessage(String.valueOf(id)));
                }

            } else {
                String msg = "";
                int i = 0;
                for (String arg : args) {
                    i++;
                    if (i > 2) {
                        msg = msg + " " + arg;
                    }
                }

                String[] splitMsg = msg.split("\"");

                if (splitMsg.length > 1) {
                    msg = splitMsg[1];
                    String old = config.getMessage(id);
                    if (old == null) {
                        MessageUtil.sendMessage(player, DMessage.CMD_MSG_ADDED.getMessage(String.valueOf(id)));

                    } else {
                        MessageUtil.sendMessage(player, DMessage.CMD_MSG_UPDATED.getMessage(String.valueOf(id)));
                    }

                    config.setMessage(id, msg);
                    config.save();

                } else {
                    MessageUtil.sendMessage(player, DMessage.ERROR_MSG_FORMAT.getMessage());
                }
            }

        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(player, DMessage.ERROR_MSG_NO_INT.getMessage());
        }

    }

}
