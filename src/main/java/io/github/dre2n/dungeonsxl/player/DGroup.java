/*
 * Copyright (C) 2012-2018 Frank Baumann
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

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.commons.player.PlayerCollection;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.dungeon.DungeonConfig;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupDisbandEvent;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupFinishDungeonEvent;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupFinishFloorEvent;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupRewardEvent;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupStartFloorEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerJoinDGroupEvent;
import io.github.dre2n.dungeonsxl.event.requirement.RequirementDemandEvent;
import io.github.dre2n.dungeonsxl.event.reward.RewardAdditionEvent;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameRuleProvider;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.requirement.Requirement;
import io.github.dre2n.dungeonsxl.reward.Reward;
import io.github.dre2n.dungeonsxl.util.DColor;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import io.github.dre2n.dungeonsxl.world.DResourceWorld;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Represents a group of players.
 *
 * @author Frank Baumann, Daniel Saukel
 */
public class DGroup {

    DungeonsXL plugin = DungeonsXL.getInstance();
    DPlayerCache dPlayers = plugin.getDPlayers();

    private String name;
    private UUID captain;
    private PlayerCollection players = new PlayerCollection();
    private PlayerCollection invitedPlayers = new PlayerCollection();
    private Dungeon dungeon;
    private List<DResourceWorld> unplayedFloors = new ArrayList<>();
    private DGameWorld gameWorld;
    private boolean playing;
    private int floorCount;
    private List<Reward> rewards = new ArrayList<>();
    private BukkitTask timeIsRunningTask;
    private DResourceWorld nextFloor;
    private DColor color;
    private int score = 0;
    private int initialLives = -1;
    private int lives = -1;

    public DGroup(Player player) {
        this("Group " + DungeonsXL.getInstance().getDGroups().size(), player);
    }

    public DGroup(Player player, DColor color) {
        this(color.toString().replace("_", " "), player);
    }

    public DGroup(String name, Player player) {
        plugin.getDGroups().add(this);
        this.name = name;

        setCaptain(player);
        addPlayer(player);

        playing = false;
        floorCount = 0;
    }

    @Deprecated
    public DGroup(Player player, String identifier, boolean multiFloor) {
        this(DungeonsXL.getInstance().getMainConfig().getGroupColorPriority().get(DungeonsXL.getInstance().getDGroups().size()).toString(), player, identifier, multiFloor);
    }

    @Deprecated
    public DGroup(String name, Player player, String identifier, boolean multiFloor) {
        this(name, player, new ArrayList<Player>(), identifier, multiFloor);
    }

    @Deprecated
    public DGroup(String name, Player captain, List<Player> players, String identifier, boolean multiFloor) {
        plugin.getDGroups().add(this);
        this.name = name;

        DPlayerJoinDGroupEvent event = new DPlayerJoinDGroupEvent(plugin.getDPlayers().getByPlayer(captain), true, this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.captain = captain.getUniqueId();
            this.players.add(captain.getUniqueId());
        }

        for (Player player : players) {
            if (!this.players.contains(player.getUniqueId())) {
                addPlayer(player);
            }
        }

        dungeon = plugin.getDungeons().getByName(identifier);
        if (multiFloor && dungeon != null) {
            // Real dungeon
            unplayedFloors = new ArrayList<>(dungeon.getConfig().getFloors());

        } else {
            // Artificial dungeon
            DResourceWorld resource = plugin.getDWorlds().getResourceByName(identifier);
            dungeon = new Dungeon(resource);
        }

        playing = false;
        floorCount = 0;
    }

    public DGroup(Player player, Dungeon dungeon) {
        this(DungeonsXL.getInstance().getMainConfig().getGroupColorPriority().get(DungeonsXL.getInstance().getDGroups().size()).toString(), player, dungeon);
    }

    public DGroup(String name, Player player, Dungeon dungeon) {
        this(name, player, new ArrayList<Player>(), dungeon);
    }

