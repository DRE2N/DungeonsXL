/*
 * Copyright (C) 2012-2017 Frank Baumann
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
package io.github.dre2n.dungeonsxl.player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * DGlobalPlayer instance manager.
 *
 * @author Daniel Saukel
 */
public class DPlayers {

    private CopyOnWriteArrayList<DGlobalPlayer> dGlobalPlayers = new CopyOnWriteArrayList<>();

    /**
     * @return the DGlobalPlayer which represents the player
     */
    public DGlobalPlayer getByPlayer(Player player) {
        for (DGlobalPlayer dGlobalPlayer : dGlobalPlayers) {
            if (dGlobalPlayer.getPlayer() == player) {
                return dGlobalPlayer;
            }
        }

        return new DGlobalPlayer(player);
    }

    /**
     * @return the dGlobalPlayers
     */
    public List<DGlobalPlayer> getDGlobalPlayers() {
        return dGlobalPlayers;
    }

    /**
     * @return the dGlobalPlayers that are an instance of DInstancePlayer
     */
    public List<DInstancePlayer> getDInstancePlayers() {
        List<DInstancePlayer> dInstancePlayers = new ArrayList<>();
        for (DGlobalPlayer player : dGlobalPlayers) {
            if (player instanceof DInstancePlayer) {
                dInstancePlayers.add((DInstancePlayer) player);
            }
        }
        return dInstancePlayers;
    }

    /**
     * @return the dGlobalPlayers that are an instance of DGamePlayer
     */
    public List<DGamePlayer> getDGamePlayers() {
        List<DGamePlayer> dPlayers = new ArrayList<>();
        for (DGlobalPlayer player : dGlobalPlayers) {
            if (player instanceof DGamePlayer) {
                dPlayers.add((DGamePlayer) player);
            }
        }
        return dPlayers;
    }

    /**
     * @return the dGlobalPlayers that are an instance of DEditPlayer
     */
    public List<DEditPlayer> getDEditPlayers() {
        List<DEditPlayer> dEditPlayers = new ArrayList<>();
        for (DGlobalPlayer player : dGlobalPlayers) {
            if (player instanceof DEditPlayer) {
                dEditPlayers.add((DEditPlayer) player);
            }
        }
        return dEditPlayers;
    }

    /**
     * @param player
     * an instance of DGlobalPlayer to add
     */
    public void addPlayer(DGlobalPlayer player) {
        removePlayer(player);
        dGlobalPlayers.add(player);
    }

    /**
     * @param player
     * an instance of DGlobalPlayer to remove
     */
    public void removePlayer(DGlobalPlayer player) {
        for (DGlobalPlayer dGlobalPlayer : dGlobalPlayers) {
            if (dGlobalPlayer.getPlayer().equals(player.getPlayer())) {
                dGlobalPlayers.remove(dGlobalPlayer);
            }
        }
    }

    /**
     * Load all players
     */
    public void loadAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            new DGlobalPlayer(player);
        }
    }

    /**
     * Checks if an old DGamePlayer instance of the user exists.
     * If yes, the old Player of the user is replaced with the new object.
     *
     * @param player
     * the player to check
     * @return if the player exists
     */
    public boolean checkPlayer(Player player) {
        DGamePlayer dPlayer = DGamePlayer.getByName(player.getName());
        if (dPlayer == null) {
            return false;
        }

        DGroup dGroup = DGroup.getByPlayer(dPlayer.getPlayer());
        if (dGroup != null) {
            dGroup.removePlayer(dPlayer.getPlayer());
            dGroup.addPlayer(player);
        }
        dPlayer.setPlayer(player);
        dPlayer.setOfflineTime(0);
        return true;
    }

}
