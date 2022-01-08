/*
 * Copyright (C) 2012-2022 Frank Baumann
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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.event.group.GroupCreateEvent.Cause;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class EnterCommand extends DCommand {

    public EnterCommand(DungeonsXL plugin) {
        super(plugin);
        setMinArgs(1);
        setMaxArgs(2);
        setCommand("enter");
        setHelp(DMessage.CMD_ENTER_HELP.getMessage());
        setPermission(DPermission.ENTER.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player captain = (Player) sender;
        String targetName = args.length == 3 ? args[2] : args[1];

        PlayerGroup joining = args.length == 3 ? plugin.getGroupCache().get(args[1]) : plugin.getPlayerGroup(captain);
        PlayerGroup target = plugin.getGroupCache().get(targetName);

        if (target == null) {
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer != null) {
                target = plugin.getPlayerGroup(targetPlayer);
            }
        }

        if (target == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_GROUP.getMessage(targetName));
            return;
        }

        Game game = target.getGame();
        if (game == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NOT_IN_GAME.getMessage(targetName));
            return;
        }

        if (joining != null && joining.getGame() != null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_LEAVE_GAME.getMessage());
            return;
        }
        if (joining == null) {
            joining = DGroup.create(plugin, Cause.COMMAND, captain, null, null, game.getDungeon());
        }
        if (joining == null) {
            return;
        }

        if (joining.getLeader() != captain && !DPermission.hasPermission(sender, DPermission.BYPASS)) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NOT_LEADER.getMessage());
            return;
        }

        game.addGroup(joining);
        joining.sendMessage(DMessage.CMD_ENTER_SUCCESS.getMessage(joining.getName(), target.getName()));

        for (Player player : joining.getMembers().getOnlinePlayers()) {
            new DGamePlayer(plugin, player, game.getWorld());
        }
    }

}
