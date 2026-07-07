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
package de.erethon.dungeonsxl.api.world;

import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.mob.DungeonMob;
import de.erethon.dungeonsxl.api.mob.MobSet;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.trigger.LogicalExpression;
import de.erethon.dungeonsxl.api.trigger.Trigger;
import de.erethon.dungeonsxl.api.trigger.TriggerListener;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * A playable dungeon instance. There may be any amount of GameWorlds per {@link Dungeon}.
 * <p>
 * A game world is not equal to a {@link de.erethon.dungeonsxl.api.dungeon.Dungeon}.
 *
 * @author Daniel Saukel
 */
// Implementation-specific methods: [gameblock, secure objects, classes signs, mobs, triggers] methods, getMobCount(), setPlaying(), startGame(), listener methods
public interface GameWorld extends InstanceWorld {

    /**
     * Returns the game that is played in the game world.
     *
     * @return the game that is played in the game world
     */
    Game getGame();

    /**
     * Creates a trigger represented by the given atomic expression.
     * <p>
     * For example, if the expression may wrap the string "D 10", but not "D 10, M ZOMBIE".
     *
     * @param owner      the {@link TriggerListener} that the trigger belongs to
     * @param expression the expression; must be {@link LogicalExpression#isAtomic() atomic}.
     * @throws IllegalArgumentException if the expression is not atomic
     * @throws IllegalStateException    if the owner is not in a game world
     * @return a trigger represented by the given atomic expression; not null.
     */
    Trigger createTrigger(TriggerListener owner, LogicalExpression expression);

    /**
     * Returns a Collection of the triggers registered in this world.
     *
     * @return a Collection of the triggers registered in this world
     */
    Collection<Trigger> getTriggers();

    /**
     * Returns a Collection of the triggers registered in this world that use the given key.
     *
     * @param key the key char
     * @see de.erethon.dungeonsxl.api.trigger.TriggerTypeKey
     * @return a Collection of the triggers registered in this world that use the given key.
     */
    Collection<Trigger> getTriggersFromKey(char key);

    /**
     * Unregisters the given trigger, which prevents them from firing unless explicitly done in code.
     *
     * @param trigger the trigger to unregister
     * @return if unregistering the trigger was successful
     */
    boolean unregisterTrigger(Trigger trigger);

    /**
     * Returns the living dungeon mobs.
     *
     * @return the living dungeon mobs
     */
    Collection<DungeonMob> getMobs();

    /**
     * Registers the given dungeon mob.
     *
     * @param mob the mob
     */
    void addMob(DungeonMob mob);

    /**
     * Unregisters the given dungeon mob.
     *
     * @param mob the mob
     */
    public void removeMob(DungeonMob mob);

    /**
     * Returns the generic {@link MobSet} that includes all mobs in the game world.
     *
     * @return the generic {@link MobSet} that includes all mobs in the game world
     */
    MobSet getAllMobSet();

    /**
     * Returns a copied Collection of the {@link MobSet}s in this game world.
     * <p>
     * It contains at least the {@link #getAllMobSet() ALL} mob set.
     *
     * @return a copied Collection of the {@link MobSet}s in this game world
     */
    Collection<MobSet> getMobSets();

    /**
     * Returns the {@link MobSet} of the given ID. If none exists, it creates one.
     *
     * @param id the identifier of the mob set
     * @return the {@link MobSet} of the given ID. If none exists, it creates one
     */
    MobSet getOrCreateMobSet(String id);

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
     * @param group each group might have its own start location
     * @return the start location of the world
     */
    Location getStartLocation(PlayerGroup group);

    /**
     * Returns if it is required to choose a class in order to start the game.
     *
     * @return if it is required to choose a class in order to start the game
     */
    boolean areClassesEnabled();

    /**
     * Sets if it is required to choose a class in order to start the game.
     *
     * @param enabled if it is required to choose a class in order to start the game
     */
    void setClassesEnabled(boolean enabled);

    /**
     * Returns a collection of the blocks that have been placed by players in the current game.
     *
     * @return a collection of the blocks that have been placed by players in the current game
     */
    Collection<Block> getPlacedBlocks();

}
