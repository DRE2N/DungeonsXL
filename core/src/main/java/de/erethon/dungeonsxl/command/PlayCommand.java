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
import de.erethon.dungeonsxl.event.dgroup.DGroupCreateEvent;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.player.DInstancePlayer;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import org.bukkit.Bukkit;
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
        DGlobalPlayer dPlayer = dPlayers.getByPlayer(player);
        if (dPlayer instanceof DInstancePlayer) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_DUNGEON.getMessage());
            return;
        }

        Dungeon dungeon = plugin.getDungeonCache().getByName(args[1]);
        if (dungeon == null) {
            DResourceWorld resource = instances.getResourceByName(args[1]);
            if (resource != null) {
                dungeon = new Dungeon(plugin, resource);
            } else {
                MessageUtil.sendMessage(player, DMessage.ERROR_NO_SUCH_DUNGEON.getMessage(args[1]));
                return;
            }
        }

        DGroup dGroup = DGroup.getByPlayer(player);
        if (dGroup != null && dGroup.isPlaying()) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_GROUP.getMessage());
            return;
        } else if (dGroup == null) {
            dGroup = new DGroup(plugin, player, dungeon);
            DGroupCreateEvent event = new DGroupCreateEvent(dGroup, player, DGroupCreateEvent.Cause.COMMAND);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                plugin.getDGroupCache().remove(dGroup);
                dGroup = null;
            }
        }
        if (!dGroup.getCaptain().equals(player) && !DPermission.hasPermission(player, DPermission.BYPASS)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_NOT_LEADER.getMessage());
            return;
        }
        dGroup.setDungeon(dungeon);

        DGameWorld gameWorld = dungeon.getMap().instantiateAsGameWorld(false);
        if (gameWorld == null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_TOO_MANY_INSTANCES.getMessage());
            return;
        }
        new Game(plugin, dGroup, gameWorld);
        for (Player groupPlayer : dGroup.getPlayers().getOnlinePlayers()) {
            new DGamePlayer(plugin, groupPlayer, dGroup.getGameWorld());
        }
    }

}
