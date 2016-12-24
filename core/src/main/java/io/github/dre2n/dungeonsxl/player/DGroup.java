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
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.requirement.Requirement;
import io.github.dre2n.dungeonsxl.reward.Reward;
import io.github.dre2n.dungeonsxl.task.TimeIsRunningTask;
import io.github.dre2n.dungeonsxl.util.DColor;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import io.github.dre2n.dungeonsxl.world.DResourceWorld;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

    static DungeonsXL plugin = DungeonsXL.getInstance();
    static DPlayers dPlayers = plugin.getDPlayers();

    private String name;
    private UUID captain;
    private List<UUID> players = new ArrayList<>();
    private List<UUID> invitedPlayers = new ArrayList<>();
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
        this("Group_" + plugin.getDGroups().size(), player);
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
        this("Group_" + plugin.getDGroups().size(), player, identifier, multiFloor);
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
        plugin.getServer().getPluginManager().callEvent(event);

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
            unplayedFloors = dungeon.getConfig().getFloors();

        } else {
            // Artificial dungeon
            DResourceWorld resource = plugin.getDWorlds().getResourceByName(identifier);
            dungeon = new Dungeon(resource);
        }

        playing = false;
        floorCount = 0;
    }

    public DGroup(Player player, Dungeon dungeon) {
        this("Group_" + plugin.getDGroups().size(), player, dungeon);
    }

    @Deprecated
    public DGroup(String name, Player player, Dungeon dungeon) {
        this(name, player, new ArrayList<Player>(), dungeon);
    }

    public DGroup(String name, Player captain, List<Player> players, Dungeon dungeon) {
        plugin.getDGroups().add(this);
        this.name = name;

        DPlayerJoinDGroupEvent event = new DPlayerJoinDGroupEvent(plugin.getDPlayers().getByPlayer(captain), true, this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.captain = captain.getUniqueId();
            this.players.add(captain.getUniqueId());
        }

        for (Player player : players) {
            if (!this.players.contains(player.getUniqueId())) {
                addPlayer(player);
            }
        }

        this.dungeon = dungeon;
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
     * @return the players as a Set<Player>
     */
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (UUID uuid : this.players) {
            players.add(Bukkit.getPlayer(uuid));
        }
        return players;
    }

    /**
     * @return the players as a Set<DGlobalPlayer>
     */
    public List<DGlobalPlayer> getDGlobalPlayers() {
        List<DGlobalPlayer> players = new ArrayList<>();
        for (UUID uuid : this.players) {
            players.add(dPlayers.getByPlayer(Bukkit.getPlayer(uuid)));
        }
        return players;
    }

    /**
     * @return the players as a Set<DGamePlayer>
     */
    public List<DGamePlayer> getDGamePlayers() {
        List<DGamePlayer> players = new ArrayList<>();
        for (UUID uuid : this.players) {
            DGlobalPlayer dPlayer = dPlayers.getByPlayer(Bukkit.getPlayer(uuid));
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
        plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (message) {
                sendMessage(DMessages.GROUP_PLAYER_JOINED.getMessage(player.getName()));
                MessageUtil.sendMessage(player, DMessages.PLAYER_JOIN_GROUP.getMessage());
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
            sendMessage(plugin.getMessageConfig().getMessage(DMessages.PLAYER_LEFT_GROUP, player.getName()));
        }

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
                MessageUtil.sendMessage(getCaptain(), plugin.getMessageConfig().getMessage(DMessages.ERROR_IN_GROUP, player.getName()));
            }
            return;
        }

        if (!silent) {
            MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.PLAYER_INVITED, getCaptain().getName(), name));
        }

        // Send message
        if (!silent) {
            sendMessage(plugin.getMessageConfig().getMessage(DMessages.GROUP_INVITED_PLAYER, getCaptain().getName(), player.getName(), name));
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
                MessageUtil.sendMessage(getCaptain(), plugin.getMessageConfig().getMessage(DMessages.ERROR_NOT_IN_GROUP, player.getName(), name));
            }
            return;
        }

        if (!silent) {
            MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.PLAYER_UNINVITED, player.getName(), name));
        }

        // Send message
        if (!silent) {
            for (Player groupPlayer : getPlayers()) {
                MessageUtil.sendMessage(groupPlayer, plugin.getMessageConfig().getMessage(DMessages.GROUP_UNINVITED_PLAYER, getCaptain().getName(), player.getName(), name));
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
     * @param dungeon
     * the dungeon to set
     */
    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    /**
     * Sets up all dungeon-related fields.
     *
     * @param name
     * the name of the dungeon
     */
    public void setDungeon(String name) {
        dungeon = plugin.getDungeons().getByName(name);
        if (dungeon != null) {
            unplayedFloors = dungeon.getConfig().getFloors();

        } else {
            DResourceWorld resource = plugin.getDWorlds().getResourceByName(name);
            dungeon = new Dungeon(resource);
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
        GameRules rules = game.getRules();

        color = plugin.getMainConfig().getGroupColorPriority().get(game.getDGroups().indexOf(this));

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
                plugin.getServer().getPluginManager().callEvent(event);

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
        String title = DMessages.GROUP_CONGRATS.getMessage();
        String subtitle = DMessages.GROUP_CONGRATS_SUB.getMessage(getName());
        for (DGamePlayer player : getDGamePlayers()) {
            player.leave(false);
            MessageUtil.sendTitleMessage(player.getPlayer(), title, subtitle, 20, 20, 100);
        }
    }

    public boolean checkTime(Game game) {
        if (DPermissions.hasPermission(getCaptain(), DPermissions.IGNORE_TIME_LIMIT)) {
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
        if (DPermissions.hasPermission(getCaptain(), DPermissions.IGNORE_REQUIREMENTS)) {
            return true;
        }

        for (DGamePlayer dPlayer : getDGamePlayers()) {
            if (!dPlayer.checkRequirements(game)) {
                return false;
            }
        }

        return true;
    }

    public boolean finishIfMembersFinished() {
        boolean finish = true;

        for (DGamePlayer dPlayer : getDGamePlayers()) {
            if (!dPlayer.isFinished()) {
                finish = false;
                break;
            }
        }

        if (finish && getDGamePlayers().size() > 0) {
            getDGamePlayers().get(0).finishFloor(nextFloor);
        }

        return finish;
    }

    /**
     * Send a message to all players in the group
     */
    public void sendMessage(String message) {
        for (Player player : getPlayers()) {
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
        for (Player player : getPlayers()) {
            if (player.isOnline() && !exceptSet.contains(player)) {
                MessageUtil.sendMessage(player, message);
            }
        }
    }

    /* Statics */
    public static DGroup getByName(String name) {
        for (DGroup dGroup : plugin.getDGroups()) {
            if (dGroup.getName().equalsIgnoreCase(name) || dGroup.getRawName().equalsIgnoreCase(name)) {
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
     * the DGameWorld to check
     * @return a List of DGroups in this DGameWorld
     */
    public static List<DGroup> getByGameWorld(DGameWorld gameWorld) {
        List<DGroup> dGroups = new ArrayList<>();
        for (DGroup dGroup : plugin.getDGroups()) {
            if (dGroup.getGameWorld().equals(gameWorld)) {
                dGroups.add(dGroup);
            }
        }

        return dGroups;
    }

}
