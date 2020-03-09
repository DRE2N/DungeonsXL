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

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.player.PlayerUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.dungeon.GameGoal;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
import de.erethon.dungeonsxl.api.mob.DungeonMob;
import de.erethon.dungeonsxl.api.player.GamePlayer;
import de.erethon.dungeonsxl.api.player.PlayerClass;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.DGame;
import de.erethon.dungeonsxl.event.dplayer.DPlayerKickEvent;
import de.erethon.dungeonsxl.event.dplayer.instance.DInstancePlayerUpdateEvent;
import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerDeathEvent;
import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerFinishEvent;
import de.erethon.dungeonsxl.event.dplayer.instance.game.DGamePlayerRewardEvent;
import de.erethon.dungeonsxl.event.requirement.RequirementCheckEvent;
import de.erethon.dungeonsxl.trigger.DistanceTrigger;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.DResourceWorld;
import de.erethon.dungeonsxl.world.block.TeamFlag;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * @author Frank Baumann, Tobias Schmitz, Milan Albrecht, Daniel Saukel
 */
public class DGamePlayer extends DInstancePlayer implements GamePlayer {

    private DGroup dGroup;

    private boolean ready = false;
    private boolean finished = false;

    private PlayerClass dClass;
    private Location checkpoint;
    private Wolf wolf;
    private int wolfRespawnTime = 30;
    private long offlineTime;

    private int initialLives = -1;
    private int lives;

    private ItemStack oldHelmet;
    private PlayerGroup stealing;

    private DGroupTag groupTag;

    public DGamePlayer(DungeonsXL plugin, Player player, GameWorld world) {
        super(plugin, player, world);

        Game game = world.getGame();
        dGroup = (DGroup) plugin.getPlayerGroup(player);
        if (game == null) {
            game = new DGame(plugin, dGroup);
        }

        GameRuleContainer rules = game.getRules();
        player.setGameMode(GameMode.SURVIVAL);

        if (!rules.getState(GameRule.KEEP_INVENTORY_ON_ENTER)) {
            clearPlayerData();
        }
        player.setAllowFlight(rules.getState(GameRule.FLY));

        if (rules.getState(GameRule.IS_LOBBY_DISABLED)) {
            ready();
        }

        initialLives = rules.getState(GameRule.INITIAL_LIVES);
        lives = initialLives;

        Location teleport = world.getLobbyLocation();
        if (teleport == null) {
            PlayerUtil.secureTeleport(player, world.getWorld().getSpawnLocation());
        } else {
            PlayerUtil.secureTeleport(player, teleport);
        }
    }

    /* Getters and setters */
    @Override
    public String getName() {
        String name = player.getName();
        if (getGroup() != null && dGroup.getDColor() != null) {
            name = getGroup().getDColor().getChatColor() + name;
        }
        return name;
    }

