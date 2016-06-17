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
package io.github.dre2n.dungeonsxl.task;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.announcer.Announcer;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.util.ProgressBar;
import java.util.HashSet;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class AnnouncerStartGameTask extends BukkitRunnable {

    private Announcer announcer;
    private ProgressBar bar;

    public AnnouncerStartGameTask(Announcer announcer) {
        this.announcer = announcer;

        HashSet<Player> players = new HashSet<>();
        for (DGroup dGroup : announcer.getDGroups()) {
            if (dGroup == null) {
                continue;
            }
            for (Player player : dGroup.getPlayers()) {
                players.add(player);
            }
        }
        bar = new ProgressBar(players, 30);
        bar.runTaskTimer(DungeonsXL.getInstance(), 0L, 20L);
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
                game = new Game(dGroup, announcer.getMapName());
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
            new DGamePlayer(player, game.getWorld());
        }

        announcer.endStartTask();
    }

}