    public DGroup(String name, Player captain, List<Player> players, Dungeon dungeon) {
        plugin.getDGroups().add(this);
        this.name = name;

        DPlayerJoinDGroupEvent event = new DPlayerJoinDGroupEvent(plugin.getDPlayers().getByPlayer(captain), true, this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.captain = captain.getUniqueId();
            this.players.add(captain.getUniqueId());
        }

        for (Player player : players) {
            if (!this.players.contains(player.getUniqueId())) {
                addPlayer(player);
            }
        }

        setDungeon(dungeon);
        playing = false;
        floorCount = 0;
    }

    // Getters and setters
    /**
     * @return the name; formatted
     */
    public String getName() {
        return (color != null ? color.getChatColor().toString() : new String()) + name;
    }

    /**
     * @return the name; not formatted
     */
    public String getRawName() {
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
     * @param name
     * the name to set
     */
    public void setName(DColor color) {
        name = color.toString().replace("_", " ");
    }

    /**
     * @return the captain
     */
    public Player getCaptain() {
        return Bukkit.getPlayer(captain);
    }

    /**
     * @param captain
     * the captain to set
     */
    public void setCaptain(Player captain) {
        this.captain = captain.getUniqueId();
    }

    /**
     * @return the players
     */
    public PlayerCollection getPlayers() {
        return players;
    }

    /**
     * @return the players as a Set<DGlobalPlayer>
     */
    public Set<DGlobalPlayer> getDGlobalPlayers() {
        Set<DGlobalPlayer> players = new HashSet<>();
        for (UUID uuid : this.players.getUniqueIds()) {
            players.add(dPlayers.getByUniqueId(uuid));
        }
        return players;
    }

    /**
     * @return the players as a Set<DGamePlayer>
     */
    public Set<DGamePlayer> getDGamePlayers() {
        Set<DGamePlayer> players = new HashSet<>();
        for (UUID uuid : this.players.getUniqueIds()) {
            DGlobalPlayer dPlayer = dPlayers.getByUniqueId(uuid);
            if (dPlayer instanceof DGamePlayer) {
                players.add((DGamePlayer) dPlayer);
            }
        }
        return players;
    }

    /**
     * Sends messages by default.
     *
     * @param player
     * the player to add
     */
    public void addPlayer(Player player) {
        addPlayer(player, true);
    }

    /**
     * @param player
     * the player to add
     * @param message
     * if messages should be sent
     */
    public void addPlayer(Player player, boolean message) {
        DPlayerJoinDGroupEvent event = new DPlayerJoinDGroupEvent(DGamePlayer.getByPlayer(player), false, this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (message) {
                sendMessage(DMessage.GROUP_PLAYER_JOINED.getMessage(player.getName()));
                MessageUtil.sendMessage(player, DMessage.PLAYER_JOIN_GROUP.getMessage());
            }

            players.add(player.getUniqueId());
        }
    }

    /**
     * Sends messages by default.
     *
     * @param player
     * the player to remove
     */
    public void removePlayer(Player player) {
        removePlayer(player, true);
    }

    /**
     * @param player
     * the player to remove
     * @param message
     * if messages should be sent
     */
    public void removePlayer(Player player, boolean message) {
        players.remove(player.getUniqueId());
        GroupSign.updatePerGroup(this);

        if (message) {
            sendMessage(DMessage.PLAYER_LEFT_GROUP.getMessage(player.getName()));
        }

        if (isEmpty()) {
            DGroupDisbandEvent event = new DGroupDisbandEvent(this, player, DGroupDisbandEvent.Cause.GROUP_IS_EMPTY);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                delete();
            }
        }
    }

