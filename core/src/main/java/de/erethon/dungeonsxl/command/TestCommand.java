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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.player.GlobalPlayer;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.DGame;
import de.erethon.dungeonsxl.player.DEditPlayer;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class TestCommand extends DCommand {

    public TestCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("test");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.CMD_TEST_HELP.getMessage());
        setPermission(DPermission.TEST.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        GlobalPlayer dPlayer = dPlayers.get(player);

        if (!(dPlayer instanceof DEditPlayer)) {
            PlayerGroup dGroup = dPlayer.getGroup();
            if (dGroup == null) {
                MessageUtil.sendMessage(sender, DMessage.ERROR_JOIN_GROUP.getMessage());
                return;
            }

            if (!dGroup.getLeader().equals(player) && !DPermission.hasPermission(player, DPermission.BYPASS)) {
                MessageUtil.sendMessage(sender, DMessage.ERROR_NOT_LEADER.getMessage());
                return;
            }

            GameWorld gameWorld = dGroup.getGameWorld();
            if (gameWorld == null) {
                MessageUtil.sendMessage(sender, DMessage.ERROR_NOT_IN_DUNGEON.getMessage());
                return;
            }

            Game game = gameWorld.getGame();
            if (game != null && game.hasStarted()) {
                MessageUtil.sendMessage(sender, DMessage.ERROR_LEAVE_DUNGEON.getMessage());
                return;
            }

            for (Player groupPlayer : dGroup.getMembers().getOnlinePlayers()) {
                ((DGamePlayer) dPlayers.getGamePlayer(groupPlayer)).ready();
            }

        } else {
            DEditPlayer editPlayer = (DEditPlayer) dPlayer;
            editPlayer.leave();
            ResourceWorld resource = editPlayer.getEditWorld().getResource();
            Dungeon dungeon = resource.getSingleFloorDungeon();
            GameWorld instance = resource.instantiateGameWorld(false);
            if (instance == null) {
                MessageUtil.sendMessage(player, DMessage.ERROR_TOO_MANY_INSTANCES.getMessage());
                return;
            }
            DGame game = new DGame(plugin, new DGroup(plugin, player, dungeon), instance);
            new DGamePlayer(plugin, player, game.getWorld());
        }
    }

}
