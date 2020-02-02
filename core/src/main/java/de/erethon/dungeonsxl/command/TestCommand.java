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
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.game.GameTypeDefault;
import de.erethon.dungeonsxl.player.DEditPlayer;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
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
        DGlobalPlayer dPlayer = dPlayers.getByPlayer(player);

        if (!(dPlayer instanceof DEditPlayer)) {
            DGroup dGroup = DGroup.getByPlayer(player);
            if (dGroup == null) {
                MessageUtil.sendMessage(sender, DMessage.ERROR_JOIN_GROUP.getMessage());
                return;
            }

            if (!dGroup.getCaptain().equals(player) && !DPermission.hasPermission(player, DPermission.BYPASS)) {
                MessageUtil.sendMessage(sender, DMessage.ERROR_NOT_LEADER.getMessage());
                return;
            }

            DGameWorld gameWorld = dGroup.getGameWorld();
            if (gameWorld == null) {
                MessageUtil.sendMessage(sender, DMessage.ERROR_NOT_IN_DUNGEON.getMessage());
                return;
            }

            Game game = gameWorld.getGame();
            if (game != null && game.hasStarted()) {
                MessageUtil.sendMessage(sender, DMessage.ERROR_LEAVE_DUNGEON.getMessage());
                return;
            }

            for (Player groupPlayer : dGroup.getPlayers().getOnlinePlayers()) {
                DGamePlayer.getByPlayer(groupPlayer).ready(GameTypeDefault.TEST);
            }

        } else {
            DEditPlayer editPlayer = (DEditPlayer) dPlayer;
            editPlayer.leave();
            DResourceWorld resource = editPlayer.getEditWorld().getResource();
            Dungeon dungeon = new Dungeon(plugin, resource);
            DGameWorld instance = resource.instantiateAsGameWorld(false);
            if (instance == null) {
                MessageUtil.sendMessage(player, DMessage.ERROR_TOO_MANY_INSTANCES.getMessage());
                return;
            }
            Game game = new Game(plugin, new DGroup(plugin, player, dungeon), GameTypeDefault.TEST, instance);
            new DGamePlayer(plugin, player, game.getWorld(), GameTypeDefault.TEST);
        }
    }

}
