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
package io.github.dre2n.dungeonsxl.player;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupDisbandEvent;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupStartFloorEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerJoinDGroupEvent;
import io.github.dre2n.dungeonsxl.event.requirement.RequirementDemandEvent;
import io.github.dre2n.dungeonsxl.event.reward.RewardAdditionEvent;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameRules;
import io.github.dre2n.dungeonsxl.game.GameType;
import io.github.dre2n.dungeonsxl.game.GameTypeDefault;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.requirement.Requirement;
import io.github.dre2n.dungeonsxl.reward.Reward;
import io.github.dre2n.dungeonsxl.task.TimeIsRunningTask;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DGroup {

    static DungeonsXL plugin = DungeonsXL.getInstance();

    private String name;
    private Player captain;
    private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();
    private List<UUID> invitedPlayers = new ArrayList<>();
    private Dungeon dungeon;
    private String dungeonName;
    private String mapName;
    private List<String> unplayedFloors = new ArrayList<>();
    private GameWorld gameWorld;
    private boolean playing;
    private int floorCount;
    private List<Reward> rewards = new ArrayList<>();
    private BukkitTask timeIsRunningTask;

    public DGroup(Player player) {
        this("Group_" + plugin.getDGroups().size(), player);
    }

    public DGroup(String name, Player player) {
        plugin.getDGroups().add(this);
        this.name = name;

        captain = player;
        players.add(player);

        playing = false;
        floorCount = 0;
    }

    public DGroup(Player player, String identifier, boolean multiFloor) {
        this("Group_" + plugin.getDGroups().size(), player, identifier, multiFloor);
    }

    public DGroup(String name, Player player, String identifier, boolean multiFloor) {
        this(name, player, new ArrayList<Player>(), identifier, multiFloor);
    }

    public DGroup(String name, Player captain, List<Player> players, String identifier, boolean multiFloor) {
        plugin.getDGroups().add(this);
        this.name = name;

        DPlayerJoinDGroupEvent event = new DPlayerJoinDGroupEvent(plugin.getDPlayers().getByPlayer(captain), true, this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.captain = captain;
            this.players.add(captain);
        }

        for (Player player : players) {
            addPlayer(player);
        }

        dungeon = plugin.getDungeons().getDungeon(identifier);
        if (multiFloor && dungeon != null) {
            dungeonName = dungeon.getName();
            mapName = dungeon.getConfig().getStartFloor();
            unplayedFloors = dungeon.getConfig().getFloors();

        } else {
            mapName = identifier;
            dungeon = new Dungeon(identifier);
        }

        playing = false;
        floorCount = 0;
    }

    // Getters and setters
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the captain
     */
    public Player getCaptain() {
        return captain;
    }

    /**
     * @param captain
     * the captain to set
     */
    public void setCaptain(Player captain) {
        this.captain = captain;
    }

    /**
     * @return the players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * @param player
     * the player to add
     */
    public void addPlayer(Player player) {
        DPlayerJoinDGroupEvent event = new DPlayerJoinDGroupEvent(DGamePlayer.getByPlayer(player), false, this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            sendMessage(plugin.getMessageConfig().getMessage(DMessages.GROUP_PLAYER_JOINED, player.getName()));
            MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.PLAYER_JOIN_GROUP));

            players.add(player);
        }
    }

    /**
     * @param player
     * the player to remove
     */
    public void removePlayer(Player player) {
        players.remove(player);
        GroupSign.updatePerGroup(this);

        // Send message
        sendMessage(plugin.getMessageConfig().getMessage(DMessages.PLAYER_LEFT_GROUP, player.getName()));

        // Check group
        if (isEmpty()) {
            DGroupDisbandEvent event = new DGroupDisbandEvent(this, player, DGroupDisbandEvent.Cause.GROUP_IS_EMPTY);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                delete();
            }
        }
    }

    /**
     * @return the players
     */
    public List<Player> getInvitedPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for (UUID uuid : invitedPlayers) {
            players.add(plugin.getServer().getPlayer(uuid));
        }

        return players;
    }

    /**
     * @param player
     * the player to add
     */
    public void addInvitedPlayer(Player player, boolean silent) {
        if (player == null) {
            return;
        }

        if (DGroup.getByPlayer(player) != null) {
            if (!silent) {
                MessageUtil.sendMessage(captain, plugin.getMessageConfig().getMessage(DMessages.ERROR_IN_GROUP, player.getName()));
            }
            return;
        }

        if (!silent) {
            MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.PLAYER_INVITED, captain.getName(), name));
        }

        // Send message
        if (!silent) {
            sendMessage(plugin.getMessageConfig().getMessage(DMessages.GROUP_INVITED_PLAYER, captain.getName(), player.getName(), name));
        }

        // Add player
        invitedPlayers.add(player.getUniqueId());
    }

    /**
     * @param player
     * the player to remove
     */
    public void removeInvitedPlayer(Player player, boolean silent) {
        if (player == null) {
            return;
        }

        if (DGroup.getByPlayer(player) != this) {
            if (!silent) {
                MessageUtil.sendMessage(captain, plugin.getMessageConfig().getMessage(DMessages.ERROR_NOT_IN_GROUP, player.getName(), name));
            }
            return;
        }

        if (!silent) {
            MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.PLAYER_UNINVITED, player.getName(), name));
        }

        // Send message
        if (!silent) {
            for (Player groupPlayer : getPlayers()) {
                MessageUtil.sendMessage(groupPlayer, plugin.getMessageConfig().getMessage(DMessages.GROUP_UNINVITED_PLAYER, captain.getName(), player.getName(), name));
            }
        }

        invitedPlayers.remove(player.getUniqueId());
    }

    /**
     * Remove all invitations for players who are not online
     */
    public void clearOfflineInvitedPlayers() {
        ArrayList<UUID> toRemove = new ArrayList<>();

        for (UUID uuid : invitedPlayers) {
            if (plugin.getServer().getPlayer(uuid) == null) {
                toRemove.add(uuid);
            }
        }

        invitedPlayers.removeAll(toRemove);
    }

    /**
     * @return the gameWorld
     */
    public GameWorld getGameWorld() {
        return gameWorld;
    }

    /**
     * @param gameWorld
     * the gameWorld to set
     */
    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    /**
     * @return the dungeon
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * @param dungeon
     * the dungeon to set
     */
    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    /**
     * @return the dungeonName
     */
    public String getDungeonName() {
        return dungeonName;
    }

    /**
     * @param dungeonName
     * the dungeonName to set
     */
    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    /**
     * @return if the group is playing
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @param name
     * the name to set
     */
    public void setMapName(String name) {
        mapName = name;
    }

    /**
     * @return the unplayedFloors
     */
    public List<String> getUnplayedFloors() {
        return unplayedFloors;
    }

    /**
     * @param unplayedFloor
     * the unplayedFloor to add
     */
    public void addUnplayedFloor(String unplayedFloor) {
        unplayedFloors.add(unplayedFloor);
    }

    /**
     * @param unplayedFloor
     * the unplayedFloor to add
     */
    public void removeUnplayedFloor(String unplayedFloor) {
        if (getDungeon().getConfig().getRemoveWhenPlayed()) {
            unplayedFloors.remove(unplayedFloor);
        }
    }

    /**
     * @return if the group is playing
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * @param playing
     * set if the group is playing
     */
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    /**
     * @return the floorCount
     */
    public int getFloorCount() {
        return floorCount;
    }

    /**
     * @param floorCount
     * the floorCount to set
     */
    public void setFloorCount(int floorCount) {
        this.floorCount = floorCount;
    }

    /**
     * @return the rewards
     */
    public List<Reward> getRewards() {
        return rewards;
    }

    /**
     * @param reward
     * the rewards to add
     */
    public void addReward(Reward reward) {
        RewardAdditionEvent event = new RewardAdditionEvent(reward, this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        rewards.add(reward);
    }

    /**
     * @param reward
     * the rewards to remove
     */
    public void removeReward(Reward reward) {
        rewards.remove(reward);
    }

    /**
     * @return the "Time is Running" task of the game
     */
    public BukkitTask getTimeIsRunningTask() {
        return timeIsRunningTask;
    }

    /**
     * @param task
     * the task to set
     */
    public void setTimeIsRunningTask(BukkitTask task) {
        this.timeIsRunningTask = task;
    }

    /**
     * @return whether there are players in the group
     */
    public boolean isEmpty() {
        return players.isEmpty();
    }

    /**
     * @return if the group has been customized with a command
     */
    public boolean isCustom() {
        return !name.matches("Group_[0-9]{1,}");
    }

    /* Actions */
    /**
     * Remove the group from the List
     */
    public void delete() {
        Game game = Game.getByDGroup(this);

        plugin.getDGroups().remove(this);

        if (game != null) {
            game.removeDGroup(this);
        }

        if (timeIsRunningTask != null) {
            timeIsRunningTask.cancel();
        }

        GroupSign.updatePerGroup(this);
    }

    public void startGame(Game game) {
        if (game == null) {
            return;
        }
        game.fetchRules();

        for (DGroup dGroup : game.getDGroups()) {
            if (dGroup == null) {
                continue;
            }

            boolean ready = true;
            for (Player player : dGroup.getPlayers()) {
                DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
                if (dPlayer == null) {
                    dPlayer = new DGamePlayer(player, gameWorld);
                }

                if (!dPlayer.isReady()) {
                    ready = false;
                }
            }

            if (!ready) {
                return;
            }
        }

        DGroupStartFloorEvent event = new DGroupStartFloorEvent(this, gameWorld);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        playing = true;

        if (gameWorld != null) {
            if (!gameWorld.isPlaying()) {
                gameWorld.startGame();
            }
        }

        floorCount++;

        for (Player player : getPlayers()) {
            DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
            if (dPlayer == null) {
                continue;
            }

            dPlayer.respawn();

            if (plugin.getMainConfig().getSendFloorTitle()) {
                if (dungeonName != null) {
                    MessageUtil.sendTitleMessage(player, "&b&l" + dungeonName.replaceAll("_", " "), "&4&l" + mapName.replaceAll("_", " "));

                } else {
                    MessageUtil.sendTitleMessage(player, "&4&l" + mapName.replaceAll("_", " "));
                }
            }

            GameRules rules = game.getRules();

            for (Requirement requirement : rules.getRequirements()) {
                RequirementDemandEvent requirementDemandEvent = new RequirementDemandEvent(requirement, player);
                plugin.getServer().getPluginManager().callEvent(event);

                if (requirementDemandEvent.isCancelled()) {
                    continue;
                }

                requirement.demand(player);
            }

            GameType gameType = game.getType();
            if (gameType == GameTypeDefault.DEFAULT) {
                player.setGameMode(rules.getGameMode());
                if (rules.isTimeIsRunning()) {
                    timeIsRunningTask = new TimeIsRunningTask(this, rules.getTimeToFinish()).runTaskTimer(plugin, 20, 20);
                }

            } else {
                player.setGameMode(gameType.getGameMode());
                if (gameType.getShowTime()) {
                    timeIsRunningTask = new TimeIsRunningTask(this, rules.getTimeToFinish()).runTaskTimer(plugin, 20, 20);
                }
            }

            // Permission bridge
            if (plugin.getPermissionProvider() != null) {
                for (String permission : rules.getGamePermissions()) {
                    plugin.getPermissionProvider().playerRemoveTransient(gameWorld.getWorld().getName(), player, permission);
                }
            }
        }

        GroupSign.updatePerGroup(this);
    }

    public boolean checkTime(Game game) {
        if (DPermissions.hasPermission(captain, DPermissions.IGNORE_TIME_LIMIT)) {
            return true;
        }

        for (Player player : players) {
            if (!DGamePlayer.getByPlayer(player).checkTime(game)) {
                return false;
            }
        }

        return true;
    }

    public boolean checkRequirements(Game game) {
        if (DPermissions.hasPermission(captain, DPermissions.IGNORE_REQUIREMENTS)) {
            return true;
        }

        for (Player player : players) {
            if (!DGamePlayer.getByPlayer(player).checkRequirements(game)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Send a message to all players in the group
     */
    public void sendMessage(String message) {
        for (Player player : players) {
            if (player.isOnline()) {
                MessageUtil.sendMessage(player, message);
            }
        }
    }

    /**
     * Send a message to all players in the group
     *
     * @param except
     * Players who do not receive the message
     */
    public void sendMessage(String message, Player... except) {
        HashSet<Player> exceptSet = new HashSet<>(Arrays.asList(except));
        for (Player player : players) {
            if (player.isOnline() && !exceptSet.contains(player)) {
                MessageUtil.sendMessage(player, message);
            }
        }
    }

    /* Statics */
    public static DGroup getByName(String name) {
        for (DGroup dGroup : plugin.getDGroups()) {
            if (dGroup.getName().equals(name)) {
                return dGroup;
            }
        }

        return null;
    }

    public static DGroup getByPlayer(Player player) {
        for (DGroup dGroup : plugin.getDGroups()) {
            if (dGroup.getPlayers().contains(player)) {
                return dGroup;
            }
        }

        return null;
    }

    public static void leaveGroup(Player player) {
        for (DGroup dGroup : plugin.getDGroups()) {
            if (dGroup.getPlayers().contains(player)) {
                dGroup.getPlayers().remove(player);
            }
        }
    }

    /**
     * @param gameWorld
     * the GameWorld to check
     * @return a List of DGroups in this GameWorld
     */
    public static List<DGroup> getByGameWorld(GameWorld gameWorld) {
        List<DGroup> dGroups = new ArrayList<>();
        for (DGroup dGroup : plugin.getDGroups()) {
            if (dGroup.getGameWorld().equals(gameWorld)) {
                dGroups.add(dGroup);
            }
        }

        return dGroups;
    }

}
