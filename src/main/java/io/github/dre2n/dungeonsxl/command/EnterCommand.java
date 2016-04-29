/*
 * Copyright (C) 2016 Daniel Saukel
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
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class EnterCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();
    protected static MessageConfig messageConfig = plugin.getMessageConfig();

    public EnterCommand() {
        setMinArgs(1);
        setMaxArgs(2);
        setCommand("enter");
        setHelp(messageConfig.getMessage(DMessages.HELP_CMD_ENTER));
        setPermission(DPermissions.ENTER.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player captain = (Player) sender;
        String targetName = args.length == 3 ? args[2] : args[1];

        DGroup joining = args.length == 3 ? DGroup.getByName(args[1]) : DGroup.getByPlayer(captain);
        DGroup target = DGroup.getByName(targetName);

        if (target == null) {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(DMessages.ERROR_NO_SUCH_GROUP, targetName));
            return;
        }

        Game game = Game.getByDGroup(target);
        if (game == null) {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(DMessages.ERROR_NOT_IN_GAME, targetName));
            return;
        }

        if (Game.getByDGroup(joining) != null) {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(DMessages.ERROR_LEAVE_GAME));
            return;
        }

        if (joining == null) {
            joining = new DGroup(captain, game.getWorld().getMapName(), game.getDungeon() != null);
        }

        if (joining.getCaptain() != captain && !DPermissions.hasPermission(sender, DPermissions.BYPASS)) {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(DMessages.ERROR_NOT_CAPTAIN));
            return;
        }

        joining.setGameWorld(game.getWorld());
        game.addDGroup(joining);
        joining.sendMessage(messageConfig.getMessage(DMessages.CMD_ENTER_SUCCESS, joining.getName(), targetName));

        for (Player player : joining.getPlayers()) {
            new DPlayer(player, game.getWorld()).ready();
        }
    }

}
