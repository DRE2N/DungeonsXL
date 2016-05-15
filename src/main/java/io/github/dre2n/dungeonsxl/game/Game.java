/*
 * Copyright (C) 2012-2016 Frank Baumann
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
package io.github.dre2n.dungeonsxl.game;

import io.github.dre2n.commons.util.playerutil.PlayerUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.config.DungeonConfig;
import io.github.dre2n.dungeonsxl.config.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.global.GameSign;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.sign.MobSign;
import io.github.dre2n.dungeonsxl.trigger.ProgressTrigger;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class Game {

    static DungeonsXL plugin = DungeonsXL.getInstance();

    private List<DGroup> dGroups = new ArrayList<>();
    private boolean started;
    private GameType type;
    private GameWorld world;
    private GameRules rules;
    private int waveCount;
    private Map<String, Integer> gameKills = new HashMap<>();
    private Map<String, Integer> waveKills = new HashMap<>();

    public Game(DGroup dGroup) {
        dGroups.add(dGroup);
        started = false;
        fetchRules();

        plugin.getGames().add(this);
    }

    public Game(DGroup dGroup, GameWorld world) {
        dGroups.add(dGroup);
        started = false;
        this.world = world;
        fetchRules();

        plugin.getGames().add(this);
    }

    public Game(DGroup dGroup, String worldName) {
        plugin.getGames().add(this);

        dGroups.add(dGroup);
        started = false;
        world = new GameWorld();
        dGroup.setGameWorld(world);
        fetchRules();
        world.load(worldName);
    }

    public Game(DGroup dGroup, GameType type, GameWorld world) {
        this(new ArrayList<>(Arrays.asList(dGroup)), type, world);
    }

    public Game(List<DGroup> dGroups, GameType type, GameWorld world) {
        this.dGroups = dGroups;
        this.type = type;
        this.world = world;
        this.started = true;
        fetchRules();

        plugin.getGames().add(this);
    }

    /**
     * @return the dGroups
     */
    public List<DGroup> getDGroups() {
        return dGroups;
    }

    /**
     * @param dGroup
     * the dGroups to add
     */
    public void addDGroup(DGroup dGroup) {
        dGroups.add(dGroup);
    }

    /**
     * @param dGroup
     * the dGroups to remove
     */
    public void removeDGroup(DGroup dGroup) {
        dGroups.remove(dGroup);

        if (dGroups.isEmpty()) {
            delete();
        }
    }

    /**
     * @return if the Game has started yet
     */
    public boolean hasStarted() {
        return started;
    }

    /**
     * @param started
     * set if the Game has started yet
     */
    public void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * @return the type
     */
    public GameType getType() {
        return type;
    }

    /**
     * @param type
     * the type to set
     */
    public void setType(GameType type) {
        this.type = type;
    }

    /**
     * @return the GameWorld connected to the Game
     */
    public GameWorld getWorld() {
        return world;
    }

    /**
     * @param world
     * the GameWorld to connect to the Game
     */
    public void setWorld(GameWorld world) {
        this.world = world;
    }

    /**
     * @return the GameRules
     */
    public GameRules getRules() {
        return rules;
    }

    /**
     * @param rules
     * the GameRules to set
     */
    public void setRules(GameRules rules) {
        this.rules = rules;
    }

    /**
     * Fetchs the rules with the following priority:
     * 1. Game type
     * 2. Dungeon config: Override values
     * 3. Floor config
     * 4. Dungeon config: Default values
     * 5. Main config: Default values
     * 6. The default values
     */
    public void fetchRules() {
        DungeonConfig dungeonConfig = null;
        if (getDungeon() != null) {
            dungeonConfig = getDungeon().getConfig();
        }

        WorldConfig floorConfig = null;
        if (world != null) {
            floorConfig = world.getConfig();
        }

        GameRules finalRules = new GameRules();

        if (type != null) {
            finalRules.apply(type);
        }

        if (dungeonConfig != null && dungeonConfig.getOverrideValues() != null) {
            finalRules.apply(dungeonConfig.getOverrideValues());
        }

        if (floorConfig != null) {
            finalRules.apply(floorConfig);
        }

        if (dungeonConfig != null && dungeonConfig.getDefaultValues() != null) {
            finalRules.apply(dungeonConfig.getDefaultValues());
        }

        finalRules.apply(plugin.getMainConfig().getDefaultWorldConfig());

        finalRules.apply(GameRules.DEFAULT_VALUES);

        rules = finalRules;
    }

    /**
     * Refers to the DGroup with the best progress.
     *
     * @return the unplayed floors
     */
    public List<String> getUnplayedFloors() {
        List<String> unplayedFloors = new ArrayList<>();
        for (DGroup dGroup : dGroups) {
            if (dGroup.getUnplayedFloors().size() < unplayedFloors.size()) {
                unplayedFloors = dGroup.getUnplayedFloors();
            }
        }
        return unplayedFloors;
    }

    /**
     * Refers to the DGroup with the best progress.
     *
     * @return the floorCount
     */
    public int getFloorCount() {
        int floorCount = 0;
        for (DGroup dGroup : dGroups) {
            if (dGroup.getFloorCount() > floorCount) {
                floorCount = dGroup.getFloorCount();
            }
        }
        return floorCount;
    }

    /**
     * @return the waveCount
     */
    public int getWaveCount() {
        return waveCount;
    }

    /**
     * @param waveCount
     * the waveCount to set
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
     * @param killer
     * the killer; null if the killer is not a player
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

    /**
     * Refers to a DGroup.
     *
     * @return the dungeon
     */
    public Dungeon getDungeon() {
        return dGroups.get(0).getDungeon();
    }

    /**
     * @return the players in all dGroups
     */
    public Set<Player> getPlayers() {
        Set<Player> toReturn = new HashSet<>();
        for (DGroup dGroup : dGroups) {
            toReturn.addAll(dGroup.getPlayers());
        }
        return toReturn;
    }

    /**
     * @return if the DGroup list is empty
     */
    public boolean isEmpty() {
        return dGroups.isEmpty();
    }

    /* Actions */
    /**
     * Remove the Game from the List
     */
    public void delete() {
        GameSign gameSign = GameSign.getByGame(this);

        plugin.getGames().remove(this);

        if (gameSign != null) {
            gameSign.update();
        }
    }

    /**
     * @param mobCountIncreaseRate
     * the new mob count will be increased by this rate
     * @param teleport
     * whether or not to teleport the players to the start location
     */
    public void finishWave(final double mobCountIncreaseRate, final boolean teleport) {
        waveCount++;
        resetWaveKills();

        Set<ProgressTrigger> triggers = ProgressTrigger.getByGameWorld(world);
        for (ProgressTrigger trigger : triggers) {
            if (getWaveCount() >= trigger.getWaveCount() & getFloorCount() >= trigger.getFloorCount() - 1 || !getUnplayedFloors().contains(trigger.getFloor()) & trigger.getFloor() != null) {
                trigger.onTrigger();
            }
        }

        int delay = rules.getTimeToNextWave();
        sendMessage(plugin.getMessageConfig().getMessage(DMessages.GROUP_WAVE_FINISHED, String.valueOf(waveCount), String.valueOf(delay)));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (teleport) {
                    for (Player player : getPlayers()) {
                        PlayerUtil.secureTeleport(player, world.getStartLocation());
                    }
                }

                for (DSign dSign : world.getDSigns()) {
                    if (!(dSign instanceof MobSign)) {
                        continue;
                    }

                    MobSign mobSign = (MobSign) dSign;
                    int newAmount = (int) Math.ceil(mobSign.getInitialAmount() * mobCountIncreaseRate);

                    mobSign.setAmount(newAmount);
                    mobSign.setInitialAmount(newAmount);
                    mobSign.initializeTask();
                }
            }
        }.runTaskLater(plugin, delay * 20);
    }

    /**
     * @param message the message to send
     */
    public void sendMessage(String message) {
        for (DGroup dGroup : dGroups) {
            dGroup.sendMessage(message);
        }
    }

    /* Statics */
    public static Game getByDGroup(DGroup dGroup) {
        for (Game game : plugin.getGames()) {
            if (game.getDGroups().contains(dGroup)) {
                return game;
            }
        }

        return null;
    }

    public static Game getByPlayer(Player player) {
        return getByDGroup(DGroup.getByPlayer(player));
    }

    public static Game getByGameWorld(GameWorld gameWorld) {
        for (Game game : plugin.getGames()) {
            if (game.getWorld().equals(gameWorld)) {
                return game;
            }
        }

        return null;
    }

    public static Game getByWorld(World world) {
        GameWorld gameWorld = GameWorld.getByWorld(world);
        if (gameWorld != null) {
            return getByGameWorld(gameWorld);
        } else {
            return null;
        }
    }

}
