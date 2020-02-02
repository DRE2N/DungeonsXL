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
package de.erethon.dungeonsxl.announcer;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.util.ProgressBar;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import java.util.HashSet;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class AnnouncerStartGameTask extends BukkitRunnable {

    private DungeonsXL plugin;

    private Announcer announcer;
    private ProgressBar bar;

    public AnnouncerStartGameTask(DungeonsXL plugin, Announcer announcer) {
        this.plugin = plugin;

        this.announcer = announcer;
        HashSet<Player> players = new HashSet<>();
        for (DGroup dGroup : announcer.getDGroups()) {
            if (dGroup == null) {
                continue;
            }
            for (Player player : dGroup.getPlayers().getOnlinePlayers()) {
                players.add(player);
            }
        }
        bar = new ProgressBar(players, 30);
        bar.send(plugin);
    }

    /**
     * @return the progress bar the players see until they get teleported
     */
    public ProgressBar getProgressBar() {
        return bar;
    }

    @Override
    public void run() {
        if (!announcer.areRequirementsFulfilled()) {
            cancel();
            return;
        }

        Game game = null;

        for (DGroup dGroup : announcer.getDGroups()) {
            if (dGroup == null) {
                continue;
            }

            if (game == null) {
                DResourceWorld resource = plugin.getDWorldCache().getResourceByName(announcer.getMapName());
                if (resource == null) {
                    dGroup.sendMessage(DMessage.ERROR_NO_SUCH_MAP.getMessage(announcer.getMapName()));
                    cancel();
                    return;
                }
                DGameWorld gameWorld = resource.instantiateAsGameWorld(false);
                if (gameWorld == null) {
                    dGroup.sendMessage(DMessage.ERROR_TOO_MANY_INSTANCES.getMessage());
                    cancel();
                    return;
                }
                game = new Game(plugin, dGroup, gameWorld);
            } else {
                game.getDGroups().add(dGroup);
            }

            dGroup.setDungeon(announcer.getDungeonName() == null ? announcer.getMapName() : announcer.getDungeonName());
            dGroup.setGameWorld(game.getWorld());
        }

        if (game == null) {
            cancel();
            return;
        }

        for (Player player : game.getPlayers()) {
            new DGamePlayer(plugin, player, game.getWorld());
        }

        announcer.endStartTask();
    }

}
