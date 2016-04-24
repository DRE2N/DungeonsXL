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
import io.github.dre2n.dungeonsxl.config.MessageConfig;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameTypeDefault;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class TestCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();
    protected static MessageConfig messageConfig = plugin.getMessageConfig();

    public TestCommand() {
        setCommand("test");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(messageConfig.getMessage(Messages.HELP_CMD_TEST));
        setPermission("dxl.test");
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;

        DGroup dGroup = DGroup.getByPlayer(player);
        if (dGroup == null) {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_JOIN_GROUP));
            return;
        }

        if (!dGroup.getCaptain().equals(player)) {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_NOT_CAPTAIN));
            return;
        }

        GameWorld gameWorld = dGroup.getGameWorld();
        if (gameWorld == null) {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_NOT_IN_DUNGEON));
            return;
        }

        Game game = gameWorld.getGame();
        if (game != null) {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_LEAVE_DUNGEON));
            return;
        }

        for (Player groupPlayer : dGroup.getPlayers()) {
            DPlayer.getByPlayer(groupPlayer).ready(GameTypeDefault.TEST);
        }
    }

}