    @Override
    public DGroup getGroup() {
        return dGroup;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * @return if the player is in test mode
     */
    public boolean isInTestMode() {
        if (getGroup() == null) {
            return false;
        }

        if (getGame() == null) {
            return false;
        }

        return getGame().hasRewards();
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public PlayerClass getPlayerClass() {
        return dClass;
    }

    @Override
    public void setPlayerClass(PlayerClass playerClass) {
        dClass = playerClass;

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
        getPlayer().getInventory().setItemInHand(VanillaItem.AIR.toItemStack());

        // Remove Potion Effects
        for (PotionEffect effect : getPlayer().getActivePotionEffects()) {
            getPlayer().removePotionEffect(effect.getType());
        }

        // Reset lvl
        getPlayer().setExp(0f);
        getPlayer().setLevel(0);

        /* Set Inventory */
        for (ItemStack istack : dClass.getItems()) {

            // Leggings
            if (VanillaItem.LEATHER_LEGGINGS.is(istack) || VanillaItem.CHAINMAIL_LEGGINGS.is(istack) || VanillaItem.IRON_LEGGINGS.is(istack)
                    || VanillaItem.DIAMOND_LEGGINGS.is(istack) || VanillaItem.GOLDEN_LEGGINGS.is(istack)) {
                getPlayer().getInventory().setLeggings(istack);
            } // Helmet
            else if (VanillaItem.LEATHER_HELMET.is(istack) || VanillaItem.CHAINMAIL_HELMET.is(istack) || VanillaItem.IRON_HELMET.is(istack)
                    || VanillaItem.DIAMOND_HELMET.is(istack) || VanillaItem.GOLDEN_HELMET.is(istack)) {
                getPlayer().getInventory().setHelmet(istack);
            } // Chestplate
            else if (VanillaItem.LEATHER_CHESTPLATE.is(istack) || VanillaItem.CHAINMAIL_CHESTPLATE.is(istack) || VanillaItem.IRON_CHESTPLATE.is(istack)
                    || VanillaItem.DIAMOND_CHESTPLATE.is(istack) || VanillaItem.GOLDEN_CHESTPLATE.is(istack)) {
                getPlayer().getInventory().setChestplate(istack);
            } // Boots
            else if (VanillaItem.LEATHER_BOOTS.is(istack) || VanillaItem.CHAINMAIL_BOOTS.is(istack) || VanillaItem.IRON_BOOTS.is(istack)
                    || VanillaItem.DIAMOND_BOOTS.is(istack) || VanillaItem.GOLDEN_BOOTS.is(istack)) {
                getPlayer().getInventory().setBoots(istack);
            } else {
                getPlayer().getInventory().addItem(istack);
            }
        }
    }

    @Override
    public Location getLastCheckpoint() {
        return checkpoint;
    }

    @Override
    public void setLastCheckpoint(Location checkpoint) {
        this.checkpoint = checkpoint;
    }

    @Override
    public long getOfflineTimeMillis() {
        return offlineTime;
    }

    @Override
    public void setOfflineTimeMillis(long offlineTime) {
        this.offlineTime = offlineTime;
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
    public Wolf getWolf() {
        return wolf;
    }

    @Override
    public void setWolf(Wolf wolf) {
        this.wolf = wolf;
    }

    public int getWolfRespawnTime() {
        return wolfRespawnTime;
    }

    public void setWolfRespawnTime(int wolfRespawnTime) {
        this.wolfRespawnTime = wolfRespawnTime;
    }

    @Override
    public boolean isStealingFlag() {
        return stealing != null;
    }

    @Override
    public PlayerGroup getRobbedGroup() {
        return stealing;
    }

    @Override
    public void setRobbedGroup(PlayerGroup group) {
        if (group != null) {
            oldHelmet = player.getInventory().getHelmet();
            player.getInventory().setHelmet(getGroup().getDColor().getWoolMaterial().toItemStack());
        }

        stealing = group;
    }

    public DGroupTag getDGroupTag() {
        return groupTag;
    }

    public void initDGroupTag() {
        groupTag = new DGroupTag(plugin, this);
    }

    /* Actions */
    @Override
    public void captureFlag() {
        if (stealing == null) {
            return;
        }

        Game game = plugin.getGame(getWorld());
        if (game == null) {
            return;
        }

        game.sendMessage(DMessage.GROUP_FLAG_CAPTURED.getMessage(getName(), stealing.getName()));

        GameRuleContainer rules = game.getRules();

        getGroup().setScore(getGroup().getScore() + 1);
        if (rules.getState(GameRule.SCORE_GOAL) == getGroup().getScore()) {
            getGroup().winGame();
        }

        stealing.setScore(stealing.getScore() - 1);
        if (stealing.getScore() == -1) {
            for (GamePlayer member : ((DGroup) stealing).getDGamePlayers()) {
                member.kill();
            }
            game.sendMessage(DMessage.GROUP_DEFEATED.getMessage(stealing.getName()));
        }

        stealing = null;
        player.getInventory().setHelmet(oldHelmet);

        if (game.getGroups().size() == 1) {
            dGroup.winGame();
        }
    }

    @Override
    public void leave() {
        leave(true);
    }

    @Override
    public void leave(boolean message) {
        Game game = plugin.getGame(getWorld());
        if (game == null) {
            return;
        }
        DGameWorld gameWorld = (DGameWorld) game.getWorld();
        if (gameWorld == null) {
            return;
        }
        GameRuleContainer rules = game.getRules();
        delete();

        if (player.isOnline()) {
            if (finished) {
                reset(rules.getState(GameRule.KEEP_INVENTORY_ON_FINISH));
            } else {
                reset(rules.getState(GameRule.KEEP_INVENTORY_ON_ESCAPE));
            }
        }

        // Permission bridge
        if (plugin.getPermissionProvider() != null) {
            for (String permission : rules.getState(GameRule.GAME_PERMISSIONS)) {
                plugin.getPermissionProvider().playerRemoveTransient(getWorld().getName(), player, permission);
            }
        }

        if (getGroup() != null) {
            dGroup.removePlayer(getPlayer(), message);
        }

        if (game != null && finished && game.hasRewards()) {
            DGamePlayerRewardEvent dGroupRewardEvent = new DGamePlayerRewardEvent(this);
            Bukkit.getPluginManager().callEvent(dGroupRewardEvent);
            if (!dGroupRewardEvent.isCancelled()) {
                giveLoot(rules, rules.getState(GameRule.REWARDS), dGroup.getRewards());
            }

            getData().logTimeLastFinished(getGroup().getDungeonName());

            // Tutorial Permissions
            if (game.isTutorial()) {
                getData().setFinishedTutorial(true);
                if (plugin.getPermissionProvider() != null && plugin.getPermissionProvider().hasGroupSupport()) {
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

        if (getGroup() != null) {
            if (!dGroup.isEmpty()) {
                /*if (dGroup.finishIfMembersFinished()) {
                    return;
                }*/

                // Give secure objects to other players
                Player groupPlayer = null;
                for (Player player : dGroup.getMembers().getOnlinePlayers()) {
                    if (player.isOnline()) {
                        groupPlayer = player;
                        break;
                    }
                }
                if (groupPlayer != null) {
                    for (ItemStack itemStack : getPlayer().getInventory()) {
                        if (itemStack != null) {
                            if (gameWorld.getSecureObjects().contains(itemStack)) {
                                groupPlayer.getInventory().addItem(itemStack);
                            }
                        }
                    }
                }
            }

            if (dGroup.getLeader().equals(getPlayer()) && dGroup.getMembers().size() > 0) {
                // Captain here!
                Player newCaptain = null;
                for (Player player : dGroup.getMembers().getOnlinePlayers()) {
                    if (player.isOnline()) {
                        newCaptain = player;
                        break;
                    }
                }
                dGroup.setLeader(newCaptain);
                if (message) {
                    MessageUtil.sendMessage(newCaptain, DMessage.PLAYER_NEW_LEADER.getMessage());
                }
                // ...*flies away*
            }
        }
    }

    @Override
    public void kill() {
        DPlayerKickEvent dPlayerKickEvent = new DPlayerKickEvent(this, DPlayerKickEvent.Cause.DEATH);
        Bukkit.getPluginManager().callEvent(dPlayerKickEvent);

        if (!dPlayerKickEvent.isCancelled()) {
            GameWorld gameWorld = getGroup().getGameWorld();
            if (lives != -1) {
                gameWorld.sendMessage(DMessage.PLAYER_DEATH_KICK.getMessage(getName()));
            } else if (getGroup().getLives() != -1) {
                gameWorld.sendMessage(DMessage.GROUP_DEATH_KICK.getMessage(getName(), dGroup.getName()));
            }

            leave();
        }
    }

    public boolean checkRequirements(Dungeon dungeon) {
        if (DPermission.hasPermission(player, DPermission.IGNORE_REQUIREMENTS)) {
            return true;
        }

        GameRuleContainer rules = dungeon.getRules();

        if (!checkTimeAfterStart(dungeon) && !checkTimeAfterFinish(dungeon)) {
            int longestTime = rules.getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_START) >= rules.getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_FINISH)
                    ? rules.getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_START) : rules.getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_FINISH);
            MessageUtil.sendMessage(player, DMessage.ERROR_COOLDOWN.getMessage(String.valueOf(longestTime)));
            return false;

        } else if (!checkTimeAfterStart(dungeon)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_COOLDOWN.getMessage(String.valueOf(rules.getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_START))));
            return false;

        } else if (!checkTimeAfterFinish(dungeon)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_COOLDOWN.getMessage(String.valueOf(rules.getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_FINISH))));
            return false;
        }

        for (Requirement requirement : rules.getState(GameRule.REQUIREMENTS)) {
            RequirementCheckEvent event = new RequirementCheckEvent(requirement, player);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                continue;
            }

            if (!requirement.check(player)) {
                return false;
            }
        }

        if (rules.getState(GameRule.MUST_FINISH_ALL) != null) {
            List<String> finished = new ArrayList<>(rules.getState(GameRule.MUST_FINISH_ALL));
            if (rules.getState(GameRule.MUST_FINISH_ONE) != null) {
                finished.addAll(rules.getState(GameRule.MUST_FINISH_ONE));
            }

            if (!finished.isEmpty()) {

                long bestTime = 0;
                int numOfNeeded = 0;
                boolean doneTheOne = false;

                if (finished.size() == rules.getState(GameRule.MUST_FINISH_ALL).size()) {
                    doneTheOne = true;
                }

                for (String played : finished) {
                    for (String dungeonName : DungeonsXL.MAPS.list()) {
                        if (new File(DungeonsXL.MAPS, dungeonName).isDirectory()) {
                            if (played.equalsIgnoreCase(dungeonName) || played.equalsIgnoreCase("any")) {

                                Long time = getData().getTimeLastFinished(dungeonName);
                                if (time != -1) {
                                    if (rules.getState(GameRule.MUST_FINISH_ALL).contains(played)) {
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

                } else if (rules.getState(GameRule.TIME_LAST_PLAYED_REQUIRED_DUNGEONS) != 0) {
                    if (System.currentTimeMillis() - bestTime > rules.getState(GameRule.TIME_LAST_PLAYED_REQUIRED_DUNGEONS) * (long) 3600000) {
                        return false;
                    }
                }

                if (numOfNeeded < rules.getState(GameRule.MUST_FINISH_ALL).size() || !doneTheOne) {
                    return false;
                }

            }
        }

        return true;
    }

    public boolean checkTimeAfterStart(Dungeon dungeon) {
        return checkTime(dungeon, dungeon.getRules().getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_START),
                getData().getTimeLastStarted(dungeon.getName()));
    }

    public boolean checkTimeAfterFinish(Dungeon dungeon) {
        return checkTime(dungeon, dungeon.getRules().getState(GameRule.TIME_TO_NEXT_PLAY_AFTER_FINISH),
                getData().getTimeLastFinished(dungeon.getName()));
    }

    public boolean checkTime(Dungeon dungeon, int requirement, long dataTime) {
        if (DPermission.hasPermission(player, DPermission.IGNORE_TIME_LIMIT)) {
            return true;
        }

        return dataTime == -1 || dataTime + requirement * 1000 * 60 * 60 <= System.currentTimeMillis();
    }

    public void giveLoot(GameRuleContainer rules, List<Reward> ruleRewards, List<Reward> groupRewards) {
        if (!canLoot(rules)) {
            return;
        }
        ruleRewards.forEach(r -> r.giveTo(player.getPlayer()));
        groupRewards.forEach(r -> r.giveTo(player.getPlayer()));
        getData().logTimeLastLoot(dGroup.getDungeon().getName());
    }

    public boolean canLoot(GameRuleContainer rules) {
        return getTimeNextLoot(rules) <= getData().getTimeLastStarted(getGroup().getDungeonName());
    }

    public long getTimeNextLoot(GameRuleContainer rules) {
        return rules.getState(GameRule.TIME_TO_NEXT_LOOT) * 60 * 60 * 1000 + getData().getTimeLastLoot(getGroup().getDungeonName());
    }

    @Override
    public boolean ready() {
        return ready(true);
    }

    public boolean ready(boolean rewards) {
        if (dGroup == null) {
            return false;
        }

        Game game = dGroup.getGame();
        if (game == null) {
            game = new DGame(plugin, dGroup, dGroup.getGameWorld());
        }
        game.setRewards(rewards);

        if (!checkRequirements(game.getDungeon())) {
            MessageUtil.sendMessage(player, DMessage.ERROR_REQUIREMENTS.getMessage());
            return false;
        }

        ready = true;

        boolean start = true;
        for (PlayerGroup gameGroup : game.getGroups()) {
            if (!gameGroup.isPlaying()) {
                if (!((DGroup) gameGroup).startGame(game)) {
                    start = false;
                }
            } else {
                respawn();
            }
        }

        game.setStarted(true);
        return start;
    }

    @Override
    public void respawn() {
        Location respawn = checkpoint;

        if (respawn == null) {
            respawn = getGroup().getGameWorld().getStartLocation(dGroup);
        }

        if (respawn == null) {
            respawn = getWorld().getSpawnLocation();
        }

        PlayerUtil.secureTeleport(getPlayer(), respawn);

        // Don't forget Doge!
        if (wolf != null) {
            wolf.teleport(getPlayer());
        }
    }

    /**
     * The DGamePlayer finishs the current floor.
     *
     * @param specifiedFloor the name of the next floor
     */
    public void finishFloor(DResourceWorld specifiedFloor) {
        if (!dGroup.getDungeon().isMultiFloor()) {
            finish();
            return;
        }

        MessageUtil.sendMessage(getPlayer(), DMessage.PLAYER_FINISHED_FLOOR.getMessage());
        finished = true;

        boolean hasToWait = false;
        if (getGroup() == null) {
            return;
        }
        if (!dGroup.isPlaying()) {
            return;
        }
        dGroup.setNextFloor(specifiedFloor);
        if (dGroup.isFinished()) {
            dGroup.finishFloor(specifiedFloor);
        } else {
            MessageUtil.sendMessage(player, DMessage.PLAYER_WAIT_FOR_OTHER_PLAYERS.getMessage());
            hasToWait = true;
        }

        DGamePlayerFinishEvent dPlayerFinishEvent = new DGamePlayerFinishEvent(this, hasToWait);
        Bukkit.getPluginManager().callEvent(dPlayerFinishEvent);
        if (dPlayerFinishEvent.isCancelled()) {
            finished = false;
        }
    }

    @Override
    public void finish() {
        finish(true);
    }

    @Override
    public void finish(boolean message) {
        if (message) {
            MessageUtil.sendMessage(getPlayer(), DMessage.PLAYER_FINISHED_DUNGEON.getMessage());
        }
        finished = true;

        boolean hasToWait = false;
        if (!getGroup().isPlaying()) {
            return;
        }
        if (dGroup.isFinished()) {
            dGroup.finish();
        } else {
            if (message) {
                MessageUtil.sendMessage(this.getPlayer(), DMessage.PLAYER_WAIT_FOR_OTHER_PLAYERS.getMessage());
            }
            hasToWait = true;
        }

        DGamePlayerFinishEvent dPlayerFinishEvent = new DGamePlayerFinishEvent(this, hasToWait);
        Bukkit.getPluginManager().callEvent(dPlayerFinishEvent);
        if (dPlayerFinishEvent.isCancelled()) {
            finished = false;
        }
    }

    public void onDeath(PlayerDeathEvent event) {
        DGameWorld gameWorld = (DGameWorld) getGameWorld();
        if (gameWorld == null) {
            return;
        }

        DGame game = (DGame) getGame();
        if (game == null) {
            return;
        }

        DGamePlayerDeathEvent dPlayerDeathEvent = new DGamePlayerDeathEvent(this, event, 1);
        Bukkit.getPluginManager().callEvent(dPlayerDeathEvent);

        if (dPlayerDeathEvent.isCancelled()) {
            return;
        }

        if (config.areGlobalDeathMessagesDisabled()) {
            event.setDeathMessage(null);
        }

        if (game.getRules().getState(GameRule.KEEP_INVENTORY_ON_DEATH)) {
            event.setKeepInventory(true);
            event.getDrops().clear();
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }

        if (getGroup() != null && dGroup.getLives() != -1) {
            int newLives = dGroup.getLives() - dPlayerDeathEvent.getLostLives();
            dGroup.setLives(newLives < 0 ? 0 : newLives);// If the group already has 0 lives, don't remove any
            gameWorld.sendMessage(DMessage.GROUP_DEATH.getMessage(getName(), dGroup.getName(), String.valueOf(dGroup.getLives())));

        } else {
            if (lives != -1) {
                lives = lives - dPlayerDeathEvent.getLostLives();
            }

            GamePlayer killer = plugin.getPlayerCache().getGamePlayer(player.getKiller());
            String newLives = lives == -1 ? DMessage.MISC_UNLIMITED.getMessage() : String.valueOf(this.lives);
            if (killer != null) {
                gameWorld.sendMessage(DMessage.PLAYER_KILLED.getMessage(getName(), killer.getName(), newLives));
            } else {
                gameWorld.sendMessage(DMessage.PLAYER_DEATH.getMessage(getName(), newLives));
            }
        }

        if (isStealingFlag()) {
            for (TeamFlag teamFlag : gameWorld.getTeamFlags()) {
                if (teamFlag.getOwner().equals(stealing)) {
                    teamFlag.reset();
                    gameWorld.sendMessage(DMessage.GROUP_FLAG_LOST.getMessage(player.getName(), stealing.getName()));
                    stealing = null;
                }
            }
        }

        if ((dGroup.getLives() == 0 || lives == 0) && ready) {
            kill();
        }

        GameType gameType = game.getType();
        if (gameType != null && gameType != GameTypeDefault.CUSTOM) {
            if (gameType.getGameGoal() == GameGoal.LAST_MAN_STANDING) {
                if (game.getGroups().size() == 1) {
                    ((DGroup) game.getGroups().get(0)).winGame();
                }
            }
        }
    }

    @Override
    public void update(boolean updateSecond) {
        boolean locationValid = true;
        Location teleportLocation = player.getLocation();
        boolean teleportWolf = false;
        boolean offline = false;
        boolean kick = false;
        boolean triggerAllInDistance = false;

        if (!updateSecond) {
            if (!getPlayer().getWorld().equals(getWorld())) {
                locationValid = false;

                if (getGameWorld() != null) {
                    teleportLocation = getLastCheckpoint();

                    if (teleportLocation == null) {
                        teleportLocation = getGameWorld().getStartLocation(getGroup());
                    }

                    // Don't forget Doge!
                    if (getWolf() != null) {
                        teleportWolf = true;
                    }
                }
            }

        } else if (getGameWorld() != null) {
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

                // TODO Is doge even DMob?
                DungeonMob dMob = plugin.getDungeonMob(getWolf());
                if (dMob != null) {
                    getGameWorld().removeMob(dMob);
                }
            }

            // Kick offline players
            if (getOfflineTimeMillis() > 0) {
                offline = true;

                if (getOfflineTimeMillis() < System.currentTimeMillis()) {
                    kick = true;
                }
            }

            triggerAllInDistance = true;
        }

        DInstancePlayerUpdateEvent event = new DInstancePlayerUpdateEvent(this, locationValid, teleportWolf, offline, kick, triggerAllInDistance);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        if (!locationValid) {
            PlayerUtil.secureTeleport(getPlayer(), teleportLocation);
        }

        if (teleportWolf) {
            getWolf().teleport(teleportLocation);
        }

        if (kick) {
            DPlayerKickEvent dPlayerKickEvent = new DPlayerKickEvent(this, DPlayerKickEvent.Cause.OFFLINE);
            Bukkit.getPluginManager().callEvent(dPlayerKickEvent);

            if (!dPlayerKickEvent.isCancelled()) {
                leave();
            }
        }

        if (triggerAllInDistance) {
            DistanceTrigger.triggerAllInDistance(getPlayer(), (DGameWorld) getGameWorld());
        }
    }

}