    /**
     * @return the players
     */
    public PlayerCollection getInvitedPlayers() {
        return invitedPlayers;
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
                MessageUtil.sendMessage(getCaptain(), DMessage.ERROR_IN_GROUP.getMessage(player.getName()));
            }
            return;
        }

        if (!silent) {
            MessageUtil.sendMessage(player, DMessage.PLAYER_INVITED.getMessage(getCaptain().getName(), name));
        }

        // Send message
        if (!silent) {
            sendMessage(DMessage.GROUP_INVITED_PLAYER.getMessage(getCaptain().getName(), player.getName(), name));
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
                MessageUtil.sendMessage(getCaptain(), DMessage.ERROR_NOT_IN_GROUP.getMessage(player.getName(), name));
            }
            return;
        }

        if (!silent) {
            MessageUtil.sendMessage(player, DMessage.PLAYER_UNINVITED.getMessage(player.getName(), name));
        }

        // Send message
        if (!silent) {
            for (Player groupPlayer : players.getOnlinePlayers()) {
                MessageUtil.sendMessage(groupPlayer, DMessage.GROUP_UNINVITED_PLAYER.getMessage(getCaptain().getName(), player.getName(), name));
            }
        }

        invitedPlayers.remove(player.getUniqueId());
    }

    /**
     * Remove all invitations for players who are not online
     */
    public void clearOfflineInvitedPlayers() {
        ArrayList<UUID> toRemove = new ArrayList<>();
        for (UUID uuid : invitedPlayers.getUniqueIds()) {
            if (Bukkit.getPlayer(uuid) == null) {
                toRemove.add(uuid);
            }
        }
        invitedPlayers.removeAll(toRemove);
    }

    /**
     * @return the gameWorld
     */
    public DGameWorld getGameWorld() {
        return gameWorld;
    }

    /**
     * @param gameWorld
     * the gameWorld to set
     */
    public void setGameWorld(DGameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    /**
     * @return the dungeon
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * Sets up all dungeon-related fields.
     *
     * @param dungeon
     * the dungeon to set
     */
    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
        if (dungeon.isMultiFloor()) {
            unplayedFloors = new ArrayList<>(dungeon.getConfig().getFloors());
        }
    }

    /**
     * Sets up all dungeon-related fields.
     *
     * @param name
     * the name of the dungeon
     */
    public boolean setDungeon(String name) {
        dungeon = plugin.getDungeons().getByName(name);
        if (dungeon != null) {
            unplayedFloors = dungeon.getConfig().getFloors();
            return true;

        } else {
            DResourceWorld resource = plugin.getDWorlds().getResourceByName(name);
            if (resource != null) {
                dungeon = new Dungeon(resource);
                return true;
            }
            return false;
        }
    }

    /**
     * @return the dungeonName
     */
    public String getDungeonName() {
        return dungeon.getName();
    }

    /**
     * @return if the group is playing
     */
    public String getMapName() {
        return gameWorld == null ? null : gameWorld.getName();
    }

    /**
     * @return the unplayed floors
     */
    public List<DResourceWorld> getUnplayedFloors() {
        return unplayedFloors;
    }

    /**
     * @param unplayedFloor
     * the unplayed floor to add
     */
    public void addUnplayedFloor(DResourceWorld unplayedFloor) {
        unplayedFloors.add(unplayedFloor);
    }

    /**
     * @param unplayedFloor
     * the unplayed floor to remove
     * @param force
     * remove the floor even if removeWhenPlayed is disabled
     */
    public void removeUnplayedFloor(DResourceWorld unplayedFloor, boolean force) {
        if (getDungeon().getConfig().getRemoveWhenPlayed() || force) {
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
        Bukkit.getPluginManager().callEvent(event);

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
        return players.size() == 0;
    }

    /**
     * @return if the group has been customized with a command
     */
    public boolean isCustom() {
        return !name.matches("Group_[0-9]{1,}");
    }

    /**
     * @return the next floor the group will enter
     */
    public DResourceWorld getNextFloor() {
        return nextFloor;
    }

    /**
     * @param floor
     * the next floor to set
     */
    public void setNextFloor(DResourceWorld floor) {
        nextFloor = floor;
    }

    /**
     * @return the color that represents this group
     */
    public DColor getDColor() {
        if (color != null) {
            return color;
        } else {
            return DColor.DEFAULT;
        }
    }

    /**
     * @param color the group color to set
     */
    public void setDColor(DColor color) {
        this.color = color;
    }

    /**
     * @return the current score
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score
     * the score to set
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return the initial group lives
     */
    public int getInitialLives() {
        return initialLives;
    }

    /**
     * @param initialLives
     * the initial group lives to set
     */
    public void setInitialLives(int initialLives) {
        this.initialLives = initialLives;
    }

    /**
     * @return the group lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * @param lives
     * the group lives to set
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * @return true if all players are finished
     */
    public boolean isFinished() {
        for (DGamePlayer dPlayer : getDGamePlayers()) {
            if (!dPlayer.isFinished()) {
                return false;
            }
        }
        return true;
    }

    /* Actions */
    /**
     * The group finishs the dungeon.
     */
    public void finish() {
        DGroupFinishDungeonEvent dGroupFinishDungeonEvent = new DGroupFinishDungeonEvent(dungeon, this);
        Bukkit.getPluginManager().callEvent(dGroupFinishDungeonEvent);
        if (dGroupFinishDungeonEvent.isCancelled()) {
            return;
        }

        Game.getByDGroup(this).resetWaveKills();
        DGroupRewardEvent dGroupRewardEvent = new DGroupRewardEvent(this);
        Bukkit.getPluginManager().callEvent(dGroupRewardEvent);
        for (DGamePlayer player : getDGamePlayers()) {
            player.leave(false);
            if (!dGroupRewardEvent.isCancelled()) {
                for (Reward reward : rewards) {
                    reward.giveTo(player.getPlayer());
                }
            }
        }
    }

    /**
     * The group finishs the current floor.
     *
     * @param specifiedFloor
     * the name of the next floor
     */
    public void finishFloor(DResourceWorld specifiedFloor) {
        DungeonConfig dConfig = dungeon.getConfig();
        int floorsLeft = getUnplayedFloors().size();
        DResourceWorld newFloor = null;
        DGameWorld.Type type = null;
        if (gameWorld.getType() == DGameWorld.Type.END_FLOOR) {
            finish();
            return;
        } else if (specifiedFloor != null) {
            newFloor = specifiedFloor;
            type = DGameWorld.Type.DEFAULT;
        } else if (floorsLeft > 0) {
            int random = NumberUtil.generateRandomInt(0, floorsLeft);
            newFloor = getUnplayedFloors().get(random);
            type = DGameWorld.Type.DEFAULT;
        } else {
            newFloor = dConfig.getEndFloor();
            type = DGameWorld.Type.END_FLOOR;
        }

        DGroupFinishFloorEvent event = new DGroupFinishFloorEvent(this, gameWorld, newFloor);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        Game game = gameWorld.getGame();
        removeUnplayedFloor(gameWorld.getResource(), false);
        DGameWorld gameWorld = newFloor.instantiateAsGameWorld();
        gameWorld.setType(type);
        this.gameWorld = gameWorld;
        game.setWorld(gameWorld);

        for (DGamePlayer player : getDGamePlayers()) {
            player.setWorld(gameWorld.getWorld());
            player.setCheckpoint(gameWorld.getStartLocation(this));
            if (player.getWolf() != null) {
                player.getWolf().teleport(player.getCheckpoint());
            }
            player.setFinished(false);
        }
        startGame(game);
    }

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
        GameRuleProvider rules = game.getRules();

        color = plugin.getMainConfig().getGroupColorPriority().get(game.getDGroups().indexOf(this));

        for (DGroup dGroup : game.getDGroups()) {
            if (dGroup == null) {
                continue;
            }

            boolean ready = true;
            for (Player player : dGroup.getPlayers().getOnlinePlayers()) {
                DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
                if (dPlayer == null) {
                    dPlayer = new DGamePlayer(player, gameWorld);
                }
                if (rules.isGroupTagEnabled()) {
                    dPlayer.initDGroupTag();
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
        Bukkit.getPluginManager().callEvent(event);

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

        for (Player player : players.getOnlinePlayers()) {
            DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
            if (dPlayer == null) {
                continue;
            }

            dPlayer.respawn();

            if (plugin.getMainConfig().isSendFloorTitleEnabled()) {
                if (rules.getTitle() != null || rules.getSubTitle() != null) {
                    String title = rules.getTitle() == null ? "" : rules.getTitle();
                    String subtitle = rules.getSubTitle() == null ? "" : rules.getSubTitle();

                    MessageUtil.sendTitleMessage(player, title, subtitle, rules.getTitleFadeIn(), rules.getTitleShow(), rules.getTitleFadeOut());

                } else if (!getDungeonName().equals(getMapName())) {
                    MessageUtil.sendTitleMessage(player, "&b&l" + getDungeonName().replaceAll("_", " "), "&4&l" + getMapName().replaceAll("_", " "));

                } else {
                    MessageUtil.sendTitleMessage(player, "&4&l" + getMapName().replaceAll("_", " "));
                }

                if (rules.getActionBar() != null) {
                    MessageUtil.sendActionBarMessage(player, rules.getActionBar());
                }

                if (rules.getChatText() != null) {
                    MessageUtil.sendCenteredMessage(player, rules.getChatText());
                }
            }

            for (Requirement requirement : rules.getRequirements()) {
                RequirementDemandEvent requirementDemandEvent = new RequirementDemandEvent(requirement, player);
                Bukkit.getPluginManager().callEvent(event);

                if (requirementDemandEvent.isCancelled()) {
                    continue;
                }

                requirement.demand(player);
            }

            player.setGameMode(rules.getGameMode());
            if (rules.isTimeIsRunning()) {
                timeIsRunningTask = new TimeIsRunningTask(this, rules.getTimeToFinish()).runTaskTimer(plugin, 20, 20);
            }

            // Permission bridge
            if (plugin.getPermissionProvider() != null) {
                for (String permission : rules.getGamePermissions()) {
                    plugin.getPermissionProvider().playerRemoveTransient(gameWorld.getWorld().getName(), player, permission);
                }
            }
        }

        GroupSign.updatePerGroup(this);
        nextFloor = null;
        initialLives = rules.getInitialGroupLives();
        lives = initialLives;
    }

    public void winGame() {
        String title = DMessage.GROUP_CONGRATS.getMessage();
        String subtitle = DMessage.GROUP_CONGRATS_SUB.getMessage(getName());
        for (DGamePlayer player : getDGamePlayers()) {
            player.leave(false);
            MessageUtil.sendTitleMessage(player.getPlayer(), title, subtitle, 20, 20, 100);
        }
    }

    public boolean checkTime(Game game) {
        if (DPermission.hasPermission(getCaptain(), DPermission.IGNORE_TIME_LIMIT)) {
            return true;
        }

        for (DGamePlayer dPlayer : getDGamePlayers()) {
            if (!dPlayer.checkTime(game)) {
                return false;
            }
        }

        return true;
    }

    public boolean checkRequirements(Game game) {
        if (DPermission.hasPermission(getCaptain(), DPermission.IGNORE_REQUIREMENTS)) {
            return true;
        }

        for (DGamePlayer dPlayer : getDGamePlayers()) {
            if (!dPlayer.checkRequirements(game)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Send a message to all players in the group
     */
    public void sendMessage(String message) {
        for (Player player : players.getOnlinePlayers()) {
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
        for (Player player : players.getOnlinePlayers()) {
            if (player.isOnline() && !exceptSet.contains(player)) {
                MessageUtil.sendMessage(player, message);
            }
        }
    }

    /* Statics */
    public static DGroup getByName(String name) {
        for (DGroup dGroup : DungeonsXL.getInstance().getDGroups()) {
            if (dGroup.getName().equalsIgnoreCase(name) || dGroup.getRawName().equalsIgnoreCase(name)) {
                return dGroup;
            }
        }

        return null;
    }

    public static DGroup getByPlayer(Player player) {
        for (DGroup dGroup : DungeonsXL.getInstance().getDGroups()) {
            if (dGroup.getPlayers().contains(player)) {
                return dGroup;
            }
        }

        return null;
    }

    public static void leaveGroup(Player player) {
        for (DGroup dGroup : DungeonsXL.getInstance().getDGroups()) {
            if (dGroup.getPlayers().contains(player)) {
                dGroup.getPlayers().remove(player);
            }
        }
    }

    /**
     * @param gameWorld
     * the DGameWorld to check
     * @return a List of DGroups in this DGameWorld
     */
    public static List<DGroup> getByGameWorld(DGameWorld gameWorld) {
        List<DGroup> dGroups = new ArrayList<>();
        for (DGroup dGroup : DungeonsXL.getInstance().getDGroups()) {
            if (dGroup.getGameWorld().equals(gameWorld)) {
                dGroups.add(dGroup);
            }
        }

        return dGroups;
    }

}
