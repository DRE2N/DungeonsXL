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
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DPermission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class LivesCommand extends DCommand {

    public LivesCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("lives");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(DMessage.CMD_LIVES_HELP.getMessage());
        setPermission(DPermission.LIVES.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = null;

        if (args.length == 2) {
            if (Bukkit.getPlayer(args[1]) != null) {
                player = Bukkit.getPlayer(args[1]);
            }

        } else if (sender instanceof Player) {
            player = (Player) sender;

        } else {
            MessageUtil.sendMessage(sender, CommonMessage.CMD_NO_CONSOLE_COMMAND.getMessage(getCommand()));
            return;
        }

        DGlobalPlayer globalPlayer = dPlayers.getByPlayer(player);
        if (!(globalPlayer instanceof DGamePlayer)) {
            MessageUtil.sendMessage(sender, args.length == 1 ? DMessage.ERROR_NO_GAME.getMessage() : DMessage.ERROR_NO_SUCH_PLAYER.getMessage(args[1]));
            return;
        }

        DGamePlayer gamePlayer = (DGamePlayer) globalPlayer;
        DGroup dGroup = gamePlayer != null ? gamePlayer.getDGroup() : DGroup.getByName(args[1]);
        if (gamePlayer != null) {
            MessageUtil.sendMessage(sender, DMessage.CMD_LIVES_PLAYER.getMessage(gamePlayer.getName(), gamePlayer.getLives() == -1 ? DMessage.MISC_UNLIMITED.getMessage() : String.valueOf(gamePlayer.getLives())));

        } else if (dGroup != null) {
            MessageUtil.sendMessage(sender, DMessage.CMD_LIVES_GROUP.getMessage(dGroup.getName(), String.valueOf(dGroup.getLives() == -1 ? "UNLIMITED" : dGroup.getLives())));

        } else {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_PLAYER.getMessage(args[1]));
        }
    }

}
