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
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class GameCommand extends DCommand {

    public GameCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("game");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.CMD_GAME_HELP.getMessage());
        setPermission(DPermission.GAME.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DGroup dGroup = DGroup.getByPlayer(player);
        if (dGroup == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_JOIN_GROUP.getMessage());
            return;
        }

        DGameWorld gameWorld = dGroup.getGameWorld();
        if (gameWorld == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_GAME.getMessage());
            return;
        }

        Game game = gameWorld.getGame();
        if (game == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_GAME.getMessage());
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
        MessageUtil.sendMessage(sender, "&bWaves finished: &e" + game.getWaveCount());
        MessageUtil.sendMessage(sender, "&bKills: &e" + game.getGameKills() + " / Game; " + game.getWaveKills() + " / Wave");
    }

}
