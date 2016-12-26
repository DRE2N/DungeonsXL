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

import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.commons.util.playerutil.PlayerUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.config.DungeonConfig;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupFinishDungeonEvent;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupRewardEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerKickEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.instance.DInstancePlayerUpdateEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.instance.game.DGamePlayerDeathEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.instance.game.DGamePlayerFinishEvent;
import io.github.dre2n.dungeonsxl.event.requirement.RequirementCheckEvent;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameGoal;
import io.github.dre2n.dungeonsxl.game.GameRules;
import io.github.dre2n.dungeonsxl.game.GameType;
import io.github.dre2n.dungeonsxl.game.GameTypeDefault;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.requirement.Requirement;
import io.github.dre2n.dungeonsxl.reward.Reward;
import io.github.dre2n.dungeonsxl.task.CreateDInstancePlayerTask;
import io.github.dre2n.dungeonsxl.trigger.DistanceTrigger;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import io.github.dre2n.dungeonsxl.world.DResourceWorld;
import io.github.dre2n.dungeonsxl.world.block.TeamFlag;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a player in a DGameWorld.
 *
 * @author Frank Baumann, Tobias Schmitz, Milan Albrecht, Daniel Saukel
 */
public class DGamePlayer extends DInstancePlayer {

    // Variables
    private DGroup dGroup;

    private boolean ready = false;
    private boolean finished = false;

    private DClass dClass;
    private Location checkpoint;
    private Wolf wolf;
    private int wolfRespawnTime = 30;
    private long offlineTime;

    private int initialLives = -1;
    private int lives;

    private ItemStack oldHelmet;
    private DGroup stealing;

    public DGamePlayer(Player player, DGameWorld world) {
        super(player, world.getWorld());

        Game game = Game.getByGameWorld(world);
        if (game == null) {
            game = new Game(DGroup.getByPlayer(player));
        }

        GameRules rules = game.getRules();
        player.setGameMode(GameMode.SURVIVAL);

        if (!rules.getKeepInventoryOnEnter()) {
            clearPlayerData();
        }

        if (rules.isLobbyDisabled()) {
            ready();
        }

        initialLives = rules.getInitialLives();
        lives = initialLives;

        Location teleport = world.getLobbyLocation();
        if (teleport == null) {
            PlayerUtil.secureTeleport(player, world.getWorld().getSpawnLocation());
        } else {
            PlayerUtil.secureTeleport(player, teleport);
        }
    }

    /**
     * @param player
     * the represented Player
     * @param gameWorld
     * the player's GameWorld
     */
    public static void create(Player player, DGameWorld gameWorld) {
        create(player, gameWorld, null);
    }

    /**
     * @param player
     * the represented Player
     * @param gameWorld
     * the player's GameWorld
     * @param ready
     * Any GameType if the player will be ready from the beginning
     * null if the player will not be ready from the beginning
     */
    public static void create(Player player, DGameWorld gameWorld, GameType ready) {
        new CreateDInstancePlayerTask(player, gameWorld, ready).runTaskTimer(plugin, 0L, 5L);
    }

    /* Getters and setters */
    @Override
    public String getName() {
        String name = player.getName();
        if (getDGroup() != null && dGroup.getDColor() != null) {
            name = getDGroup().getDColor().getChatColor() + name;
        }
        return name;
    }

    /**
     * @return the DGroup of this player
     */
    public DGroup getDGroup() {
        if (dGroup == null) {
            dGroup = DGroup.getByPlayer(player);
        }
        return dGroup;
    }

    /**
     * @param player
     * the player to set
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * @return if the player is in test mode
     */
    public boolean isInTestMode() {
        if (getDGroup() == null) {
            return false;
        }

        DGameWorld gameWorld = dGroup.getGameWorld();
        if (gameWorld == null) {
            return false;
        }

        Game game = gameWorld.getGame();
        if (game == null) {
            return false;
        }

        GameType gameType = game.getType();
        if (gameType == GameTypeDefault.TEST) {
            return true;
        }

        return false;
    }

    /**
     * @return the isReady
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * @param ready
     * If the player is ready to play the dungeon
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * @param finished
     * the finished to set
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * @return the dClass
     */
    public DClass getDClass() {
        return dClass;
    }

