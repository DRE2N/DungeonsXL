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
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.config.DungeonConfig;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupCreateEvent;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class PlayCommand extends BRCommand {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public PlayCommand() {
        setCommand("play");
        setMinArgs(1);
        setMaxArgs(2);
        setHelp(DMessages.HELP_CMD_PLAY.getMessage());
        setPermission(DPermissions.PLAY.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);

        if (dPlayer != null) {
            MessageUtil.sendMessage(player, DMessages.ERROR_LEAVE_DUNGEON.getMessage());
            return;
        }

        if (!(args.length >= 2 && args.length <= 3)) {
            displayHelp(player);
            return;
        }

        String identifier = args[1];
        String mapName = identifier;

        boolean multiFloor = false;
        if (args.length == 3) {
            identifier = args[2];
            mapName = identifier;
            if (args[1].equalsIgnoreCase("dungeon") || args[1].equalsIgnoreCase("d")) {
                Dungeon dungeon = plugin.getDungeons().getByName(args[2]);
                if (dungeon != null) {
                    multiFloor = true;
                    mapName = dungeon.getConfig().getStartFloor();
                } else {
                    displayHelp(player);
                    return;
                }

            } else if (args[1].equalsIgnoreCase("map") || args[1].equalsIgnoreCase("m")) {
                identifier = args[2];
            }
        }

        if (!multiFloor && !EditWorld.exists(identifier)) {
            MessageUtil.sendMessage(player, DMessages.ERROR_DUNGEON_NOT_EXIST.getMessage(identifier));
            return;
        }

        DGroup dGroup = DGroup.getByPlayer(player);

        if (dGroup != null) {
            if (!dGroup.getCaptain().equals(player) && !DPermissions.hasPermission(player, DPermissions.BYPASS)) {
                MessageUtil.sendMessage(player, DMessages.ERROR_NOT_CAPTAIN.getMessage());
            }

            if (dGroup.getMapName() == null) {
                if (!multiFloor) {
                    dGroup.setMapName(identifier);

                } else {
                    dGroup.setDungeonName(identifier);
                    Dungeon dungeon = plugin.getDungeons().getByName(identifier);

                    if (dungeon != null) {
                        DungeonConfig config = dungeon.getConfig();

                        if (config != null) {
                            dGroup.setMapName(config.getStartFloor());
                        }
                    }
                }

            } else {
                MessageUtil.sendMessage(player, DMessages.ERROR_LEAVE_GROUP.getMessage());
                return;
            }

        } else {
            dGroup = new DGroup(player, identifier, multiFloor);
        }

        DGroupCreateEvent event = new DGroupCreateEvent(dGroup, player, DGroupCreateEvent.Cause.COMMAND);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            plugin.getDGroups().remove(dGroup);
            dGroup = null;
        }

        if (dGroup == null) {
            return;
        }

        if (dGroup.getGameWorld() == null) {
            new Game(dGroup, dGroup.getMapName());
        }

        if (dGroup.getGameWorld() == null) {
            MessageUtil.sendMessage(player, DMessages.ERROR_NOT_SAVED.getMessage(DGroup.getByPlayer(player).getMapName()));
            dGroup.delete();
            return;
        }

        if (dGroup.getGameWorld().getLobbyLocation() == null) {
            for (Player groupPlayer : dGroup.getPlayers()) {
                new DGamePlayer(groupPlayer, dGroup.getGameWorld());
            }

        } else {
            for (Player groupPlayer : dGroup.getPlayers()) {
                new DGamePlayer(groupPlayer, dGroup.getGameWorld());
            }
        }
    }

}
