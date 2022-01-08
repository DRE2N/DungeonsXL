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
package de.erethon.dungeonsxl.api;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.mob.ExMob;
import de.erethon.commons.misc.Registry;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.mob.DungeonMob;
import de.erethon.dungeonsxl.api.mob.ExternalMobProvider;
import de.erethon.dungeonsxl.api.player.GroupAdapter;
import de.erethon.dungeonsxl.api.player.PlayerCache;
import de.erethon.dungeonsxl.api.player.PlayerClass;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.world.EditWorld;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import java.io.File;
import java.util.Collection;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * The API main interface.
 *
 * @author Daniel Saukel
 */
public interface DungeonsAPI extends Plugin {

    static final File PLUGIN_ROOT = new File("plugins/DungeonsXL");
    static final File BACKUPS = new File(PLUGIN_ROOT, "backups");
    static final File LANGUAGES = new File(PLUGIN_ROOT, "languages");
    static final File MAPS = new File(PLUGIN_ROOT, "maps");
    static final File PLAYERS = new File(PLUGIN_ROOT, "players");
    static final File SCRIPTS = new File(PLUGIN_ROOT, "scripts");
    static final File CLASSES = new File(SCRIPTS, "classes");
    static final File DUNGEONS = new File(SCRIPTS, "dungeons");

    /**
     * Returns the loaded instance of the Caliburn API.
     *
     * @return the loaded instance of the Caliburn API
     */
    CaliburnAPI getCaliburn();

    /**
     * Returns a cache of player wrapper objects.
     *
     * @return a cache of player wrapper objects
     */
    PlayerCache getPlayerCache();

    /**
     * Returns a cache of Game objects.
     *
     * @return a cache of Game objects
     */
    Collection<Game> getGameCache();

    /**
     * Returns a registry of the loaded classes.
     *
     * @return a registry of the loaded classes
     */
    Registry<String, PlayerClass> getClassRegistry();

    /**
     * Returns a registry of the sign types.
     *
     * @return a registry of the sign types
     */
    Registry<String, Class<? extends DungeonSign>> getSignRegistry();

    /**
     * Returns a registry of the requirement types.
     *
     * @return a registry of the requirement types
     */
    Registry<String, Class<? extends Requirement>> getRequirementRegistry();

    /**
     * Returns a registry of the reward types.
     *
     * @return a registry of the reward types
     */
    Registry<String, Class<? extends Reward>> getRewardRegistry();

    /**
     * Returns a registry of the dungeons.
     *
     * @return a registry of the dungeons
     */
    Registry<String, Dungeon> getDungeonRegistry();

    /**
     * Returns a registry of the resources worlds.
     *
     * @return a registry of the resources worlds
     */
    Registry<String, ResourceWorld> getMapRegistry();

    /**
     * Returns a cache of the instance worlds.
     *
     * @return a cache of the instance worlds
     */
    Registry<Integer, InstanceWorld> getInstanceCache();

    /**
     * Returns a registry of the game rules.
     *
     * @return a registry of the game rules
     */
    Registry<String, GameRule> getGameRuleRegistry();

    /**
     * Returns a registry of the external mob providers.
     *
     * @return a registry of the external mob providers
     */
    Registry<String, ExternalMobProvider> getExternalMobProviderRegistry();

    /**
     * Returns a cache of the player groups.
     *
     * @return a cache of the player groups
     */
    Registry<String, PlayerGroup> getGroupCache();

    /**
     * Registers a DungeonModule.
     *
     * @param module the module to register
     */
    void registerModule(DungeonModule module);

    /**
     * Makes DungeonsXL track external group and synchronize them with its own groups.
     *
     * @param groupAdapter the group adapter to register
     */
    void registerGroupAdapter(GroupAdapter groupAdapter);

    /* Object initialization */
    /**
     * Creates a new group.
     *
     * @param leader the leader
     * @return a new group
     */
    PlayerGroup createGroup(Player leader);

    /**
     * Creates a new group.
     *
     * @param leader the leader
     * @param color  the color that represents the group and sets the name
     * @return a new group or null if values are invalid
     */
    PlayerGroup createGroup(Player leader, PlayerGroup.Color color);

