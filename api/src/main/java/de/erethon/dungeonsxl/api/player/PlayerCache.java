/*
 * Copyright (C) 2014-2022 Daniel Saukel
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

import de.erethon.commons.misc.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class PlayerCache extends Registry<Player, GlobalPlayer> {

    public GlobalPlayer get(UUID uuid) {
        return getFirstIf(p -> p.getUniqueId().equals(uuid));
    }

    public InstancePlayer getInstancePlayer(Player player) {
        return getFirstInstancePlayerIf(p -> p.getPlayer() == player);
    }

    public EditPlayer getEditPlayer(Player player) {
        return getFirstEditPlayerIf(p -> p.getPlayer() == player);
    }

    public GamePlayer getGamePlayer(Player player) {
        return getFirstGamePlayerIf(p -> p.getPlayer() == player);
    }

    /**
     * Returns the first InstancePlayer that satisfies the given predicate.
     *
     * @param predicate the predicate to check
     * @return the first InstancePlayer that satisfies the given predicate
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
     * Returns the first EditPlayer that satisfies the given predicate.
     *
     * @param predicate the predicate to check
     * @return the first EditPlayer that satisfies the given predicate
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
     * Returns the first GamePlayer that satisfies the given predicate.
     *
     * @param predicate the predicate to check
     * @return the first GamePlayer that satisfies the given predicate
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
     * Returns all InstancePlayers that satisfy the given predicate.
     *
     * @param predicate the predicate to check
     * @return all InstancePlayers that satisfy the given predicate
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
     * Returns all EditPlayer that satisfy the given predicate.
     *
     * @param predicate the predicate to check
     * @return all EditPlayer that satisfy the given predicate
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
     * Returns all GamePlayer that satisfy the given predicate.
     *
     * @param predicate the predicate to check
     * @return all GamePlayer that satisfy the given predicate
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
     * Returns all InstancePlayers.
     *
     * @return all InstancePlayers
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
     * Returns all EditPlayers.
     *
     * @return all EditPlayers
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
     * Returns all GamePlayers.
     *
     * @return all GamePlayers
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
