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
import io.github.dre2n.dungeonsxl.world.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class GameCommand extends BRCommand {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();
    protected static MessageConfig messageConfig = plugin.getMessageConfig();

    public GameCommand() {
        setCommand("game");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(messageConfig.getMessage(Messages.HELP_CMD_GAME));
        setPermission("dxl.game");
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

        GameWorld gameWorld = dGroup.getGameWorld();
        if (gameWorld == null) {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_NO_GAME));
            return;
        }

        Game game = gameWorld.getGame();
        if (game == null) {
            MessageUtil.sendMessage(sender, messageConfig.getMessage(Messages.ERROR_NO_GAME));
            return;
        }

        MessageUtil.sendCenteredMessage(sender, "&4&l[ &6Game &4&l]");
        String groups = "";
        for (DGroup group : game.getDGroups()) {
            groups += (group == game.getDGroups().get(0) ? "" : "&b, &e") + group.getName();
        }
        MessageUtil.sendMessage(sender, "&bGroups: &e" + groups);
        MessageUtil.sendMessage(sender, "&bGame type: &e" + (game.getType() == null ? "Not started yet" : game.getType()));
        MessageUtil.sendMessage(sender, "&bDungeon: &e" + (dGroup.getDungeonName() == null ? "N/A" : dGroup.getDungeonName()));
        MessageUtil.sendMessage(sender, "&bMap: &e" + (dGroup.getMapName() == null ? "N/A" : dGroup.getMapName()));
    }

}
