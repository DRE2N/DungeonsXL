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
import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.config.WorldConfig;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class MsgCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();
    protected static MessageConfig messageConfig = plugin.getMessageConfig();

    public MsgCommand() {
        setMinArgs(-1);
        setMaxArgs(-1);
        setCommand("msg");
        setHelp(messageConfig.getMessage(DMessages.HELP_CMD_MSG));
        setPermission(DPermissions.MESSAGE.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        EditWorld editWorld = EditWorld.getByWorld(player.getWorld());

        if (editWorld == null) {
            MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.ERROR_NOT_IN_DUNGEON));
            return;
        }

        if (args.length <= 1) {
            displayHelp(player);
            return;
        }

        try {
            int id = NumberUtil.parseInt(args[1]);

            WorldConfig confreader = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + editWorld.getMapName(), "config.yml"));

            if (args.length == 2) {
                String msg = confreader.getMsg(id, true);

                if (msg != null) {
                    MessageUtil.sendMessage(player, ChatColor.WHITE + msg);

                } else {
                    MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.ERROR_MSG_ID_NOT_EXIST, "" + id));
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
                    String old = confreader.getMsg(id, false);
                    if (old == null) {
                        MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.CMD_MSG_ADDED, "" + id));

                    } else {
                        MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.CMD_MSG_UPDATED, "" + id));
                    }

                    confreader.setMsg(msg, id);
                    confreader.save();

                } else {
                    MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.ERROR_MSG_FORMAT));
                }
            }

        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(player, messageConfig.getMessage(DMessages.ERROR_MSG_NO_INT));
        }

    }

}
