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
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPermission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class LivesCommand extends DRECommand {

    public LivesCommand() {
        setCommand("lives");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(DMessage.HELP_CMD_LIVES.getMessage());
        setPermission(DPermission.LIVES.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = null;

        if (args.length == 2) {
            if (Bukkit.getServer().getPlayer(args[1]) != null) {
                player = Bukkit.getServer().getPlayer(args[1]);
            }

        } else if (sender instanceof Player) {
            player = (Player) sender;

        } else {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_CONSOLE_COMMAND.getMessage(getCommand()));
            return;
        }

        DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
        if (dPlayer == null && args.length == 1) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_PLAYER.getMessage(args[1]));
            return;
        }
        DGroup dGroup = dPlayer != null ? dPlayer.getDGroup() : DGroup.getByName(args[1]);
        if (dPlayer != null) {
            MessageUtil.sendMessage(sender, DMessage.CMD_LIVES_PLAYER.getMessage(dPlayer.getName(), String.valueOf(dPlayer.getLives() == -1 ? "UNLIMITED" : dPlayer.getLives())));

        } else if (dGroup != null) {
            MessageUtil.sendMessage(sender, DMessage.CMD_LIVES_GROUP.getMessage(dGroup.getName(), String.valueOf(dGroup.getLives() == -1 ? "UNLIMITED" : dPlayer.getLives())));

        } else {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_PLAYER.getMessage(args[1]));
        }
    }

}