    /**
     * @param dClass
     * the dClass to set
     */
    public void setDClass(String className) {
        Game game = Game.getByWorld(getPlayer().getWorld());
        if (game == null) {
            return;
        }

        DClass dClass = plugin.getDClasses().getByName(className);
        if (dClass != null) {
            if (this.dClass != dClass) {
                this.dClass = dClass;

                /* Set Dog */
                if (wolf != null) {
                    wolf.remove();
                    wolf = null;
                }

                if (dClass.hasDog()) {
                    wolf = (Wolf) getWorld().spawnEntity(getPlayer().getLocation(), EntityType.WOLF);
                    wolf.setTamed(true);
                    wolf.setOwner(getPlayer());

                    double maxHealth = ((Damageable) wolf).getMaxHealth();
                    wolf.setHealth(maxHealth);
                }

                /* Delete Inventory */
                getPlayer().getInventory().clear();
                getPlayer().getInventory().setArmorContents(null);
                getPlayer().getInventory().setItemInHand(new ItemStack(Material.AIR));

                // Remove Potion Effects
                for (PotionEffect effect : getPlayer().getActivePotionEffects()) {
                    getPlayer().removePotionEffect(effect.getType());
                }

                // Reset lvl
                getPlayer().setTotalExperience(0);
                getPlayer().setLevel(0);

                /* Set Inventory */
                for (ItemStack istack : dClass.getItems()) {

                    // Leggings
                    if (istack.getType() == Material.LEATHER_LEGGINGS || istack.getType() == Material.CHAINMAIL_LEGGINGS || istack.getType() == Material.IRON_LEGGINGS
                            || istack.getType() == Material.DIAMOND_LEGGINGS || istack.getType() == Material.GOLD_LEGGINGS) {
                        getPlayer().getInventory().setLeggings(istack);
                    } // Helmet
                    else if (istack.getType() == Material.LEATHER_HELMET || istack.getType() == Material.CHAINMAIL_HELMET || istack.getType() == Material.IRON_HELMET
                            || istack.getType() == Material.DIAMOND_HELMET || istack.getType() == Material.GOLD_HELMET) {
                        getPlayer().getInventory().setHelmet(istack);
                    } // Chestplate
                    else if (istack.getType() == Material.LEATHER_CHESTPLATE || istack.getType() == Material.CHAINMAIL_CHESTPLATE || istack.getType() == Material.IRON_CHESTPLATE
                            || istack.getType() == Material.DIAMOND_CHESTPLATE || istack.getType() == Material.GOLD_CHESTPLATE) {
                        getPlayer().getInventory().setChestplate(istack);
                    } // Boots
                    else if (istack.getType() == Material.LEATHER_BOOTS || istack.getType() == Material.CHAINMAIL_BOOTS || istack.getType() == Material.IRON_BOOTS
                            || istack.getType() == Material.DIAMOND_BOOTS || istack.getType() == Material.GOLD_BOOTS) {
                        getPlayer().getInventory().setBoots(istack);
                    } else {
                        getPlayer().getInventory().addItem(istack);
                    }
                }
            }
        }
    }

    /**
     * @return the checkpoint
     */
    public Location getCheckpoint() {
        return checkpoint;
    }

    /**
     * @param checkpoint
     * the checkpoint to set
     */
    public void setCheckpoint(Location checkpoint) {
        this.checkpoint = checkpoint;
    }

    /**
     * @return the wolf
     */
    public Wolf getWolf() {
        return wolf;
    }

    /**
     * @param wolf
     * the wolf to set
     */
    public void setWolf(Wolf wolf) {
        this.wolf = wolf;
    }

    /**
     * @return the wolfRespawnTime
     */
    public int getWolfRespawnTime() {
        return wolfRespawnTime;
    }

    /**
     * @param wolfRespawnTime
     * the wolfRespawnTime to set
     */
    public void setWolfRespawnTime(int wolfRespawnTime) {
        this.wolfRespawnTime = wolfRespawnTime;
    }

    /**
     * @return the offlineTime
     */
    public long getOfflineTime() {
        return offlineTime;
    }

    /**
     * @param offlineTime
     * the offlineTime to set
     */
    public void setOfflineTime(long offlineTime) {
        this.offlineTime = offlineTime;
    }

