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
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameTypeDefault;
import io.github.dre2n.dungeonsxl.player.DEditPlayer;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGlobalPlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPermission;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import io.github.dre2n.dungeonsxl.world.DResourceWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class TestCommand extends DRECommand {

    public TestCommand() {
        setCommand("test");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.HELP_CMD_TEST.getMessage());
        setPermission(DPermission.TEST.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DGlobalPlayer dPlayer = DungeonsXL.getInstance().getDPlayers().getByPlayer(player);

        if (!(dPlayer instanceof DEditPlayer)) {
            DGroup dGroup = DGroup.getByPlayer(player);
            if (dGroup == null) {
                MessageUtil.sendMessage(sender, DMessage.ERROR_JOIN_GROUP.getMessage());
                return;
            }

            if (!dGroup.getCaptain().equals(player) && !DPermission.hasPermission(player, DPermission.BYPASS)) {
                MessageUtil.sendMessage(sender, DMessage.ERROR_NOT_CAPTAIN.getMessage());
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
            Dungeon dungeon = new Dungeon(resource);
            DGameWorld instance = resource.instantiateAsGameWorld();
            Game game = new Game(new DGroup(player, dungeon), GameTypeDefault.TEST, instance);
            DGamePlayer.create(player, game.getWorld(), GameTypeDefault.TEST);
        }
    }

}
