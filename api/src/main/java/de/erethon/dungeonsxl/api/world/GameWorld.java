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
package de.erethon.dungeonsxl.api.world;

import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.mob.DungeonMob;
import java.util.Collection;
import org.bukkit.Location;

/**
 * A playable resource instance. There may be any amount of GameWorlds per {@link ResourceWorld}.
 * <p>
 * A game world is not equal to a {@link de.erethon.dungeonsxl.api.dungeon.Dungeon}.
 *
 * @author Daniel Saukel
 */
// Implementation-specific methods: [gameblock, secure objects, classes signs, mobs, triggers] methods, getMobCount(), setPlaying(), startGame(), listener methods
public interface GameWorld extends InstanceWorld {

    enum Type {
        START_FLOOR,
        END_FLOOR,
        DEFAULT
    }

    /**
     * Returns the {@link Type} of this GameWorld.
     *
     * @return the {@link Type} of this GameWorld
     */
    Type getType();

    /**
     * Sets the {@link Type} of this GameWorld.
     *
     * @param type the type
     */
    void setType(Type type);

    /**
     * Returns the game that is played in the game world.
     *
     * @return the game that is played in the game world
     */
    Game getGame();

    /**
     * Returns the dungeon that the game world is part of.
     * <p>
     * Note: While a {@link ResourceWorld} may be part of multiple dungeons, an instance is instantiated per game and thus has just one dungeon.
     *
     * @return the dungeon that the game world is part of
     */
    Dungeon getDungeon();

    /**
     * Returns the living dungeon mobs
     *
     * @return the living dungeon mobs
     */
    Collection<DungeonMob> getMobs();

    /**
     * Registers the given dungeon mob
     *
     * @param mob the mob
     */
    void addMob(DungeonMob mob);

    /**
     * Unregisters the given dungeon mob
     *
     * @param mob the mob
     */
    public void removeMob(DungeonMob mob);

    /**
     * Returns if the game has begun in the game world.
     *
     * @return if the game has begun in the game world
     */
    boolean isPlaying();

    /**
     * Returns the start location of the world. This may be set by a start {@link de.erethon.dungeonsxl.api.sign.DungeonSign sign} or, if none exists, the
     * Vanilla spawn location of the {@link #getWorld() world}.
     *
     * @return the start location of the world
     */
    Location getStartLocation();

}