    /**
     * Creates a new group.
     *
     * @param leader the leader
     * @param name   the group's name - must be unique
     * @return a new group or null if values are invalid
     */
    PlayerGroup createGroup(Player leader, String name);

    /**
     * Creates a new group.
     *
     * @param leader  the leader
     * @param dungeon the dungeon to play
     * @return a new group or null if values are invalid
     */
    PlayerGroup createGroup(Player leader, Dungeon dungeon);

    /**
     * Creates a new group.
     *
     * @param leader  the leader
     * @param members the group members with or without the leader
     * @param name    the name of the group
     * @param dungeon the dungeon to play
     * @return a new group or null if values are invalid
     */
    PlayerGroup createGroup(Player leader, Collection<Player> members, String name, Dungeon dungeon);

    /**
     * Wraps the given {@link LivingEntity} object in a {@link DungeonMob} object.
     *
     * @param entity    the entity
     * @param gameWorld the game world where the entity is
     * @param triggerId the identifier used in mob triggers
     * @return the wrapped DungeonMob
     */
    DungeonMob wrapEntity(LivingEntity entity, GameWorld gameWorld, String triggerId);

    /**
     * Wraps the given {@link LivingEntity} object in a {@link DungeonMob} object.
     *
     * @param entity    the entity
     * @param gameWorld the game world where the entity is
     * @param type      the ExMob type of the entity
     * @return the wrapped DungeonMob
     */
    DungeonMob wrapEntity(LivingEntity entity, GameWorld gameWorld, ExMob type);

    /**
     * Wraps the given {@link LivingEntity} object in a {@link DungeonMob} object.
     *
     * @param entity    the entity
     * @param gameWorld the game world where the entity is
     * @param type      the ExMob type of the entity
     * @param triggerId the identifier used in mob triggers
     * @return the wrapped DungeonMob
     */
    DungeonMob wrapEntity(LivingEntity entity, GameWorld gameWorld, ExMob type, String triggerId);

    /* Getters */
    /**
     * Returns an existing {@link DungeonMob} object that wraps the given {@link LivingEntity} object or null if none exists.
     *
     * @param entity the entity
     * @return an existing {@link DungeonMob} object that wraps the given {@link LivingEntity} object or null if none exists
     */
    DungeonMob getDungeonMob(LivingEntity entity);

    /**
     * Returns the group the player is a member of or null if he is in none.
     *
     * @param member the player
     * @return the group the player is a member of or null if he is in none
     */
    PlayerGroup getPlayerGroup(Player member);

    /**
     * Returns the game the given player plays.
     *
     * @param player the player
     * @return the game the given player plays
     */
    Game getGame(Player player);

    /**
     * Returns the game played in the given instance world.
     *
     * @param world the instance world
     * @return the game played in the given instance world
     */
    Game getGame(World world);

    /**
     * Returns the GameWorld that wraps the given instance world.
     *
     * @param world the instance world
     * @return the GameWorld that wraps the given instance world
     */
    GameWorld getGameWorld(World world);

    /**
     * Returns the EditWorld that wraps the given instance world.
     *
     * @param world the instance world
     * @return the EditWorld that wraps the given instance worl
     */
    EditWorld getEditWorld(World world);

    /**
     * Returns if the given world is an instance.
     *
     * @param world the world
     * @return if the given world is an instance
     */
    boolean isInstance(World world);

    /**
     * Returns if the given item stack is a dungeon item.
     * <p>
     * Dungeon items are items that are removed from the inventory when the dungeon is finished.
     *
     * @param itemStack the item stack
     * @return if the given item stack is a dungeon item
     */
    boolean isDungeonItem(ItemStack itemStack);

    /**
     * Sets the given item stack to be a dungeon item and returns a copy with the updated state.
     * <p>
     * Dungeon items are items that are removed from the inventory when the dungeon is finished.
     *
     * @param itemStack   the item stack
     * @param dungeonItem if the item stack
     * @return a copy of the item stack that is a dungeon item
     */
    ItemStack setDungeonItem(ItemStack itemStack, boolean dungeonItem);

}
