/*
 * Copyright (C) 2012-2022 Frank Baumann
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
package de.erethon.dungeonsxl.dungeon;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.dungeon.GameGoal;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.global.GameSign;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.sign.windup.MobSign;
import de.erethon.dungeonsxl.trigger.ProgressTrigger;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class DGame implements Game {

    private DungeonsXL plugin;

    private Dungeon dungeon;
    private GameWorld world;
    private List<ResourceWorld> unplayedFloors = new ArrayList<>();
    private ResourceWorld nextFloor;
    private int floorCount;
    private List<PlayerGroup> groups = new ArrayList<>();
    private boolean tutorial;
    private boolean rewards = true;
    private boolean started;
    private int waveCount;
    private Map<String, Integer> gameKills = new HashMap<>();
    private Map<String, Integer> waveKills = new HashMap<>();

    public DGame(DungeonsXL plugin, Dungeon dungeon) {
        this.plugin = plugin;
        plugin.getGameCache().add(this);

        setDungeon(dungeon);

        if (this.dungeon == null) {
            throw new IllegalStateException("Game initialized without dungeon");
        }
        tutorial = false;
        started = false;
    }

    public DGame(DungeonsXL plugin, Dungeon dungeon, PlayerGroup group) {
        this(plugin, dungeon);
        addGroup(group);
    }

    public DGame(DungeonsXL plugin, Dungeon dungeon, List<PlayerGroup> groups) {
        this(plugin, dungeon);
        groups.forEach(this::addGroup);
    }

    @Override
    public boolean isTutorial() {
        return tutorial;
    }

    @Override
    public void setTutorial(boolean tutorial) {
        this.tutorial = tutorial;
    }

    @Override
    public List<PlayerGroup> getGroups() {
        return new ArrayList<>(groups);
    }

    @Override
    public void addGroup(PlayerGroup group) {
        groups.add(group);

        ((DGroup) group).setGame(this);
        group.setInitialLives(getRules().getState(GameRule.INITIAL_GROUP_LIVES));
        group.setLives(getRules().getState(GameRule.INITIAL_GROUP_LIVES));
        GameGoal goal = getRules().getState(GameRule.GAME_GOAL);
        if (goal.getType().hasComponent(GameGoal.INITIAL_SCORE)) {
            group.setScore(goal.getState(GameGoal.INITIAL_SCORE));
        }
    }

    @Override
    public void removeGroup(PlayerGroup group) {
        groups.remove(group);

        if (groups.isEmpty()) {
            delete();
        }
    }

    @Override
    public boolean hasStarted() {
        return started;
    }

    @Override
    public void setStarted(boolean started) {
        this.started = started;
    }

    @Override
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * Sets up all dungeon-related fields.
     *
     * @param dungeon the dungeon to set
     */
    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
        if (dungeon.isMultiFloor()) {
            unplayedFloors = dungeon.getFloors();
        }
    }

    /**
     * Sets up all dungeon-related fields.
     *
     * @param name the name of the dungeon
     * @return if the action was successful
     */
    public boolean setDungeon(String name) {
        dungeon = plugin.getDungeonRegistry().get(name);
        if (dungeon != null) {
            unplayedFloors = dungeon.getFloors();
            return true;

        } else {
            ResourceWorld resource = plugin.getMapRegistry().get(name);
            if (resource != null) {
                dungeon = resource.getSingleFloorDungeon();
                return true;
            }
            return false;
        }
    }

    @Override
    public DGameWorld getWorld() {
        return (DGameWorld) world;
    }

    @Override
    public void setWorld(GameWorld gameWorld) {
        world = gameWorld;
    }

    @Override
    public List<ResourceWorld> getUnplayedFloors() {
        return unplayedFloors;
    }

    @Override
    public boolean addUnplayedFloor(ResourceWorld unplayedFloor) {
        return unplayedFloors.add(unplayedFloor);
    }

    @Override
    public boolean removeUnplayedFloor(ResourceWorld unplayedFloor, boolean force) {
        if (getDungeon().getRemoveWhenPlayed() || force) {
            return unplayedFloors.remove(unplayedFloor);
        }
        return false;
    }

    @Override
    public ResourceWorld getNextFloor() {
        return nextFloor;
    }

    @Override
    public void setNextFloor(ResourceWorld floor) {
        nextFloor = floor;
    }

    @Override
    public int getFloorCount() {
        return floorCount;
    }

    @Override
    public boolean hasRewards() {
        return rewards;
    }

    @Override
    public void setRewards(boolean enabled) {
        rewards = enabled;
    }

    /**
     * @return the waveCount
     */
    public int getWaveCount() {
        return waveCount;
    }

    /**
     * @param waveCount the waveCount to set
     */
    public void setWaveCount(int waveCount) {
        this.waveCount = waveCount;
    }

    /**
     * @return how many mobs have been killed in the game
     */
    public int getGameKills() {
        int count = 0;
        for (String killer : gameKills.keySet()) {
            count += gameKills.get(killer);
        }
        return count;
    }

    /**
     * @return how many mobs have been killed in the last game
     */
    public int getWaveKills() {
        int count = 0;
        for (String killer : waveKills.keySet()) {
            count += waveKills.get(killer);
        }
        return count;
    }

    /**
     * @param killer the killer; null if the killer is not a player
     */
    public void addKill(String killer) {
        if (killer == null) {
            killer = "N/A";
        }
        waveKills.put(killer, waveKills.get(killer) == null ? 1 : waveKills.get(killer) + 1);
    }

    /**
     * Adds the values of the wave kills map to the game kills map and resets the wave kills.
     */
    public void resetWaveKills() {
        gameKills.putAll(waveKills);
        waveKills.clear();
    }

    @Override
    public Collection<Player> getPlayers() {
        Set<Player> toReturn = new HashSet<>();
        for (PlayerGroup group : groups) {
            toReturn.addAll(group.getMembers().getOnlinePlayers());
        }
        return toReturn;
    }

    @Override
    public boolean isEmpty() {
        return groups.isEmpty();
    }

    @Override
    public GameWorld ensureWorldIsLoaded(boolean ignoreLimit) {
        if (world != null) {
            return world;
        }
        world = dungeon.getMap().instantiateGameWorld(this, ignoreLimit);
        return world;
    }

    @Override
    public boolean start() {
        getWorld().setWeather(getRules());

        for (PlayerGroup group : groups) {
            if (group == null) {
                continue;
            }
            if (!((DGroup) group).checkStartGame(this)) {
                plugin.log("Could not start game for group " + group);
                return false;
            }
        }
        int i = 0;
        for (PlayerGroup group : groups) {
            if (group != null) {
                ((DGroup) group).startGame(this, i++);
            }
        }

        if (getWorld() != null) {
            if (!getWorld().isPlaying()) {
                getWorld().startGame();
            }
        }

        floorCount++;
        nextFloor = null;
        started = true;
        return true;
    }

    @Override
    public void delete() {
        GameSign gameSign = GameSign.getByGame(plugin, this);

        plugin.getGameCache().remove(this);

        if (gameSign != null) {
            gameSign.update();
        }
    }

    /**
     * @param mobCountIncreaseRate the new mob count will be increased by this rate
     * @param teleport             whether or not to teleport the players to the start location
     */
    public void finishWave(final double mobCountIncreaseRate, final boolean teleport) {
        waveCount++;
        resetWaveKills();

        Set<ProgressTrigger> triggers = ProgressTrigger.getByGameWorld(getWorld());
        for (ProgressTrigger trigger : triggers) {
            if (getWaveCount() >= trigger.getWaveCount() & getFloorCount() >= trigger.getFloorCount() - 1 || !getUnplayedFloors().contains(trigger.getFloor()) & trigger.getFloor() != null) {
                trigger.onTrigger();
            }
        }

        int delay = getRules().getState(GameRule.TIME_TO_NEXT_WAVE);
        sendMessage(DMessage.GROUP_WAVE_FINISHED.getMessage(String.valueOf(waveCount), String.valueOf(delay)));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (teleport) {
                    groups.forEach(g -> g.getMembers().getOnlinePlayers().forEach(p -> p.teleport(world.getStartLocation(plugin.getPlayerGroup(p)))));
                }

                for (DungeonSign dSign : world.getDungeonSigns()) {
                    if (!(dSign instanceof MobSign)) {
                        continue;
                    }

                    MobSign mobSign = (MobSign) dSign;
                    int newAmount = (int) Math.ceil(mobSign.getInitialAmount() * mobCountIncreaseRate);

                    mobSign.setN(newAmount);
                    mobSign.startTask();
                }
            }
        }.runTaskLater(plugin, delay * 20);
    }

    @Override
    public boolean isFinished() {
        return groups.stream().allMatch(PlayerGroup::isFinished);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{dungeon=" + getDungeon() + "}";
    }

}
