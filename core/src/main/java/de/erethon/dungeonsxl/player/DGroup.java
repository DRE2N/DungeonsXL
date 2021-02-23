/*
 * Copyright (C) 2012-2021 Frank Baumann
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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
import de.erethon.dungeonsxl.api.event.group.GroupCollectRewardEvent;
import de.erethon.dungeonsxl.api.event.group.GroupDisbandEvent;
import de.erethon.dungeonsxl.api.event.group.GroupFinishDungeonEvent;
import de.erethon.dungeonsxl.api.event.group.GroupFinishFloorEvent;
import de.erethon.dungeonsxl.api.event.group.GroupPlayerJoinEvent;
import de.erethon.dungeonsxl.api.event.group.GroupStartFloorEvent;
import de.erethon.dungeonsxl.api.player.GamePlayer;
import de.erethon.dungeonsxl.api.player.GlobalPlayer;
import de.erethon.dungeonsxl.api.player.InstancePlayer;
import de.erethon.dungeonsxl.api.player.PlayerCache;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.player.PlayerGroup.Color;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.DDungeon;
import de.erethon.dungeonsxl.dungeon.DGame;
import de.erethon.dungeonsxl.dungeon.DungeonConfig;
import de.erethon.dungeonsxl.global.GroupSign;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.util.commons.player.PlayerCollection;
import de.erethon.dungeonsxl.world.DResourceWorld;
import java.util.ArrayList;
import java.util.Collection;
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
 * @author Frank Baumann, Daniel Saukel
 */
public class DGroup implements PlayerGroup {

    private DungeonsXL plugin;
    private PlayerCache dPlayers;

    private static int counter;

    private int id;
    private String name;
    private GroupSign groupSign;
    private Player captain;
    private PlayerCollection players = new PlayerCollection();
    private PlayerCollection invitedPlayers = new PlayerCollection();
    private Dungeon dungeon;
    private Game game;
    private GameWorld gameWorld;
    private boolean playing;
    private List<Reward> rewards = new ArrayList<>();
    private BukkitTask timeIsRunningTask;
    private Color color;
    private int score = 0;
    private int initialLives = -1;
    private int lives = -1;

    public DGroup(DungeonsXL plugin, Player player) {
        this(plugin, "Group", player);
    }

    public DGroup(DungeonsXL plugin, Player player, Color color) {
        this(plugin, color.toString(), player);
    }

    public DGroup(DungeonsXL plugin, String name, Player player) {
        this.plugin = plugin;
        dPlayers = plugin.getPlayerCache();

        plugin.getGroupCache().add(name, this);
        id = counter++;
        this.name = name + "#" + id;

        GroupPlayerJoinEvent event = new GroupPlayerJoinEvent(this, dPlayers.get(player), true);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            setLeader(player);
            addMember(player);
        } else {
            plugin.getGroupCache().remove(this);
            return;
        }