    /**
     * @return the initialLives
     */
    public int getInitialLives() {
        return initialLives;
    }

    /**
     * @param initialLives
     * the initialLives to set
     */
    public void setInitialLives(int initialLives) {
        this.initialLives = initialLives;
    }

    /**
     * @return the lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * @param lives
     * the lives to set
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * @return if the player is stealing a flag
     */
    public boolean isStealing() {
        return stealing != null;
    }

    /**
     * @return the group whose flag is stolen
     */
    public DGroup getRobbedGroup() {
        return stealing;
    }

    /**
     * @param dGroup
     * the group whose flag is stolen
     */
    public void setRobbedGroup(DGroup dGroup) {
        if (dGroup != null) {
            oldHelmet = player.getInventory().getHelmet();
            player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, getDGroup().getDColor().getWoolData()));
        }

        stealing = dGroup;
    }

    /* Actions */
    public void captureFlag() {
        if (stealing == null) {
            return;
        }

        Game game = Game.getByWorld(getWorld());
        if (game == null) {
            return;
        }

        game.sendMessage(DMessages.GROUP_FLAG_CAPTURED.getMessage(getName(), stealing.getName()));

        GameRules rules = game.getRules();

        getDGroup().setScore(getDGroup().getScore() + 1);
        if (rules.getScoreGoal() == dGroup.getScore()) {
            dGroup.winGame();
        }

        stealing.setScore(stealing.getScore() - 1);
        if (stealing.getScore() == -1) {
            for (DGamePlayer member : stealing.getDGamePlayers()) {
                member.kill();
            }
            game.sendMessage(DMessages.GROUP_DEFEATED.getMessage(stealing.getName()));
        }

        stealing = null;
        player.getInventory().setHelmet(oldHelmet);

        if (game.getDGroups().size() == 1) {
            dGroup.winGame();
        }
    }

    @Override
    public void leave() {
        leave(true);
    }

    /**
     * @param message
     * if messages should be sent
     */
    public void leave(boolean message) {
        Game game = Game.getByWorld(getWorld());
        if (game == null) {
            return;
        }
        DGameWorld gameWorld = game.getWorld();
        if (gameWorld == null) {
            return;
        }
        GameRules rules = game.getRules();
        delete();

        if (finished) {
            reset(rules.getKeepInventoryOnFinish());
        } else {
            reset(rules.getKeepInventoryOnEscape());
        }

        // Permission bridge
        if (plugin.getPermissionProvider() != null) {
            for (String permission : rules.getGamePermissions()) {
                plugin.getPermissionProvider().playerRemoveTransient(getWorld().getName(), player, permission);
            }
        }

        if (getDGroup() != null) {
            dGroup.removePlayer(getPlayer(), message);
        }

        if (game != null) {
            if (finished) {
                if (game.getType() == GameTypeDefault.CUSTOM || game.getType().hasRewards()) {
                    for (Reward reward : rules.getRewards()) {
                        reward.giveTo(getPlayer());
                    }

                    getData().logTimeLastPlayed(getDGroup().getDungeon().getName());

                    // Tutorial Permissions
                    if (game.isTutorial() && plugin.getPermissionProvider().hasGroupSupport()) {
                        String endGroup = plugin.getMainConfig().getTutorialEndGroup();
                        if (plugin.isGroupEnabled(endGroup)) {
                            plugin.getPermissionProvider().playerAddGroup(getPlayer(), endGroup);
                        }

                        String startGroup = plugin.getMainConfig().getTutorialStartGroup();
                        if (plugin.isGroupEnabled(startGroup)) {
                            plugin.getPermissionProvider().playerRemoveGroup(getPlayer(), startGroup);
                        }
                    }
                }
            }
        }

        if (getDGroup() != null) {
            if (!dGroup.isEmpty()) {
                /*if (dGroup.finishIfMembersFinished()) {
                    return;
                }*/

                // Give secure objects to other players
                int i = 0;
                Player groupPlayer;
                do {
                    groupPlayer = dGroup.getPlayers().get(i);
                    if (groupPlayer != null) {
                        for (ItemStack itemStack : getPlayer().getInventory()) {
                            if (itemStack != null) {
                                if (gameWorld.getSecureObjects().contains(itemStack)) {
                                    groupPlayer.getInventory().addItem(itemStack);
                                }
                            }
                        }
                    }
                    i++;
                } while (groupPlayer == null);
            }

            if (dGroup.getCaptain().equals(getPlayer()) && dGroup.getPlayers().size() > 0) {
                // Captain here!
                Player newCaptain = dGroup.getPlayers().get(0);
                dGroup.setCaptain(newCaptain);
                if (message) {
                    MessageUtil.sendMessage(newCaptain, DMessages.PLAYER_NEW_CAPTAIN.getMessage());
                }
                // ...*flies away*
            }
        }
    }

    public void kill() {
        DPlayerKickEvent dPlayerKickEvent = new DPlayerKickEvent(this, DPlayerKickEvent.Cause.DEATH);
        plugin.getServer().getPluginManager().callEvent(dPlayerKickEvent);

        if (!dPlayerKickEvent.isCancelled()) {
            DGameWorld gameWorld = getDGroup().getGameWorld();
            if (lives != -1) {
                gameWorld.sendMessage(DMessages.PLAYER_DEATH_KICK.getMessage(getName()));
            } else if (getDGroup().getLives() != -1) {
                gameWorld.sendMessage(DMessages.GROUP_DEATH_KICK.getMessage(getName(), dGroup.getName()));
            }

            GameRules rules = Game.getByPlayer(player).getRules();
            leave();
            if (rules.getKeepInventoryOnEscape() && rules.getKeepInventoryOnDeath()) {
                applyRespawnInventory();
            }
        }
    }

    public boolean checkRequirements(Game game) {
        if (DPermissions.hasPermission(player, DPermissions.IGNORE_REQUIREMENTS)) {
            return true;
        }

        GameRules rules = game.getRules();

        if (!checkTime(game)) {
            MessageUtil.sendMessage(player, DMessages.ERROR_COOLDOWN.getMessage(String.valueOf(rules.getTimeToNextPlay())));
            return false;
        }

        for (Requirement requirement : rules.getRequirements()) {
            RequirementCheckEvent event = new RequirementCheckEvent(requirement, player);
            plugin.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                continue;
            }

            if (!requirement.check(player)) {
                return false;
            }
        }

        if (rules.getFinished() != null && rules.getFinishedAll() != null) {
            if (!rules.getFinished().isEmpty()) {

                long bestTime = 0;
                int numOfNeeded = 0;
                boolean doneTheOne = false;

                if (rules.getFinished().size() == rules.getFinishedAll().size()) {
                    doneTheOne = true;
                }

                for (String played : rules.getFinished()) {
                    for (String dungeonName : DungeonsXL.MAPS.list()) {
                        if (new File(DungeonsXL.MAPS, dungeonName).isDirectory()) {
                            if (played.equalsIgnoreCase(dungeonName) || played.equalsIgnoreCase("any")) {

                                Long time = getData().getTimeLastPlayed(dungeonName);
                                if (time != -1) {
                                    if (rules.getFinishedAll().contains(played)) {
                                        numOfNeeded++;
                                    } else {
                                        doneTheOne = true;
                                    }
                                    if (bestTime < time) {
                                        bestTime = time;
                                    }
                                }
                                break;

                            }
                        }
                    }
                }

                if (bestTime == 0) {
                    return false;

                } else if (rules.getTimeLastPlayed() != 0) {
                    if (System.currentTimeMillis() - bestTime > rules.getTimeLastPlayed() * (long) 3600000) {
                        return false;
                    }
                }

                if (numOfNeeded < rules.getFinishedAll().size() || !doneTheOne) {
                    return false;
                }

            }
        }

        return true;
    }

    public boolean checkTime(Game game) {
        if (DPermissions.hasPermission(player, DPermissions.IGNORE_TIME_LIMIT)) {
            return true;
        }

        GameRules rules = game.getRules();

        if (rules.getTimeToNextPlay() != 0) {
            // read PlayerConfig
            long time = getData().getTimeLastPlayed(game.getDungeon().getName());
            if (time != -1) {
                if (time + rules.getTimeToNextPlay() * 1000 * 60 * 60 > System.currentTimeMillis()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void ready() {
        ready(GameTypeDefault.DEFAULT);
    }

    public void ready(GameType gameType) {
        if (getDGroup() == null) {
            return;
        }

        Game game = Game.getByGameWorld(dGroup.getGameWorld());
        if (game == null) {
            game = new Game(dGroup, gameType, dGroup.getGameWorld());

        } else {
            game.setType(gameType);
        }
        game.fetchRules();

        if (!checkRequirements(game)) {
            MessageUtil.sendMessage(player, DMessages.ERROR_REQUIREMENTS.getMessage());
            return;
        }

        ready = true;

        for (DGroup gameGroup : game.getDGroups()) {
            if (!gameGroup.isPlaying()) {
                gameGroup.startGame(game);

            } else {
                respawn();
            }
        }

        game.setStarted(true);
    }

    public void respawn() {
        Location respawn = checkpoint;

        if (respawn == null) {
            respawn = getDGroup().getGameWorld().getStartLocation(dGroup);
        }

        if (respawn == null) {
            respawn = getWorld().getSpawnLocation();
        }

        PlayerUtil.secureTeleport(getPlayer(), respawn);

        // Don't forget Doge!
        if (wolf != null) {
            wolf.teleport(getPlayer());
        }

        // Respawn Items
        Game game = Game.getByWorld(getWorld());

        if (game != null && game.getRules().getKeepInventoryOnDeath()) {
            applyRespawnInventory();
        }
    }

    /**
     * The DGamePlayer finishs the current floor.
     *
     * @param specifiedFloor
     * the name of the next floor
     */
    public void finishFloor(DResourceWorld specifiedFloor) {
        MessageUtil.sendMessage(getPlayer(), DMessages.PLAYER_FINISHED_DUNGEON.getMessage());
        finished = true;

        if (getDGroup() == null) {
            return;
        }

        if (!dGroup.isPlaying()) {
            return;
        }

        dGroup.setNextFloor(specifiedFloor);

        for (Player player : dGroup.getPlayers()) {
            DGamePlayer dPlayer = getByPlayer(player);
            if (!dPlayer.finished) {
                MessageUtil.sendMessage(this.getPlayer(), DMessages.PLAYER_WAIT_FOR_OTHER_PLAYERS.getMessage());
                return;
            }
        }

        boolean invalid = !dGroup.getDungeon().isMultiFloor();

        for (Player player : dGroup.getPlayers()) {
            DGamePlayer dPlayer = getByPlayer(player);

            if (invalid) {
                dPlayer.finish(false);

            } else {
                dPlayer.finished = false;
            }
        }

        if (invalid) {
            return;
        }

        DungeonConfig dConfig = dGroup.getDungeon().getConfig();
        int random = NumberUtil.generateRandomInt(0, dConfig.getFloors().size());
        DResourceWorld newFloor = dGroup.getUnplayedFloors().get(random);
        if (dConfig.getFloorCount() == dGroup.getFloorCount() - 1) {
            newFloor = dConfig.getEndFloor();

        } else if (specifiedFloor != null) {
            newFloor = specifiedFloor;
        }

        /*DGroupFinishFloorEvent event = new DGroupFinishFloorEvent(dGroup, dGroup.getGameWorld(), newFloor);

        if (event.isCancelled()) {
            return;
        }
         */
        Game game = dGroup.getGameWorld().getGame();

        dGroup.removeUnplayedFloor(dGroup.getGameWorld().getResource(), false);

        DGameWorld gameWorld = null;
        if (newFloor != null) {
            gameWorld = newFloor.instantiateAsGameWorld();
        }
        dGroup.setGameWorld(gameWorld);

        for (Player player : dGroup.getPlayers()) {
            DGamePlayer dPlayer = getByPlayer(player);
            dPlayer.setWorld(gameWorld.getWorld());
            dPlayer.setCheckpoint(dGroup.getGameWorld().getStartLocation(dGroup));
            if (dPlayer.getWolf() != null) {
                dPlayer.getWolf().teleport(dPlayer.getCheckpoint());
            }
        }
        dGroup.startGame(game);
    }

    /**
     * The DGamePlayer finishs the current game.
     */
    public void finish() {
        finish(true);
    }

    /**
     * @param message
     * if messages should be sent
     */
    public void finish(boolean message) {
        if (message) {
            MessageUtil.sendMessage(getPlayer(), DMessages.PLAYER_FINISHED_DUNGEON.getMessage());
        }
        finished = true;

        if (getDGroup() == null) {
            return;
        }

        if (!dGroup.isPlaying()) {
            return;
        }

        boolean first = true;
        boolean hasToWait = false;

        for (Player player : dGroup.getPlayers()) {
            DGamePlayer dPlayer = getByPlayer(player);
            if (!dPlayer.finished) {
                if (message) {
                    MessageUtil.sendMessage(this.getPlayer(), DMessages.PLAYER_WAIT_FOR_OTHER_PLAYERS.getMessage());
                }
                hasToWait = true;

            } else if (dPlayer != this) {
                first = false;
            }
        }

        DGamePlayerFinishEvent dPlayerFinishEvent = new DGamePlayerFinishEvent(this, first, hasToWait);

        if (dPlayerFinishEvent.isCancelled()) {
            finished = false;
            return;
        }

        if (hasToWait) {
            return;
        }

        DGroupFinishDungeonEvent dGroupFinishDungeonEvent = new DGroupFinishDungeonEvent(dGroup);

        if (dGroupFinishDungeonEvent.isCancelled()) {
            return;
        }

        Game.getByDGroup(dGroup).resetWaveKills();

        DGroupRewardEvent dGroupRewardEvent = new DGroupRewardEvent(dGroup);
        plugin.getServer().getPluginManager().callEvent(dGroupRewardEvent);
        for (Player player : dGroup.getPlayers()) {
            DGamePlayer dPlayer = getByPlayer(player);
            dPlayer.leave(false);

            if (!dGroupRewardEvent.isCancelled()) {
                for (Reward reward : dGroup.getRewards()) {
                    reward.giveTo(player);
                }
            }
        }
    }

    @Override
    public void sendMessage(String message) {
        DGameWorld gameWorld = DGameWorld.getByWorld(getWorld());
        gameWorld.sendMessage(message);

        for (DGlobalPlayer player : plugin.getDPlayers().getDGlobalPlayers()) {
            if (player.isInChatSpyMode()) {
                if (!gameWorld.getWorld().getPlayers().contains(player.getPlayer())) {
                    MessageUtil.sendMessage(player.getPlayer(), ChatColor.GREEN + "[Chatspy] " + ChatColor.WHITE + message);
                }
            }
        }
    }

    public void onDeath(PlayerDeathEvent event) {
        DGameWorld gameWorld = DGameWorld.getByWorld(player.getLocation().getWorld());
        if (gameWorld == null) {
            return;
        }

        Game game = Game.getByGameWorld(gameWorld);
        if (game == null) {
            return;
        }

        DGamePlayerDeathEvent dPlayerDeathEvent = new DGamePlayerDeathEvent(this, event, 1);
        plugin.getServer().getPluginManager().callEvent(dPlayerDeathEvent);

        if (dPlayerDeathEvent.isCancelled()) {
            return;
        }

        if (lives != -1) {
            lives = lives - dPlayerDeathEvent.getLostLives();

            DGamePlayer killer = DGamePlayer.getByPlayer(player.getKiller());
            if (killer != null) {
                gameWorld.sendMessage(DMessages.PLAYER_KILLED.getMessage(getName(), killer.getName(), String.valueOf(lives)));
            } else {
                gameWorld.sendMessage(DMessages.PLAYER_DEATH.getMessage(getName(), String.valueOf(lives)));
            }

            if (game.getRules().getKeepInventoryOnDeath()) {
                setRespawnInventory(event.getEntity().getInventory().getContents());
                setRespawnArmor(event.getEntity().getInventory().getArmorContents());
                // Delete all drops
                for (ItemStack item : event.getDrops()) {
                    item.setType(Material.AIR);
                }
            }

        } else if (getDGroup() != null && dGroup.getLives() != -1) {
            dGroup.setLives(dGroup.getLives() - 1);
            MessageUtil.broadcastMessage(DMessages.GROUP_DEATH.getMessage(player.getName(), String.valueOf(lives)));
        }

        if (isStealing()) {
            for (TeamFlag teamFlag : gameWorld.getTeamFlags()) {
                if (teamFlag.getOwner().equals(stealing)) {
                    teamFlag.reset();
                    gameWorld.sendMessage(DMessages.GROUP_FLAG_LOST.getMessage(player.getName(), stealing.getName()));
                    stealing = null;
                }
            }
        }

        if (lives == 0 && ready) {
            kill();
        }

        GameType gameType = game.getType();
        if (gameType != null && gameType != GameTypeDefault.CUSTOM) {
            if (gameType.getGameGoal() == GameGoal.LAST_MAN_STANDING) {
                if (game.getDGroups().size() == 1) {
                    game.getDGroups().get(0).winGame();
                }
            }
        }
    }

    @Override
    public void update(boolean updateSecond) {
        boolean locationValid = true;
        Location teleportLocation = player.getLocation();
        boolean teleportWolf = false;
        boolean respawnInventory = false;
        boolean offline = false;
        boolean kick = false;
        boolean triggerAllInDistance = false;

        DGameWorld gameWorld = DGameWorld.getByWorld(getWorld());

        if (!updateSecond) {
            if (!getPlayer().getWorld().equals(getWorld())) {
                locationValid = false;

                if (gameWorld != null) {
                    teleportLocation = getCheckpoint();

                    if (teleportLocation == null) {
                        teleportLocation = getDGroup().getGameWorld().getStartLocation(getDGroup());
                    }

                    // Don't forget Doge!
                    if (getWolf() != null) {
                        teleportWolf = true;
                    }

                    // Respawn Items
                    if (getRespawnInventory() != null || getRespawnArmor() != null) {
                        respawnInventory = true;
                    }
                }
            }

        } else if (gameWorld != null) {
            // Update Wolf
            if (getWolf() != null) {
                if (getWolf().isDead()) {
                    if (getWolfRespawnTime() <= 0) {
                        setWolf((Wolf) getWorld().spawnEntity(getPlayer().getLocation(), EntityType.WOLF));
                        getWolf().setTamed(true);
                        getWolf().setOwner(getPlayer());
                        setWolfRespawnTime(30);
                    }
                    wolfRespawnTime--;
                }

                DMob dMob = DMob.getByEntity(getWolf());
                if (dMob != null) {
                    gameWorld.removeDMob(dMob);
                }
            }

            // Kick offline players
            if (getOfflineTime() > 0) {
                offline = true;

                if (getOfflineTime() < System.currentTimeMillis()) {
                    kick = true;
                }
            }

            triggerAllInDistance = true;
        }

        DInstancePlayerUpdateEvent event = new DInstancePlayerUpdateEvent(this, locationValid, teleportWolf, respawnInventory, offline, kick, triggerAllInDistance);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (!locationValid) {
            PlayerUtil.secureTeleport(getPlayer(), teleportLocation);
        }

        if (teleportWolf) {
            getWolf().teleport(teleportLocation);
        }

        if (respawnInventory) {
            applyRespawnInventory();
        }

        if (kick) {
            DPlayerKickEvent dPlayerKickEvent = new DPlayerKickEvent(this, DPlayerKickEvent.Cause.OFFLINE);

            if (!dPlayerKickEvent.isCancelled()) {
                leave();
            }
        }

        if (triggerAllInDistance) {
            DistanceTrigger.triggerAllInDistance(getPlayer(), gameWorld);
        }
    }

    /* Statics */
    public static DGamePlayer getByPlayer(Player player) {
        for (DGamePlayer dPlayer : plugin.getDPlayers().getDGamePlayers()) {
            if (dPlayer.getPlayer().equals(player)) {
                return dPlayer;
            }
        }
        return null;
    }

    public static DGamePlayer getByName(String name) {
        for (DGamePlayer dPlayer : plugin.getDPlayers().getDGamePlayers()) {
            if (dPlayer.getPlayer().getName().equalsIgnoreCase(name) || dPlayer.getName().equalsIgnoreCase(name)) {
                return dPlayer;
            }
        }

        return null;
    }

    public static List<DGamePlayer> getByWorld(World world) {
        List<DGamePlayer> dPlayers = new ArrayList<>();

        for (DGamePlayer dPlayer : plugin.getDPlayers().getDGamePlayers()) {
            if (dPlayer.getWorld() == world) {
                dPlayers.add(dPlayer);
            }
        }

        return dPlayers;
    }

}
