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
package de.erethon.dungeonsxl.player;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.player.PlayerCollection;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.player.PlayerGroup.Color;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.dungeon.DungeonConfig;
import de.erethon.dungeonsxl.event.dgroup.DGroupDisbandEvent;
import de.erethon.dungeonsxl.event.dgroup.DGroupFinishDungeonEvent;
import de.erethon.dungeonsxl.event.dgroup.DGroupFinishFloorEvent;
import de.erethon.dungeonsxl.event.dgroup.DGroupStartFloorEvent;
import de.erethon.dungeonsxl.event.dplayer.DPlayerJoinDGroupEvent;
import de.erethon.dungeonsxl.event.requirement.RequirementDemandEvent;
import de.erethon.dungeonsxl.event.reward.RewardAdditionEvent;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.game.GameRuleProvider;
import de.erethon.dungeonsxl.requirement.Requirement;
import de.erethon.dungeonsxl.reward.Reward;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Represents a group of players.
 *
 * @author Frank Baumann, Daniel Saukel
 */
public class DGroup {

    DungeonsXL plugin;
    DPlayerCache dPlayers;

    private static int counter;

    private int id;
    private String name;
    private Player captain;
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
    private Color color;
    private int score = 0;
    private int initialLives = -1;
    private int lives = -1;

    public DGroup(DungeonsXL plugin, Player player) {
        this(plugin, "Group#" + counter, player);
    }

    public DGroup(DungeonsXL plugin, Player player, Color color) {
        this(plugin, color.toString() + "#" + counter, player);
    }

    public DGroup(DungeonsXL plugin, String name, Player player) {
        this.plugin = plugin;
        dPlayers = plugin.getDPlayerCache();

        plugin.getDGroupCache().add(this);
        this.name = name;

        setCaptain(player);
        addPlayer(player);

        playing = false;
        floorCount = 0;

        id = counter++;
    }

    public DGroup(DungeonsXL plugin, Player player, Dungeon dungeon) {
        this(plugin, "Group#" + counter, player, dungeon);
    }

    public DGroup(DungeonsXL plugin, String name, Player player, Dungeon dungeon) {
        this(plugin, name, player, new ArrayList<Player>(), dungeon);
    }

    public DGroup(DungeonsXL plugin, String name, Player captain, List<Player> players, Dungeon dungeon) {
        this.plugin = plugin;
        dPlayers = plugin.getDPlayerCache();

        plugin.getDGroupCache().add(this);
        this.name = name;

        DPlayerJoinDGroupEvent event = new DPlayerJoinDGroupEvent(plugin.getDPlayerCache().getByPlayer(captain), true, this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.captain = captain;
            this.players.add(captain);
        }

        for (Player player : players) {
            if (!this.players.contains(player)) {
                addPlayer(player);
            }
        }

        setDungeon(dungeon);
        playing = false;
        floorCount = 0;

        id = counter++;
    }

    // Getters and setters
    /**
     * @return the group ID
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name; formatted
     */
    public String getName() {
        return getDColor().getChatColor() + name;
    }

    /**
     * @return the name; not formatted
     */
    public String getRawName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param color the color to fetch the name from
     */
    public void setName(Color color) {
        name = color.toString() + "#" + id;
    }

    /**
     * @return the captain
     */
    public Player getCaptain() {
        return captain;
    }

    /**
     * @param captain the captain to set
     */
    public void setCaptain(Player captain) {
        this.captain = captain;
    }

    /**
     * @return the players
     */
    public PlayerCollection getPlayers() {
        return players;
    }

    /**
     * @return the players as a Set&lt;DGlobalPlayer&gt;
     */
    public Set<DGlobalPlayer> getDGlobalPlayers() {
        Set<DGlobalPlayer> players = new HashSet<>();
        for (UUID uuid : this.players) {
            players.add(dPlayers.getByUniqueId(uuid));
        }
        return players;
    }

