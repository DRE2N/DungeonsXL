/*
 * Copyright (C) 2014-2020 Daniel Saukel
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

import org.bukkit.entity.Player;

/**
 * Implement and register in order to track a group.
 *
 * @param <T> the external group object
 * @author Daniel Saukel
 */
public abstract class GroupAdapter<T> {

    /**
     * How the implementation handles players.
     */
    public enum Philosophy {
        /**
         * The group persists upon restarts.
         * <p>
         * DungeonsXL under no circumstances creates persistent external groups.
         */
        PERSISTENT,
        /**
         * The group continues to exist as long as the server is running, but does not persist upon restarts.
         */
        RUNTIME,
        /**
         * Players are kicked from the group when they leave the server.
         */
        ONLINE
    }

    private Philosophy philosophy;

    /**
     * @param philosophy the player handling philosophy
     */
    protected GroupAdapter(Philosophy philosophy) {
        this.philosophy = philosophy;
    }

    /**
     * Returns the player handling philosophy.
     *
     * @return the player handling philosophy
     */
    public Philosophy getPhilosophy() {
        return philosophy;
    }

    /**
     * Creates a dungeon group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the external group.
     *
     * @param eGroup the external group
     * @return a dungeon group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the external group
     */
    public abstract PlayerGroup createPlayerGroup(T eGroup);

    /**
     * Creates an external group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the dungeon
     * group.
     *
     * @param dGroup the dungeon group
     * @return an external group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the dungeon group
     */
    public abstract T createExternalGroup(PlayerGroup dGroup);

    /**
     * Returns the dungeon group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the external
     * group or null of none exists.
     *
     * @param eGroup the external group
     * @return the dungeon group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the external
     *         group
     */
    public abstract PlayerGroup getPlayerGroup(T eGroup);

    /**
     * Returns the external group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the dungeon
     * group.
     *
     * @param dGroup the dungeon group
     * @return the external group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the dungeon
     *         group
     */
    public abstract T getExternalGroup(PlayerGroup dGroup);

    /**
     * Returns the dungeon group that mirrors the external group.
     * <p>
     * Creates a dungeon group if none exists.
     *
     * @param eGroup the dungeon group
     * @return the dungeon group that mirrors the dungeon group
     */
    public PlayerGroup getOrCreatePlayerGroup(T eGroup) {
        PlayerGroup dGroup = getPlayerGroup(eGroup);
        if (dGroup == null) {
            dGroup = createPlayerGroup(eGroup);
        }
        return dGroup;
    }

    /**
     * Returns the external group that mirrors the dungeon group.
     * <p>
     * Creates an external group if none exists.
     *
     * @param dGroup the dungeon group
     * @return the external group that mirrors the dungeon group
     */
    public T getOrCreateExternalGroup(PlayerGroup dGroup) {
        T eGroup = getExternalGroup(dGroup);
        if (eGroup == null && getPhilosophy() != Philosophy.PERSISTENT) {
            eGroup = createExternalGroup(dGroup);
        }
        return eGroup;
    }

    /**
     * Checks if two groups are corresponding.
     * <p>
     * Corresponding groups are groups that should be regarded as one from the perspective of a player.
     * <p>
     * Two null values are regarded as corresponding.
     *
     * @param dGroup the dungeon group
     * @param eGroup the external group
     * @return if the two groups are corresponding
     */
    public abstract boolean areCorresponding(PlayerGroup dGroup, T eGroup);

    /**
     * Ensures that the player is in {@link #areCorresponding(PlayerGroup, Object) corresponding} groups.
     * <p>
     * If the player is in an external group but not in a corresponding dungeon group, they are added to the corresponding dungeon group.
     * If no dungeon group exists, it is created automatically. Switching dungeon groups forces the player to leave their dungeon.
     * <p>
     * If the player is in a dungeon group but not in an external group, the player is added to the corresponding external group if it exists.
     * If no corresponding external group exists, a new one is only created if the {@link #getPhilosophy() philosophy} is either
     * {@link Philosophy#RUNTIME} or {@link Philosophy#ONLINE}.
     *
     * @param player the player
     */
    public void syncPlayer(Player player) {
        throw new UnsupportedOperationException("TODO");
    }

}
