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
package de.erethon.dungeonsxl.api.dungeon;

import de.erethon.dungeonsxl.api.world.ResourceWorld;
import java.util.List;

/**
 * A dungeon consists of floors and settings including its game rules.
 * <p>
 * MFD = multiple floor dungeon; SFD = single floor dungeon.
 *
 * @author Daniel Saukel
 */
public interface Dungeon {

    /**
     * Returns the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Sets the name to the given value.
     *
     * @param name the name
     */
    void setName(String name);

    /**
     * Returns if this dungeon has multiple floors.
     *
     * @return if this dungeon has multiple floors
     */
    boolean isMultiFloor();

    /**
     * Returns the map to instantiate.
     * <p>
     * This method is the same as {@link #getStartFloor()} but a bit more intuitive for SFDs.
     *
     * @return the map to instantiate
     */
    default ResourceWorld getMap() {
        return getStartFloor();
    }

    /**
     * Returns the first floor of this dungeon.
     *
     * @return the first floor of this dungeon
     */
    ResourceWorld getStartFloor();

    /**
     * Sets the first floor of this dungeon.
     *
     * @param startFloor the startFloor to set
     */
    void setStartFloor(ResourceWorld startFloor);

    /**
     * Returns the last floor of this dungeon or null if this is an SFD.
     *
     * @return the last floor of this dungeon or null if this is an SFD
     */
    ResourceWorld getEndFloor();

    /**
     * Sets the last floor of this MFD.
     *
     * @param endFloor the last floor
     */
    void setEndFloor(ResourceWorld endFloor);

    /**
     * Returns a list of the floors without start and end floor.
     *
     * @return a list of the floors without start and end floor
     */
    List<ResourceWorld> getFloors();

    /**
     * Adds the given floor.
     *
     * @param resource the resource to add
     */
    void addFloor(ResourceWorld resource);

    /**
     * Removes the given floor.
     *
     * @param resource the resource to remove
     */
    void removeFloor(ResourceWorld resource);

    /**
     * Returns the amount of floors in this dungeon including start and end floor.
     * <p>
     * This may be less than the size of {@link #getFloors()} + 2 if not all floors from the list are used.
     *
     * @return the amount of floors in this dungeon including start and end floor
     */
    int getFloorCount();

    /**
     * Sets the amount of floors that shall be played.
     *
     * @param floorCount the amount of floors to set
     */
    void setFloorCount(int floorCount);

    /**
     * Returns if floors cannot be played once if floors are selected randomly from the list.
     *
     * @return the removeWhenPlayed if floors cannot be played once if floors are selected randomly from the list
     */
    boolean getRemoveWhenPlayed();

    /**
     * Sets if floors cannot be played once if floors are selected randomly from the list.
     *
     * @param removeWhenPlayed if floors cannot be played once if floors are selected randomly from the list
     */
    void setRemoveWhenPlayed(boolean removeWhenPlayed);

    /**
     * The values from this game rule container will override all values of the game rule containers of the dungeon's maps.
     *
     * @return the override values
     */
    GameRuleContainer getOverrideValues();

    /**
     * Sets the game rule container whose values override all values of the game rule containers of the dungeon's maps.
     *
     * @param rules the override values
     */
    void setOverrideValues(GameRuleContainer rules);

    /**
     * The values from this game rule container will be overriden by values of the game rule containers of the dungeon's maps. They will however still override
     * the values from the main config.
     *
     * @return the default values
     */
    GameRuleContainer getDefaultValues();

    /**
     * Sets the game rule container whose values will be overriden by values of the game rule containers of the dungeon's maps. They will however still override
     * the values from the main config.
     *
     * @param rules the default values
     */
    void setDefaultValues(GameRuleContainer rules);

    /**
     * Returns true if the floor is either in the floors list or the start / end floor.
     *
     * @param resource the ResourceWorld to check
     * @return true if the floor is either in the floors list or the start / end floor.
     */
    default boolean containsFloor(ResourceWorld resource) {
        return getFloors().contains(resource) || getStartFloor().equals(resource) || getEndFloor().equals(resource);
    }

    /**
     * Returns true if the floor is either in the floors list or the start / end floor.
     *
     * @param mapName the name of the map to check
     * @return true if the floor is either in the floors list or the start / end floor.
     */
    default boolean containsFloor(String mapName) {
        for (ResourceWorld world : getFloors()) {
            if (world.getName().equals(mapName)) {
                return true;
            }
        }
        return getStartFloor().getName().equals(mapName) || getEndFloor().getName().equals(mapName);
    }

    /**
     * Returns the rules of this game.
     * <p>
     * This is not necessarily represented 1:1 by a config file because it is usually merged together through {@link #setupRules()}.
     *
     * @return the rules of this game
     */
    GameRuleContainer getRules();

    /**
     * Sets the rules of the game.
     *
     * @param rules the rules
     */
    void setRules(GameRuleContainer rules);

    /**
     * Sets up the rules with the following priority: 1. Game type 2. Dungeon config: Override values 3. Floor config 4. Dungeon config: Default values 5. Main
     * config: Default values 6. The default values
     */
    void setupRules();

    /**
     * Returns false if there are errors in the setup; true if not.
     *
     * @return false if there are errors in the setup; true if not
     */
    boolean isSetupCorrect();

}