    /**
     * @return the players as a Set&lt;DGamePlayer&gt;
     */
    public Set<DGamePlayer> getDGamePlayers() {
        Set<DGamePlayer> players = new HashSet<>();
        for (UUID uuid : this.players) {
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
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        addPlayer(player, true);
    }

    /**
     * @param player  the player to add
     * @param message if messages should be sent
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
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        removePlayer(player, true);
    }

    /**
     * @param player  the player to remove
     * @param message if messages should be sent
     */
    public void removePlayer(Player player, boolean message) {
        players.remove(player.getUniqueId());
        plugin.getGlobalProtectionCache().updateGroupSigns(this);

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
     * @param player the player to add
     * @param silent if messages shall be sent
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
     * @param player the player to remove
     * @param silent if messages shall be sent
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
     * @param gameWorld the gameWorld to set
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
     * @param dungeon the dungeon to set
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
     * @param name the name of the dungeon
     * @return if the action was successful
     */
    public boolean setDungeon(String name) {
        dungeon = plugin.getDungeonCache().getByName(name);
        if (dungeon != null) {
            unplayedFloors = dungeon.getConfig().getFloors();
            return true;

        } else {
            DResourceWorld resource = plugin.getDWorldCache().getResourceByName(name);
            if (resource != null) {
                dungeon = new Dungeon(plugin, resource);
                return true;
            }
            return false;
        }
    }

    /**
     * @return the dungeonName
     */
    public String getDungeonName() {
        if (dungeon == null) {
            return null;
        }
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
     * @param unplayedFloor the unplayed floor to add
     */
    public void addUnplayedFloor(DResourceWorld unplayedFloor) {
        unplayedFloors.add(unplayedFloor);
    }

    /**
     * @param unplayedFloor the unplayed floor to remove
     * @param force         remove the floor even if removeWhenPlayed is disabled
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
     * @param playing set if the group is playing
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
     * @param floorCount the floorCount to set
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
     * @param reward the rewards to add
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
     * @param reward the rewards to remove
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
     * @param task the task to set
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
        return !name.matches("Group#[0-9]{1,}");
    }

    /**
     * @return the next floor the group will enter
     */
    public DResourceWorld getNextFloor() {
        return nextFloor;
    }

    /**
     * @param floor the next floor to set
     */
    public void setNextFloor(DResourceWorld floor) {
        nextFloor = floor;
    }

    /**
     * @return the color that represents this group
     */
    public Color getDColor() {
        if (color != null) {
            return color;
        } else {
            return Color.WHITE;
        }
    }

    /**
     * @param color the group color to set
     */
    public void setDColor(Color color) {
        this.color = color;
    }

    /**
     * @return the current score
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score the score to set
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
     * @param initialLives the initial group lives to set
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
     * @param lives the group lives to set
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
    public boolean teleport() {
        if (dungeon == null || dungeon.getMap() == null) {
            sendMessage(DMessage.ERROR_NO_SUCH_DUNGEON.getMessage());
            return false;
        }

        DGameWorld target = dungeon.getMap().instantiateAsGameWorld(false);
        Game game = Game.getByDGroup(this);

        if (target == null && game != null) {
            target = game.getWorld();
        }

        if (target == null) {
            if (game != null) {
                for (DGroup otherTeam : game.getDGroups()) {
                    if (otherTeam.getGameWorld() != null) {
                        target = otherTeam.getGameWorld();
                        break;
                    }
                }
            }
        }

        if (target == null && dungeon != null) {
            DResourceWorld resource = dungeon.getMap();
            if (resource != null) {
                target = resource.instantiateAsGameWorld(false);
                if (target == null) {
                    sendMessage(DMessage.ERROR_TOO_MANY_INSTANCES.getMessage());
                    return false;
                }
                gameWorld = target;
            }
        }

        if (target == null) {
            sendMessage(DMessage.ERROR_NO_SUCH_DUNGEON.getMessage());
            return false;
        }

        if (game == null) {
            game = new Game(plugin, this, target);

        } else {
            game.setWorld(target);
            gameWorld = target;
        }

        for (OfflinePlayer offline : players.getOfflinePlayers()) {
            if (!offline.isOnline()) {
                players.remove(offline);
            }
            Player player = offline.getPlayer();
            new DGamePlayer(plugin, player, target);
        }
        return true;
    }

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
        getDGamePlayers().forEach(p -> p.leave(false));
    }

    /**
     * The group finishs the current floor.
     *
     * @param specifiedFloor the name of the next floor
     */
    public void finishFloor(DResourceWorld specifiedFloor) {
        DungeonConfig dConfig = dungeon.getConfig();
        int floorsLeft = getDungeon().getFloors().size() + 1 - floorCount;//floorCount contains start floor, but dungeon floor list doesn't
        DResourceWorld newFloor = null;
        DGameWorld.Type type = null;
        if (gameWorld.getType() == DGameWorld.Type.END_FLOOR) {
            finish();
            return;
        } else if (specifiedFloor != null) {
            newFloor = specifiedFloor;
            type = DGameWorld.Type.DEFAULT;
        } else if (floorsLeft > 0) {
            int random = new Random().nextInt(floorsLeft);
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
        DGameWorld gameWorld = newFloor.instantiateAsGameWorld(true);
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

        plugin.getDGroupCache().remove(this);

        if (game != null) {
            game.removeDGroup(this);
        }

        for (UUID uuid : players.getUniqueIds()) {
            DGlobalPlayer member = dPlayers.getByUniqueId(uuid);
            if (member instanceof DInstancePlayer) {
                ((DInstancePlayer) member).leave();
            }
        }

        if (timeIsRunningTask != null) {
            timeIsRunningTask.cancel();
        }

        plugin.getGlobalProtectionCache().updateGroupSigns(this);
    }

    public boolean startGame(Game game) {
        if (game == null) {
            return false;
        }
        game.fetchRules();
        GameRuleProvider rules = game.getRules();
        gameWorld.setWeather(rules);

        if (color == null) {
            color = plugin.getMainConfig().getGroupColorPriority((game.getDGroups().indexOf(this)));
        }

        for (DGroup dGroup : game.getDGroups()) {
            if (dGroup == null) {
                continue;
            }

            boolean ready = true;
            for (Player player : dGroup.getPlayers().getOnlinePlayers()) {
                DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
                if (dPlayer == null) {
                    dPlayer = new DGamePlayer(plugin, player, gameWorld);
                }
                if (rules.isGroupTagEnabled()) {
                    dPlayer.initDGroupTag();
                }
                if (!dPlayer.isReady()) {
                    ready = false;
                }
            }

            if (!ready) {
                return false;
            }
        }

        DGroupStartFloorEvent event = new DGroupStartFloorEvent(this, gameWorld);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
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
            dPlayer.getData().logTimeLastStarted(getDungeonName());
            dPlayer.getData().setKeepInventoryAfterLogout(rules.getKeepInventoryOnEscape());

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

                if (!DPermission.hasPermission(player, DPermission.IGNORE_REQUIREMENTS)) {
                    requirement.demand(player);
                }
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

        plugin.getGlobalProtectionCache().updateGroupSigns(this);
        nextFloor = null;
        initialLives = rules.getInitialGroupLives();
        lives = initialLives;
        return true;
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
            if (!dPlayer.checkTimeAfterStart(game) || !dPlayer.checkTimeAfterFinish(game)) {
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
     * Sends a message to all players in the group.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        for (Player player : players.getOnlinePlayers()) {
            if (player.isOnline()) {
                MessageUtil.sendMessage(player, message);
            }
        }
    }

    /**
     * Sends a message to all players in the group.
     *
     * @param message the message to sent
     * @param except  Players who shall not receive the message
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
        for (DGroup dGroup : DungeonsXL.getInstance().getDGroupCache()) {
            if (dGroup.getName().equalsIgnoreCase(name) || dGroup.getRawName().equalsIgnoreCase(name)) {
                return dGroup;
            }
        }

        return null;
    }

    public static DGroup getByPlayer(Player player) {
        for (DGroup dGroup : DungeonsXL.getInstance().getDGroupCache()) {
            if (dGroup.getPlayers().contains(player)) {
                return dGroup;
            }
        }

        return null;
    }

    public static void leaveGroup(Player player) {
        for (DGroup dGroup : DungeonsXL.getInstance().getDGroupCache()) {
            if (dGroup.getPlayers().contains(player)) {
                dGroup.getPlayers().remove(player);
            }
        }
    }

    /**
     * @param gameWorld the DGameWorld to check
     * @return a List of DGroups in this DGameWorld
     */
    public static List<DGroup> getByGameWorld(DGameWorld gameWorld) {
        List<DGroup> dGroups = new ArrayList<>();
        for (DGroup dGroup : DungeonsXL.getInstance().getDGroupCache()) {
            if (dGroup.getGameWorld().equals(gameWorld)) {
                dGroups.add(dGroup);
            }
        }

        return dGroups;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name=" + name + "; captain=" + captain + "}";
    }

}
