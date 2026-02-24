/*
 * Copyright (C) 2015-2026 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api.player;

import de.erethon.xlib.util.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.entity.Player;

/**
 * Stores information on players in and out of dungeons.
 *
 * @author Daniel Saukel
 */
public class PlayerCache extends Registry<Player, GlobalPlayer> {

    /**
     * Returns the {@link GlobalPlayer} that represents the player with the given UUID.
     *
     * @param uuid a player's UUID to check
     * @return the {@link GlobalPlayer} that represents the given player both for players in dungeons or outside; null if they are offline
     */
    public GlobalPlayer get(UUID uuid) {
        return getFirstIf(p -> p.getUniqueId().equals(uuid));
    }

    /**
     * Returns the {@link InstancePlayer} that represents the given player.
     *
     * @param player the player to check
     * @return the {@link InstancePlayer} that represents the given player; null if the player is neither editing nor in a game.
     */
    public InstancePlayer getInstancePlayer(Player player) {
        return getFirstInstancePlayerIf(p -> p.getPlayer() == player);
    }

    /**
     * Returns the {@link EditPlayer} that represents the given player.
     *
     * @param player the player to check
     * @return the {@link EditPlayer} that represents the given player; null if the player is not editing
     */
    public EditPlayer getEditPlayer(Player player) {
        return getFirstEditPlayerIf(p -> p.getPlayer() == player);
    }

    /**
     * Returns the {@link GamePlayer} that represents the given player.
     *
     * @param player the player to check
     * @return the {@link GamePlayer} that represents the given player; null if the player is not in a game
     */
    public GamePlayer getGamePlayer(Player player) {
        return getFirstGamePlayerIf(p -> p.getPlayer() == player);
    }

    /**
     * Returns the first {@link InstancePlayer} that satisfies the given predicate.
     *
     * @param predicate the predicate to check
     * @return the first {@link InstancePlayer} that satisfies the given predicate
     */
    public InstancePlayer getFirstInstancePlayerIf(Predicate<InstancePlayer> predicate) {
        for (GlobalPlayer element : elements.values()) {
            if (!(element instanceof InstancePlayer)) {
                continue;
            }
            InstancePlayer instancePlayer = (InstancePlayer) element;
            if (predicate.test(instancePlayer)) {
                return instancePlayer;
            }
        }
        return null;
    }

    /**
     * Returns the first {@link EditPlayer} that satisfies the given predicate.
     *
     * @param predicate the predicate to check
     * @return the first {@link EditPlayer} that satisfies the given predicate
     */
    public EditPlayer getFirstEditPlayerIf(Predicate<EditPlayer> predicate) {
        for (GlobalPlayer element : elements.values()) {
            if (!(element instanceof EditPlayer)) {
                continue;
            }
            EditPlayer editPlayer = (EditPlayer) element;
            if (predicate.test(editPlayer)) {
                return editPlayer;
            }
        }
        return null;
    }

    /**
     * Returns the first {@link GamePlayer} that satisfies the given predicate.
     *
     * @param predicate the predicate to check
     * @return the first {@link GamePlayer} that satisfies the given predicate
     */
    public GamePlayer getFirstGamePlayerIf(Predicate<GamePlayer> predicate) {
        for (GlobalPlayer element : elements.values()) {
            if (!(element instanceof GamePlayer)) {
                continue;
            }
            GamePlayer gamePlayer = (GamePlayer) element;
            if (predicate.test(gamePlayer)) {
                return gamePlayer;
            }
        }
        return null;
    }

    /**
     * Returns all {@link InstancePlayer}s that satisfy the given predicate.
     *
     * @param predicate the predicate to check
     * @return all {@link InstancePlayer} that satisfy the given predicate
     */
    public Collection<InstancePlayer> getAllInstancePlayersIf(Predicate<InstancePlayer> predicate) {
        Collection<InstancePlayer> checked = new ArrayList<>();
        for (GlobalPlayer element : elements.values()) {
            if (!(element instanceof InstancePlayer)) {
                continue;
            }
            InstancePlayer instancePlayer = (InstancePlayer) element;
            if (predicate.test(instancePlayer)) {
                checked.add(instancePlayer);
            }
        }
        return checked;
    }

    /**
     * Returns all {@link EditPlayer}s that satisfy the given predicate.
     *
     * @param predicate the predicate to check
     * @return all {@link EditPlayer}s that satisfy the given predicate
     */
    public Collection<EditPlayer> getAllEditPlayersIf(Predicate<EditPlayer> predicate) {
        Collection<EditPlayer> checked = new ArrayList<>();
        for (GlobalPlayer element : elements.values()) {
            if (!(element instanceof EditPlayer)) {
                continue;
            }
            EditPlayer editPlayer = (EditPlayer) element;
            if (predicate.test(editPlayer)) {
                checked.add(editPlayer);
            }
        }
        return checked;
    }

    /**
     * Returns all {@link GamePlayer}s that satisfy the given predicate.
     *
     * @param predicate the predicate to check
     * @return all {@link GamePlayer} that satisfy the given predicate
     */
    public Collection<GamePlayer> getAllGamePlayersIf(Predicate<GamePlayer> predicate) {
        Collection<GamePlayer> checked = new ArrayList<>();
        for (GlobalPlayer element : elements.values()) {
            if (!(element instanceof GamePlayer)) {
                continue;
            }
            GamePlayer gamePlayer = (GamePlayer) element;
            if (predicate.test(gamePlayer)) {
                checked.add(gamePlayer);
            }
        }
        return checked;
    }

    /**
     * Returns all {@link InstancePlayer}s.
     *
     * @return all {@link InstancePlayer}s
     */
    public Collection<InstancePlayer> getAllInstancePlayers() {
        Collection<InstancePlayer> checked = new ArrayList<>();
        for (GlobalPlayer element : elements.values()) {
            if (element instanceof InstancePlayer) {
                checked.add((InstancePlayer) element);
            }
        }
        return checked;
    }

    /**
     * Returns all {@link EditPlayer}s.
     *
     * @return all {@link EditPlayer}s
     */
    public Collection<EditPlayer> getAllEditPlayers() {
        Collection<EditPlayer> checked = new ArrayList<>();
        for (GlobalPlayer element : elements.values()) {
            if (element instanceof EditPlayer) {
                checked.add((EditPlayer) element);
            }
        }
        return checked;
    }

    /**
     * Returns all {@link GamePlayer}s.
     *
     * @return all {@link GamePlayer}s
     */
    public Collection<GamePlayer> getAllGamePlayers() {
        Collection<GamePlayer> checked = new ArrayList<>();
        for (GlobalPlayer element : elements.values()) {
            if (element instanceof GamePlayer) {
                checked.add((GamePlayer) element);
            }
        }
        return checked;
    }

}
