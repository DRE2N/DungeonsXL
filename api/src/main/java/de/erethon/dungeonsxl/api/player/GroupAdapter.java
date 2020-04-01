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

import de.erethon.dungeonsxl.api.DungeonsAPI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.entity.Player;

/**
 * Implement and register in order to track a group.
 * <p>
 * See implementation classes in de.erethon.dungeonsxl.player.groupadapter for reference.
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

    public class ExternalGroupData<T> {

        private T eGroup;
        private boolean createdByDXL;

        public ExternalGroupData(T eGroup, boolean createdByDXL) {
            this.eGroup = eGroup;
            this.createdByDXL = createdByDXL;
        }

        /**
         * Returns the wrapped external group object.
         *
         * @return the wrapped external group object
         */
        public T get() {
            return eGroup;
        }

        /**
         * Returns if the external group was created by DungeonsXL.
         * <p>
         * Groups may be created by DungeonsXL, for example through a command, a group sign or automatically if a dungeon is entered.
         * The integration implementation should give dungeon groups equivalent groups from the external group plugin.
         * External groups created to mirror dungeon groups should be removed when their dungeon group is deleted, but those created intentionally should not.
         *
         * @return if the external group was created by DungeonsXL.
         */
        public boolean isCreatedByDXL() {
            return createdByDXL;
        }

    }

    protected DungeonsAPI dxl;
    private Philosophy philosophy;
    protected Map<PlayerGroup, ExternalGroupData<T>> groups = new HashMap<>();

    /**
     * @param dxl        the DungeonsAPI instance
     * @param philosophy the player handling philosophy
     */
    protected GroupAdapter(DungeonsAPI dxl, Philosophy philosophy) {
        this.dxl = dxl;
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
    public abstract PlayerGroup createDungeonGroup(T eGroup);

    /**
     * Creates an external group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the dungeon group.
     *
     * @param dGroup the dungeon group
     * @return an external group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the dungeon group
     */
    public abstract T createExternalGroup(PlayerGroup dGroup);

    /**
     * Returns the dungeon group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the external group or null of none exists.
     *
     * @param eGroup the external group
     * @return the dungeon group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the external group
     */
    public PlayerGroup getDungeonGroup(T eGroup) {
        for (Entry<PlayerGroup, ExternalGroupData<T>> entry : groups.entrySet()) {
            if (entry.getValue().get().equals(eGroup)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Returns the external group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the dungeon group.
     *
     * @param dGroup the dungeon group
     * @return the external group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the dungeon group
     */
    public T getExternalGroup(PlayerGroup dGroup) {
        ExternalGroupData<T> data = groups.get(dGroup);
        return data != null ? data.get() : null;
    }

    /**
     * Returns the dungeon group that mirrors the external group.
     * <p>
     * Creates a dungeon group if none exists.
     *
     * @param eGroup the dungeon group
     * @return the dungeon group that mirrors the dungeon group
     */
    public PlayerGroup getOrCreateDungeonGroup(T eGroup) {
        PlayerGroup dGroup = getDungeonGroup(eGroup);
        if (dGroup == null) {
            dGroup = createDungeonGroup(eGroup);
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
     * Returns the external group of the given group member.
     *
     * @param member the group member
     * @return the external group of the given group member
     */
    public abstract T getExternalGroup(Player member);

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
    public boolean areCorresponding(PlayerGroup dGroup, T eGroup) {
        if (dGroup == null || eGroup == null) {
            return false;
        }
        ExternalGroupData<T> data = groups.get(dGroup);
        return data != null && eGroup.equals(data.get());
    }

    /**
     * Deletes the external group corresponding with the given dungeon group.
     *
     * @param dGroup the dungeon group corresponding with the external one to delete
     * @return if the deletion was successful
     */
    public abstract boolean deleteCorrespondingGroup(PlayerGroup dGroup);

    /**
     * Checks if the two groups have the same members.
     *
     * @param dGroup the dungeon group
     * @param eGroup the external group
     * @return if the two groups have the same members
     */
    public abstract boolean areSimilar(PlayerGroup dGroup, T eGroup);

    /**
     * Ensures that the player is in {@link #areCorresponding(PlayerGroup, Object) corresponding} groups.
     * <p>
     * If the player is in an external group but not in a corresponding dungeon group, they are added to the corresponding dungeon group.
     * If no dungeon group exists, it is created automatically. Switching dungeon groups forces the player to leave their dungeon.
     * <p>
     * If the player is in a dungeon group but not in an external group, the player is added to the corresponding external group if it exists.
     * If no corresponding external group exists, a new one is created.
     *
     * @param player the player
     */
    public void syncJoin(Player player) {
        T eGroup = getExternalGroup(player);
        PlayerGroup dGroup = dxl.getPlayerGroup(player);

        if (eGroup != null && !areCorresponding(dGroup, eGroup)) {
            if (areSimilar(dGroup, eGroup)) {
                // The groups are not yet marked as corresponding because one of them is still being created.
                return;
            }
            if (dGroup != null) {
                dGroup.removeMember(player, false);
                return;
            }
            dGroup = getDungeonGroup(eGroup);
            if (dGroup != null && !dGroup.getMembers().contains(player)) {
                dGroup.addMember(player);
            } else {
                dGroup = createDungeonGroup(eGroup);
            }

        } else if (eGroup == null && dGroup != null) {
            eGroup = getExternalGroup(dGroup);
            if (eGroup == null) {
                eGroup = createExternalGroup(dGroup);
            }
            if (!isExternalGroupMember(eGroup, player)) {
                addExternalGroupMember(eGroup, player);
            }
        }
    }

    /**
     * Returns if the player is a member of the external group.
     *
     * @param eGroup the external group
     * @param player player
     * @return if the player is a member of the external group
     */
    public abstract boolean isExternalGroupMember(T eGroup, Player player);

    /**
     * Adds the member to the external group.
     *
     * @param eGroup the external group
     * @param member the member
     * @return if adding the member was successful
     */
    public abstract boolean addExternalGroupMember(T eGroup, Player member);

    /**
     * Removes the member from the external group.
     *
     * @param eGroup the external group
     * @param member the member
     * @return if removing the player was successful
     */
    public abstract boolean removeExternalGroupMember(T eGroup, Player member);

}
