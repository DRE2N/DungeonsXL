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
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.event.group.GroupCreateEvent;
import de.erethon.dungeonsxl.api.player.GlobalPlayer;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.DGame;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DInstancePlayer;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class PlayCommand extends DCommand {

    public PlayCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("play");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(DMessage.CMD_PLAY_HELP.getMessage());
        setPermission(DPermission.PLAY.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        GlobalPlayer dPlayer = dPlayers.get(player);
        if (dPlayer instanceof DInstancePlayer) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_DUNGEON.getMessage());
            return;
        }

        Dungeon dungeon = plugin.getDungeonRegistry().get(args[1]);
        if (dungeon == null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_NO_SUCH_DUNGEON.getMessage(args[1]));
            return;
        }

        DGroup group = (DGroup) dPlayer.getGroup();
        if (group != null && group.isPlaying()) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_GROUP.getMessage());
            return;
        } else if (group == null) {
            group = DGroup.create(plugin, GroupCreateEvent.Cause.COMMAND, player, null, null, dungeon);
            if (group == null) {
                return;
            }
        }
        if (!group.getLeader().equals(player) && !DPermission.hasPermission(player, DPermission.BYPASS)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_NOT_LEADER.getMessage());
            return;
        }
        group.setDungeon(dungeon);

        if (!dPlayer.checkRequirements(dungeon)) {
            return;
        }

        Game game = new DGame(plugin, dungeon, group);
        GameWorld gameWorld = game.ensureWorldIsLoaded(false);
        if (gameWorld == null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_TOO_MANY_INSTANCES.getMessage());
            return;
        }
        for (Player groupPlayer : group.getMembers().getOnlinePlayers()) {
            new DGamePlayer(plugin, groupPlayer, group.getGameWorld());
        }
    }

}
