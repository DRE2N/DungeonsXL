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
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPermission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class EnterCommand extends DRECommand {

    public EnterCommand() {
        setMinArgs(1);
        setMaxArgs(2);
        setCommand("enter");
        setHelp(DMessage.HELP_CMD_ENTER.getMessage());
        setPermission(DPermission.ENTER.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player captain = (Player) sender;
        String targetName = args.length == 3 ? args[2] : args[1];

        DGroup joining = args.length == 3 ? DGroup.getByName(args[1]) : DGroup.getByPlayer(captain);
        DGroup target = DGroup.getByName(targetName);

        if (target == null) {
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer != null) {
                target = DGroup.getByPlayer(targetPlayer);
            }
        }

        if (target == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_GROUP.getMessage(targetName));
            return;
        }

        Game game = Game.getByDGroup(target);
        if (game == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NOT_IN_GAME.getMessage(targetName));
            return;
        }

        if (Game.getByDGroup(joining) != null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_LEAVE_GAME.getMessage());
            return;
        }

        if (joining == null) {
            joining = new DGroup(captain, game.getDungeon());
        }

        if (joining.getCaptain() != captain && !DPermission.hasPermission(sender, DPermission.BYPASS)) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NOT_CAPTAIN.getMessage());
            return;
        }

        joining.setGameWorld(game.getWorld());
        game.addDGroup(joining);
        joining.sendMessage(DMessage.CMD_ENTER_SUCCESS.getMessage(joining.getName(), target.getName()));

        for (Player player : joining.getPlayers().getOnlinePlayers()) {
            DGamePlayer.create(player, game.getWorld(), game.getType());
        }
    }

}
