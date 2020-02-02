/*
 * Copyright (C) 2012-2020 Frank Baumann
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
package de.erethon.dungeonsxl.game;

import de.erethon.commons.player.PlayerUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.dungeon.DungeonConfig;
import de.erethon.dungeonsxl.global.GameSign;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.sign.DSign;
import de.erethon.dungeonsxl.sign.MobSign;
import de.erethon.dungeonsxl.trigger.ProgressTrigger;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import de.erethon.dungeonsxl.world.WorldConfig;
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
 * Game mostly stores for which purposes and how a {@link de.erethon.dungeonsxl.dungeon.Dungeon} is used, the player groups and the progress.
 *
 * @author Daniel Saukel
 */
public class Game {

    private DungeonsXL plugin;

    private boolean tutorial;
    private List<DGroup> dGroups = new ArrayList<>();
    private boolean started;
    private GameType type = GameTypeDefault.DEFAULT;
    private DGameWorld world;
    private GameRuleProvider rules;
    private int waveCount;
    private Map<String, Integer> gameKills = new HashMap<>();
    private Map<String, Integer> waveKills = new HashMap<>();

    public Game(DungeonsXL plugin, DGroup dGroup) {
        this.plugin = plugin;
        plugin.getGameCache().add(this);

        tutorial = false;
        started = false;

        dGroups.add(dGroup);
        dGroup.setGameWorld(world);
        fetchRules();
        dGroup.setInitialLives(rules.getInitialGroupLives());
        dGroup.setLives(rules.getInitialGroupLives());
        dGroup.setScore(rules.getInitialScore());
    }

    public Game(DungeonsXL plugin, DGroup dGroup, DGameWorld world) {
        this.plugin = plugin;
        plugin.getGameCache().add(this);

        tutorial = false;
        started = false;
        this.world = world;

        dGroups.add(dGroup);
        dGroup.setGameWorld(world);
        fetchRules();
        dGroup.setInitialLives(rules.getInitialGroupLives());
        dGroup.setLives(rules.getInitialGroupLives());
        dGroup.setScore(rules.getInitialScore());
    }

    public Game(DungeonsXL plugin, DGroup dGroup, GameType type, DGameWorld world) {
        this(plugin, new ArrayList<>(Arrays.asList(dGroup)), type, world);
    }

    public Game(DungeonsXL plugin, List<DGroup> dGroups, GameType type, DGameWorld world) {
        this.plugin = plugin;
        plugin.getGameCache().add(this);

        this.dGroups = dGroups;
        this.type = type;
        this.world = world;
        this.tutorial = false;
        this.started = true;

        for (DGroup dGroup : dGroups) {
            dGroup.setGameWorld(world);
            fetchRules();
            dGroup.setInitialLives(rules.getInitialGroupLives());
            dGroup.setLives(rules.getInitialGroupLives());
            dGroup.setScore(rules.getInitialScore());
        }
    }

    /**
     * @return the tutorial
     */
    public boolean isTutorial() {
        return tutorial;
    }

    /**
     * @param tutorial if the DGameWorld is the tutorial
     */
    public void setTutorial(boolean tutorial) {
        this.tutorial = tutorial;
    }

    /**
     * @return the dGroups
     */
    public List<DGroup> getDGroups() {
        return dGroups;
    }

    /**
     * @param dGroup the dGroups to add
     */
    public void addDGroup(DGroup dGroup) {
        dGroups.add(dGroup);

        dGroup.setGameWorld(world);
        dGroup.setInitialLives(rules.getInitialGroupLives());
        dGroup.setLives(rules.getInitialGroupLives());
    }

    /**
     * @param dGroup the dGroups to remove
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
     * @param started set if the Game has started yet
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
     * @param type the type to set
     */
    public void setType(GameType type) {
        this.type = type;
    }

    /**
     * @return the DGameWorld connected to the Game
     */
    public DGameWorld getWorld() {
        return world;
    }

    /**
     * @param world the DGameWorld to connect to the Game
     */
    public void setWorld(DGameWorld world) {
        this.world = world;
    }

    /**
     * @return the GameRules
     */
    public GameRuleProvider getRules() {
        return rules;
    }

    /**
     * @param rules the GameRules to set
     */
    public void setRules(GameRuleProvider rules) {
        this.rules = rules;
    }

    /**
     * Fetchs the rules with the following priority: 1. Game type 2. Dungeon config: Override values 3. Floor config 4. Dungeon config: Default values 5. Main
     * config: Default values 6. The default values
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

        GameRuleProvider finalRules = new GameRuleProvider();

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

        finalRules.apply(GameRuleProvider.DEFAULT_VALUES);

        rules = finalRules;
    }

    /**
     * Refers to the DGroup with the best progress.
     *
     * @return the unplayed floors
     */
    public List<DResourceWorld> getUnplayedFloors() {
        List<DResourceWorld> unplayedFloors = null;
        for (DGroup dGroup : dGroups) {
            if (unplayedFloors == null || dGroup.getUnplayedFloors().size() < unplayedFloors.size()) {
                unplayedFloors = dGroup.getUnplayedFloors();
            }
        }
        if (unplayedFloors == null) {
            unplayedFloors = new ArrayList<>();
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
            toReturn.addAll(dGroup.getPlayers().getOnlinePlayers());
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

        Set<ProgressTrigger> triggers = ProgressTrigger.getByGameWorld(world);
        for (ProgressTrigger trigger : triggers) {
            if (getWaveCount() >= trigger.getWaveCount() & getFloorCount() >= trigger.getFloorCount() - 1 || !getUnplayedFloors().contains(trigger.getFloor()) & trigger.getFloor() != null) {
                trigger.onTrigger();
            }
        }

        int delay = rules.getTimeToNextWave();
        sendMessage(DMessage.GROUP_WAVE_FINISHED.getMessage(String.valueOf(waveCount), String.valueOf(delay)));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (teleport) {
                    for (Player player : getPlayers()) {
                        PlayerUtil.secureTeleport(player, world.getStartLocation(DGroup.getByPlayer(player)));
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

    public boolean isFinished() {
        return dGroups.stream().allMatch(DGroup::isFinished);
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
        for (Game game : DungeonsXL.getInstance().getGameCache()) {
            if (game.getDGroups().contains(dGroup)) {
                return game;
            }
        }

        return null;
    }

    public static Game getByPlayer(Player player) {
        return getByDGroup(DGroup.getByPlayer(player));
    }

    public static Game getByGameWorld(DGameWorld gameWorld) {
        for (Game game : DungeonsXL.getInstance().getGameCache()) {
            if (gameWorld.equals(game.getWorld())) {
                return game;
            }
        }

        return null;
    }

    public static Game getByWorld(World world) {
        DGameWorld gameWorld = DGameWorld.getByWorld(world);
        if (gameWorld != null) {
            return getByGameWorld(gameWorld);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{dungeon=" + getDungeon() + "}";
    }

}