        playing = false;
    }

    public DGroup(DungeonsXL plugin, Player player, Dungeon dungeon) {
        this(plugin, "Group", player, dungeon);
    }

    public DGroup(DungeonsXL plugin, String name, Player player, Dungeon dungeon) {
        this(plugin, name, player, new ArrayList<Player>(), dungeon);
    }

    public DGroup(DungeonsXL plugin, String name, Player captain, Collection<Player> players, Dungeon dungeon) {
        this.plugin = plugin;
        dPlayers = plugin.getPlayerCache();

        plugin.getGroupCache().add(name, this);
        id = counter++;
        this.name = name + "#" + id;

        GroupPlayerJoinEvent event = new GroupPlayerJoinEvent(this, dPlayers.get(captain), true);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.captain = captain;
            this.players.add(captain);
        }

        for (Player player : players) {
            if (!this.players.contains(player)) {
                addMember(player);
            }
        }
        if (getMembers().size() == 0) {
            plugin.getGroupCache().remove(this);
            return;
        }

        this.dungeon = dungeon;
        playing = false;
    }

    // Getters and setters
    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return getDColor().getChatColor() + name;
    }

    @Override
    public String getRawName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public GroupSign getGroupSign() {
        return groupSign;
    }

    public void setGroupSign(GroupSign groupSign) {
        this.groupSign = groupSign;
    }

    @Override
    public Player getLeader() {
        return captain;
    }

    @Override
    public void setLeader(Player player) {
        captain = player;
    }

    @Override
    public PlayerCollection getMembers() {
        return players;
    }

    /**
     * @return the players as a Set&lt;DGlobalPlayer&gt;
     */
    public Set<DGlobalPlayer> getDGlobalPlayers() {
        Set<DGlobalPlayer> players = new HashSet<>();
        for (UUID uuid : this.players) {
            players.add((DGlobalPlayer) dPlayers.get(uuid));
        }
        return players;
    }

    public Set<DGamePlayer> getDGamePlayers() {
        Set<DGamePlayer> players = new HashSet<>();
        for (UUID uuid : this.players) {
            GlobalPlayer dPlayer = dPlayers.get(uuid);
            if (dPlayer instanceof DGamePlayer) {
                players.add((DGamePlayer) dPlayer);
            }
        }
        return players;
    }

    @Override
    public void addMember(Player player, boolean message) {
        GroupPlayerJoinEvent event = new GroupPlayerJoinEvent(this, dPlayers.get(player), false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        if (message) {
            sendMessage(DMessage.GROUP_PLAYER_JOINED.getMessage(player.getName()));
            MessageUtil.sendMessage(player, DMessage.PLAYER_JOIN_GROUP.getMessage());
        }
        players.add(player.getUniqueId());
        plugin.getGroupAdapters().forEach(a -> a.syncJoin(player));
    }

    @Override
    public void removeMember(Player player) {
        removeMember(player, true);
    }

    @Override
    public void removeMember(Player player, boolean message) {
        players.remove(player.getUniqueId());
        plugin.getGlobalProtectionCache().updateGroupSigns(this);

        if (message) {
            sendMessage(DMessage.PLAYER_LEFT_GROUP.getMessage(player.getName()));
        }

        if (isEmpty()) {
            GroupDisbandEvent event = new GroupDisbandEvent(this, dPlayers.get(player), GroupDisbandEvent.Cause.GROUP_IS_EMPTY);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                delete();
                return;
            }
        }

        plugin.getGroupAdapters().forEach(a -> a.removeExternalGroupMember(a.getExternalGroup(player), player));
    }

    @Override
    public PlayerCollection getInvitedPlayers() {
        return invitedPlayers;
    }

    @Override
    public void addInvitedPlayer(Player player, boolean silent) {
        if (player == null) {
            return;
        }

        if (plugin.getPlayerGroup(player) != null) {
            if (!silent) {
                MessageUtil.sendMessage(getLeader(), DMessage.ERROR_IN_GROUP.getMessage(player.getName()));
            }
            return;
        }

        if (!silent) {
            MessageUtil.sendMessage(player, DMessage.PLAYER_INVITED.getMessage(getLeader().getName(), name));
        }

        // Send message
        if (!silent) {
            sendMessage(DMessage.GROUP_INVITED_PLAYER.getMessage(getLeader().getName(), player.getName(), name));
        }

        // Add player
        invitedPlayers.add(player.getUniqueId());
    }

    @Override
    public void removeInvitedPlayer(Player player, boolean silent) {
        if (player == null) {
            return;
        }

        if (plugin.getPlayerGroup(player) != this) {
            if (!silent) {
                MessageUtil.sendMessage(getLeader(), DMessage.ERROR_NOT_IN_GROUP.getMessage(player.getName(), name));
            }
            return;
        }

        if (!silent) {
            MessageUtil.sendMessage(player, DMessage.PLAYER_UNINVITED.getMessage(player.getName(), name));
        }

        // Send message
        if (!silent) {
            for (Player groupPlayer : players.getOnlinePlayers()) {
                MessageUtil.sendMessage(groupPlayer, DMessage.GROUP_UNINVITED_PLAYER.getMessage(getLeader().getName(), player.getName(), name));
            }
        }

        invitedPlayers.remove(player.getUniqueId());
    }

    @Override
    public void clearOfflineInvitedPlayers() {
        ArrayList<UUID> toRemove = new ArrayList<>();
        for (UUID uuid : invitedPlayers.getUniqueIds()) {
            if (Bukkit.getPlayer(uuid) == null) {
                toRemove.add(uuid);
            }
        }
        invitedPlayers.removeAll(toRemove);
    }

    @Override
    public GameWorld getGameWorld() {
        return gameWorld;
    }

    @Override
    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public Game getGame() {
        if (game == null && gameWorld != null) {
            game = gameWorld.getGame();
        }
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public Dungeon getDungeon() {
        return game != null ? game.getDungeon() : dungeon;
    }

    /**
     * {@link #getDungeon()} ignores this if the group is in a game.
     *
     * @param dungeon dungeon to set
     */
    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    /**
     * Sets the dungeon.
     *
     * @param name the name of the dungeon
     * @return if the action was successful
     */
    public boolean setDungeon(String name) {
        return (dungeon = plugin.getDungeonRegistry().get(name)) != null;
    }

    public String getDungeonName() {
        if (getDungeon() == null) {
            return null;
        }
        return getDungeon().getName();
    }

    public String getMapName() {
        return gameWorld == null ? null : gameWorld.getName();
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    @Override
    public List<Reward> getRewards() {
        return rewards;
    }

    @Override
    public void addReward(Reward reward) {
        GroupCollectRewardEvent event = new GroupCollectRewardEvent(this, null, reward);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        rewards.add(reward);
    }

    @Override
    public void removeReward(Reward reward) {
        rewards.remove(reward);
    }

    public BukkitTask getTimeIsRunningTask() {
        return timeIsRunningTask;
    }

    public void setTimeIsRunningTask(BukkitTask task) {
        this.timeIsRunningTask = task;
    }

    public boolean isEmpty() {
        return players.size() == 0;
    }

    public boolean isCustom() {
        return !name.matches("Group#[0-9]{1,}");
    }

    /**
     * Returns the color that represents this group.
     *
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
     * Sets the color that represents this group.
     *
     * @param color the group color to set
     */
    public void setDColor(Color color) {
        this.color = color;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int getInitialLives() {
        return initialLives;
    }

    @Override
    public void setInitialLives(int initialLives) {
        this.initialLives = initialLives;
    }

    @Override
    public int getLives() {
        return lives;
    }

    @Override
    public void setLives(int lives) {
        this.lives = lives;
    }

    @Override
    public boolean isFinished() {
        for (DGamePlayer player : getDGamePlayers()) {
            if (!player.isFinished()) {
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

        GameWorld target = dungeon.getMap().instantiateGameWorld(false);
        Game game = getGame();

        if (target == null && game != null) {
            target = game.getWorld();
        }

        if (target == null) {
            if (game != null) {
                for (PlayerGroup otherTeam : game.getGroups()) {
                    if (otherTeam.getGameWorld() != null) {
                        target = otherTeam.getGameWorld();
                        break;
                    }
                }
            }
        }

        if (target == null && dungeon != null) {
            ResourceWorld resource = dungeon.getMap();
            if (resource != null) {
                target = resource.instantiateGameWorld(false);
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
            game = new DGame(plugin, this, target);

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
        GroupFinishDungeonEvent groupFinishDungeonEvent = new GroupFinishDungeonEvent(this, dungeon);
        Bukkit.getPluginManager().callEvent(groupFinishDungeonEvent);
        if (groupFinishDungeonEvent.isCancelled()) {
            return;
        }

        ((DGame) getGame()).resetWaveKills();
        getDGamePlayers().forEach(p -> p.leave(false));
    }

    // TODO: Move code to more appropriate classes
    /**
     * The group finishs the current floor.
     *
     * @param specifiedFloor the name of the next floor
     */
    public void finishFloor(DResourceWorld specifiedFloor) {
        Game game = getGame();
        DungeonConfig dConfig = ((DDungeon) dungeon).getConfig();
        int floorsLeft = getDungeon().getFloors().size() - game.getFloorCount(); //floorCount contains start floor, but dungeon floor list doesn't
        game.removeUnplayedFloor((DResourceWorld) gameWorld.getResource(), false);
        ResourceWorld newFloor = null;
        GameWorld.Type type = null;
        if (gameWorld.getType() == GameWorld.Type.END_FLOOR) {
            finish();
            return;
        } else if (specifiedFloor != null) {
            newFloor = specifiedFloor;
            type = GameWorld.Type.DEFAULT;
        } else if (floorsLeft > 0) {
            int random = new Random().nextInt(floorsLeft);
            newFloor = game.getUnplayedFloors().get(random);
            type = GameWorld.Type.DEFAULT;
        } else {
            newFloor = dConfig.getEndFloor();
            type = GameWorld.Type.END_FLOOR;
        }

        GroupFinishFloorEvent event = new GroupFinishFloorEvent(this, gameWorld, newFloor);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        GameWorld gameWorld = newFloor.instantiateGameWorld(true);
        gameWorld.setType(type);
        this.gameWorld = gameWorld;
        game.setWorld(gameWorld);

        for (DGamePlayer player : getDGamePlayers()) {
            player.setInstanceWorld(gameWorld);
            player.setLastCheckpoint(gameWorld.getStartLocation(this));
            if (player.getWolf() != null) {
                player.getWolf().teleport(player.getLastCheckpoint());
            }
            player.setFinished(false);
        }
        game.start();
    }

    @Override
    public void delete() {
        Game game = getGame();

        plugin.getGroupCache().remove(this);

        if (game != null) {
            game.removeGroup(this);
        }

        for (UUID uuid : players.getUniqueIds()) {
            GlobalPlayer member = dPlayers.get(uuid);
            if (member instanceof InstancePlayer) {
                ((InstancePlayer) member).leave();
            }
        }

        if (timeIsRunningTask != null) {
            timeIsRunningTask.cancel();
        }

        plugin.getGlobalProtectionCache().updateGroupSigns(this);

        plugin.getGroupAdapters().forEach(a -> a.deleteCorrespondingGroup(this));
    }

    public boolean startGame(Game game, int index) {
        if (color == null) {
            color = plugin.getMainConfig().getGroupColorPriority(index);
        }

        boolean ready = true;
        for (Player player : getMembers().getOnlinePlayers()) {
            GamePlayer gamePlayer = plugin.getPlayerCache().getGamePlayer(player);
            if (gamePlayer == null) {
                gamePlayer = new DGamePlayer(plugin, player, getGameWorld());
            }

            if (!gamePlayer.isReady()) {
                ready = false;
            }
        }

        if (!ready) {
            return false;
        }

        GroupStartFloorEvent event = new GroupStartFloorEvent(this, getGameWorld());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        playing = true;
        plugin.getGlobalProtectionCache().updateGroupSigns(this);

        GameRuleContainer rules = getDungeon().getRules();
        initialLives = rules.getState(GameRule.INITIAL_GROUP_LIVES);
        lives = initialLives;
        if (rules.getState(GameRule.TIME_TO_FINISH) != -1) {
            timeIsRunningTask = new TimeIsRunningTask(plugin, this, rules.getState(GameRule.TIME_TO_FINISH)).runTaskTimer(plugin, 20, 20);
        }

        for (UUID playerId : getMembers()) {
            GlobalPlayer player = plugin.getPlayerCache().get(playerId);
            if (!(player instanceof DGamePlayer)) {
                plugin.log("Player isn't a DGamePlayer");
                continue;
            }
            ((DGamePlayer) player).startGame();
        }

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

    // This is not used.
    public boolean checkTime() {
        if (DPermission.hasPermission(getLeader(), DPermission.IGNORE_TIME_LIMIT)) {
            return true;
        }

        for (DGamePlayer dPlayer : getDGamePlayers()) {
            if (!dPlayer.checkTimeAfterStart(dungeon) || !dPlayer.checkTimeAfterFinish(dungeon)) {
                return false;
            }
        }

        return true;
    }

    // This is not used.
    public boolean checkRequirements() {
        if (DPermission.hasPermission(getLeader(), DPermission.IGNORE_REQUIREMENTS)) {
            return true;
        }

        for (DGamePlayer dPlayer : getDGamePlayers()) {
            if (!dPlayer.checkRequirements(dungeon)) {
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name=" + name + "; captain=" + captain + "}";
    }

}
