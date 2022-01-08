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

    protected DungeonsAPI dxl;
    protected Map<PlayerGroup, T> groups = new HashMap<>();

    /**
     * @param dxl the DungeonsAPI instance
     */
    protected GroupAdapter(DungeonsAPI dxl) {
        this.dxl = dxl;
    }

    /**
     * Creates a dungeon group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the external group.
     *
     * @param eGroup the external group
     * @return a dungeon group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the external group
     */
    public abstract PlayerGroup createDungeonGroup(T eGroup);

    /**
     * Returns the dungeon group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the external group or null of none exists.
     *
     * @param eGroup the external group
     * @return the dungeon group {@link #areCorresponding(PlayerGroup, Object) corresponding} with the external group
     */
    public PlayerGroup getDungeonGroup(T eGroup) {
        if (eGroup == null) {
            return null;
        }
        for (Entry<PlayerGroup, T> entry : groups.entrySet()) {
            if (entry.getValue().equals(eGroup)) {
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
        return groups.get(dGroup);
    }

    /**
     * Returns the dungeon group that mirrors the external group.
     * <p>
     * Creates a dungeon group if none exists and if the party has no more online members than maxSize.
     *
     * @param eGroup  the dungeon group
     * @param maxSize the maximum size of the group
     * @return the dungeon group that mirrors the dungeon group
     */
    public PlayerGroup getOrCreateDungeonGroup(T eGroup, int maxSize) {
        if (eGroup == null) {
            return null;
        }
        PlayerGroup dGroup = getDungeonGroup(eGroup);
        if (dGroup == null && getGroupOnlineSize(eGroup) <= maxSize) {
            dGroup = createDungeonGroup(eGroup);
        }
        return dGroup;
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
        if (eGroup == null) {
            return null;
        }
        PlayerGroup dGroup = getDungeonGroup(eGroup);
        if (dGroup == null) {
            dGroup = createDungeonGroup(eGroup);
        }
        return dGroup;
    }

    /**
     * Returns the external group of the given group member.
     *
     * @param member the group member
     * @return the external group of the given group member
     */
    public abstract T getExternalGroup(Player member);

    /**
     * Returns the amount of members in the external group who are online.
     *
     * @param eGroup the external group
     * @return the amount of members in the external group who are online
     */
    public abstract int getGroupOnlineSize(T eGroup);

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
        T dExternal = groups.get(dGroup);
        return dExternal != null && eGroup.equals(dExternal);
    }

    /**
     * Returns if the player is a member of any external group.
     *
     * @param player the player
     * @return if the player is a member of any external group
     */
    public boolean isExternalGroupMember(Player player) {
        return getExternalGroup(player) != null;
    }

    /**
     * Returns if the player is a member of the external group.
     *
     * @param eGroup the external group
     * @param player the player
     * @return if the player is a member of the external group
     */
    public abstract boolean isExternalGroupMember(T eGroup, Player player);

    /**
     * Clears the external / dungeon group references.
     */
    public void clear() {
        groups.clear();
    }

    /**
     * Removes the external / dungeon group reference from the cache.
     *
     * @param dGroup the DXL group that belongs to an external group.
     */
    public void removeReference(PlayerGroup dGroup) {
        groups.remove(dGroup);
    }

}
