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

import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.compatibility.Version;
import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.commons.util.playerutil.PlayerUtil;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.config.DungeonConfig;
import io.github.dre2n.dungeonsxl.config.WorldConfig;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupFinishDungeonEvent;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupFinishFloorEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerFinishEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerKickEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerUpdateEvent;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameType;
import io.github.dre2n.dungeonsxl.game.GameTypeDefault;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.reward.DLootInventory;
import io.github.dre2n.dungeonsxl.reward.Reward;
import io.github.dre2n.dungeonsxl.trigger.DistanceTrigger;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a player in a GameWorld.
 *
 * @author Frank Baumann, Tobias Schmitz, Milan Albrecht, Daniel Saukel
 */
public class DPlayer extends DGlobalPlayer {

    // Variables
    private World world;

    private DSavePlayer savePlayer;

    private boolean editing;
    private boolean inDungeonChat = false;
    private boolean ready = false;
    private boolean finished = false;

    private DClass dClass;
    private Location checkpoint;
    private Wolf wolf;
    private int wolfRespawnTime = 30;
    private long offlineTime;
    private String[] linesCopy;

    private Inventory treasureInv = plugin.getServer().createInventory(getPlayer(), 45, DMessages.PLAYER_TREASURES.getMessage());

    private int initialLives = -1;
    private int lives;

    public DPlayer(Player player, GameWorld gameWorld) {
        this(player, gameWorld.getWorld(), false);
    }

    @Deprecated
    public DPlayer(Player player, World world, boolean editing) {
        super(player);

        this.world = world;

        double health = player.getHealth();

        if (!Version.andHigher(Version.MC1_9).contains(CompatibilityHandler.getInstance().getVersion())) {
            savePlayer = new DSavePlayer(player.getName(), player.getUniqueId(), player.getLocation(), player.getInventory().getContents(), player.getInventory().getArmorContents(), null, player.getLevel(),
                    player.getTotalExperience(), (int) health, player.getFoodLevel(), player.getFireTicks(), player.getGameMode(), player.getActivePotionEffects());

        } else {
            savePlayer = new DSavePlayer(player.getName(), player.getUniqueId(), player.getLocation(), player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getInventory().getItemInOffHand(), player.getLevel(),
                    player.getTotalExperience(), (int) health, player.getFoodLevel(), player.getFireTicks(), player.getGameMode(), player.getActivePotionEffects());
        }
        this.editing = editing;

        Location teleport;
        if (this.editing) {
            this.getPlayer().setGameMode(GameMode.CREATIVE);
            clearPlayerData();
            teleport = EditWorld.getByWorld(world).getLobby();

        } else {
            WorldConfig worldConfig = GameWorld.getByWorld(world).getConfig();
            this.getPlayer().setGameMode(GameMode.SURVIVAL);
            if (!worldConfig.getKeepInventoryOnEnter()) {
                clearPlayerData();
            }
            if (worldConfig.isLobbyDisabled()) {
                ready();
            }
            initialLives = worldConfig.getInitialLives();
            lives = initialLives;
            teleport = GameWorld.getByWorld(world).getLocLobby();
        }

        if (teleport == null) {
            PlayerUtil.secureTeleport(player, world.getSpawnLocation());

        } else {
            PlayerUtil.secureTeleport(player, teleport);
        }
    }

    public void clearPlayerData() {
        getPlayer().getInventory().clear();
        getPlayer().getInventory().setArmorContents(null);
        getPlayer().setTotalExperience(0);
        getPlayer().setLevel(0);
        getPlayer().setHealth(20);
        getPlayer().setFoodLevel(20);
        for (PotionEffect effect : getPlayer().getActivePotionEffects()) {
            getPlayer().removePotionEffect(effect.getType());
        }
    }

    /* Getters and setters */
    /**
     * @param player
     * the player to set
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * @param world
     * the world to set
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * @return the savePlayer
     */
    public DSavePlayer getSavePlayer() {
        return savePlayer;
    }

    /**
     * @param savePlayer
     * the savePlayer to set
     */
    public void setSavePlayer(DSavePlayer savePlayer) {
        this.savePlayer = savePlayer;
    }

    /**
     * @return if the player is in test mode
     */
    public boolean isInTestMode() {
        DGroup dGroup = DGroup.getByPlayer(getPlayer());
        if (dGroup == null) {
            return false;
        }

        GameWorld gameWorld = dGroup.getGameWorld();
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

    @Deprecated
    public boolean isEditing() {
        return editing;
    }

    @Deprecated
    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    /**
     * @return the inDungeonChat
     */
    public boolean isInDungeonChat() {
        return inDungeonChat;
    }

    /**
     * @param inDungeonChat
     * the inDungeonChat to set
     */
    public void setInDungeonChat(boolean inDungeonChat) {
        this.inDungeonChat = inDungeonChat;
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
        GameWorld gameWorld = GameWorld.getByWorld(getPlayer().getWorld());
        if (gameWorld == null) {
            return;
        }

        DClass dClass = gameWorld.getConfig().getClass(className);
        if (dClass != null) {
            if (this.dClass != dClass) {
                this.dClass = dClass;

                /* Set Dog */
                if (wolf != null) {
                    wolf.remove();
                    wolf = null;
                }

                if (dClass.hasDog()) {
                    wolf = (Wolf) world.spawnEntity(getPlayer().getLocation(), EntityType.WOLF);
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
     * @return the linesCopy
     */
    public String[] getLinesCopy() {
        return linesCopy;
    }

    /**
     * @param linesCopy
     * the linesCopy to set
     */
    public void setLinesCopy(String[] linesCopy) {
        this.linesCopy = linesCopy;
    }

    /**
     * @return the treasureInv
     */
    public Inventory getTreasureInv() {
        return treasureInv;
    }

    /**
     * @param treasureInv
     * the treasureInv to set
     */
    public void setTreasureInv(Inventory treasureInv) {
        this.treasureInv = treasureInv;
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

    /* Actions */
    public void escape() {
        delete();
        savePlayer.reset(false);
    }

    public void leave() {
        delete();

        if (!editing) {
            WorldConfig dConfig = GameWorld.getByWorld(world).getConfig();
            if (finished) {
                savePlayer.reset(dConfig.getKeepInventoryOnFinish());
            } else {
                savePlayer.reset(dConfig.getKeepInventoryOnEscape());
            }

        } else {
            savePlayer.reset(false);
        }

        GameWorld gameWorld = GameWorld.getByWorld(world);
        DGroup dGroup = DGroup.getByPlayer(getPlayer());

        if (editing) {
            EditWorld editWorld = EditWorld.getByWorld(world);
            if (editWorld != null) {
                editWorld.save();
            }

        } else {
            Game game = Game.getByGameWorld(gameWorld);
            if (dGroup != null) {
                dGroup.removePlayer(getPlayer());
            }

            // Belohnung
            if (game != null) {
                if (finished) {
                    if (game.getType().hasRewards()) {
                        for (Reward reward : gameWorld.getConfig().getRewards()) {
                            reward.giveTo(getPlayer());
                        }

                        addTreasure();

                        // Set Time
                        File file = new File(plugin.getDataFolder() + "/maps/" + gameWorld.getMapName(), "players.yml");

                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);

                        playerConfig.set(getPlayer().getUniqueId().toString(), System.currentTimeMillis());

                        try {
                            playerConfig.save(file);

                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }

                        // Tutorial Permissions
                        if (gameWorld.isTutorial()) {
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

            if (dGroup != null) {
                // Give Secure Objects other Players
                if (!dGroup.isEmpty()) {
                    int i = 0;
                    Player groupPlayer;
                    do {
                        groupPlayer = dGroup.getPlayers().get(i);
                        if (groupPlayer != null) {
                            for (ItemStack itemStack : getPlayer().getInventory()) {
                                if (itemStack != null) {
                                    if (gameWorld.getSecureObjects().contains(itemStack.getType())) {
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
                    MessageUtil.sendMessage(newCaptain, DMessages.PLAYER_NEW_CAPTAIN.getMessage());
                    // ...*flies away*
                }
            }
        }
    }

    public void ready() {
        ready(GameTypeDefault.DEFAULT);
    }

    public void ready(GameType gameType) {
        ready = true;

        DGroup dGroup = DGroup.getByPlayer(getPlayer());

        if (dGroup == null) {
            return;
        }

        Game game = Game.getByGameWorld(dGroup.getGameWorld());
        if (game == null) {
            game = new Game(dGroup, gameType, dGroup.getGameWorld());

        } else {
            game.setType(gameType);
        }

        for (DGroup gameGroup : game.getDGroups()) {
            if (!gameGroup.isPlaying()) {
                gameGroup.startGame(game);

            } else {
                respawn();
            }
        }
    }

    public void respawn() {
        DGroup dGroup = DGroup.getByPlayer(getPlayer());

        Location respawn = checkpoint;

        if (respawn == null) {
            respawn = dGroup.getGameWorld().getLocStart();
        }

        if (respawn == null) {
            respawn = dGroup.getGameWorld().getLocLobby();
        }

        if (respawn == null) {
            respawn = world.getSpawnLocation();
        }

        PlayerUtil.secureTeleport(getPlayer(), respawn);

        // Don't forget Doge!
        if (wolf != null) {
            wolf.teleport(getPlayer());
        }

        // Respawn Items
        if (GameWorld.getByWorld(world).getConfig().getKeepInventoryOnDeath()) {
            applyRespawnInventory();
        }
    }

    public void finishFloor(String specifiedFloor) {
        MessageUtil.sendMessage(getPlayer(), DMessages.PLAYER_FINISHED_DUNGEON.getMessage());
        finished = true;

        DGroup dGroup = DGroup.getByPlayer(getPlayer());
        if (dGroup == null) {
            return;
        }

        if (!dGroup.isPlaying()) {
            return;
        }

        for (Player player : dGroup.getPlayers()) {
            DPlayer dPlayer = getByPlayer(player);
            if (!dPlayer.finished) {
                MessageUtil.sendMessage(this.getPlayer(), DMessages.PLAYER_WAIT_FOR_OTHER_PLAYERS.getMessage());
                return;
            }
        }

        boolean invalid = false;

        if (dGroup.getDungeon() == null) {
            invalid = true;
        }

        for (Player player : dGroup.getPlayers()) {
            DPlayer dPlayer = getByPlayer(player);

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
        String newFloor = dGroup.getUnplayedFloors().get(random);
        if (dConfig.getFloorCount() == dGroup.getFloorCount() - 1) {
            newFloor = dConfig.getEndFloor();

        } else if (specifiedFloor != null) {
            newFloor = specifiedFloor;
        }

        DGroupFinishFloorEvent event = new DGroupFinishFloorEvent(dGroup, dGroup.getGameWorld(), newFloor);

        if (event.isCancelled()) {
            return;
        }

        Game game = dGroup.getGameWorld().getGame();

        dGroup.removeUnplayedFloor(dGroup.getMapName());
        dGroup.setMapName(newFloor);
        GameWorld gameWorld = GameWorld.load(newFloor);
        dGroup.setGameWorld(gameWorld);
        for (Player player : dGroup.getPlayers()) {
            DPlayer dPlayer = getByPlayer(player);
            dPlayer.setWorld(gameWorld.getWorld());
            dPlayer.setCheckpoint(dGroup.getGameWorld().getLocStart());
            if (dPlayer.getWolf() != null) {
                dPlayer.getWolf().teleport(dPlayer.getCheckpoint());
            }
        }
        dGroup.startGame(game);
    }

    public void finish() {
        finish(true);
    }

    public void finish(boolean message) {
        if (message) {
            MessageUtil.sendMessage(getPlayer(), DMessages.PLAYER_FINISHED_DUNGEON.getMessage());
        }
        finished = true;

        DGroup dGroup = DGroup.getByPlayer(getPlayer());
        if (dGroup == null) {
            return;
        }

        if (!dGroup.isPlaying()) {
            return;
        }

        boolean first = true;
        boolean hasToWait = false;

        for (Player player : dGroup.getPlayers()) {
            DPlayer dPlayer = getByPlayer(player);
            if (!dPlayer.finished) {
                if (message) {
                    MessageUtil.sendMessage(this.getPlayer(), DMessages.PLAYER_WAIT_FOR_OTHER_PLAYERS.getMessage());
                }
                hasToWait = true;

            } else if (dPlayer != this) {
                first = false;
            }
        }

        DPlayerFinishEvent dPlayerFinishEvent = new DPlayerFinishEvent(this, first, hasToWait);

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

        for (Player player : dGroup.getPlayers()) {
            DPlayer dPlayer = getByPlayer(player);
            dPlayer.leave();

            for (Reward reward : dGroup.getRewards()) {
                reward.giveTo(player);
            }
        }
    }

    public void sendMessage(String message) {
        if (editing) {
            EditWorld editWorld = EditWorld.getByWorld(world);
            editWorld.sendMessage(message);
            for (DGlobalPlayer player : plugin.getDPlayers().getPlayers()) {
                if (player.isInChatSpyMode()) {
                    if (!editWorld.getWorld().getPlayers().contains(player.getPlayer())) {
                        MessageUtil.sendMessage(player.getPlayer(), ChatColor.GREEN + "[Chatspy] " + ChatColor.WHITE + message);
                    }
                }
            }

        } else {
            GameWorld gameWorld = GameWorld.getByWorld(world);
            gameWorld.sendMessage(message);
            for (DGlobalPlayer player : plugin.getDPlayers().getPlayers()) {
                if (player.isInChatSpyMode()) {
                    if (!gameWorld.getWorld().getPlayers().contains(player.getPlayer())) {
                        MessageUtil.sendMessage(player.getPlayer(), ChatColor.GREEN + "[Chatspy] " + ChatColor.WHITE + message);
                    }
                }
            }
        }
    }

    public void poke(Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            String[] lines = sign.getLines();
            if (lines[0].isEmpty() && lines[1].isEmpty() && lines[2].isEmpty() && lines[3].isEmpty()) {
                if (linesCopy != null) {
                    SignChangeEvent event = new SignChangeEvent(block, getPlayer(), linesCopy);
                    plugin.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        sign.setLine(0, event.getLine(0));
                        sign.setLine(1, event.getLine(1));
                        sign.setLine(2, event.getLine(2));
                        sign.setLine(3, event.getLine(3));
                        sign.update();
                    }
                }
            } else {
                linesCopy = lines;
                MessageUtil.sendMessage(getPlayer(), DMessages.PLAYER_SIGN_COPIED.getMessage());
            }
        } else {
            String info = "" + block.getType();
            if (block.getData() != 0) {
                info = info + "," + block.getData();
            }
            MessageUtil.sendMessage(getPlayer(), DMessages.PLAYER_BLOCK_INFO.getMessage(info));
        }
    }

    public void addTreasure() {
        new DLootInventory(getPlayer(), treasureInv.getContents());
    }

    public void update(boolean updateSecond) {
        boolean locationValid = true;
        Location teleportLocation = player.getLocation();
        boolean teleportWolf = false;
        boolean respawnInventory = false;
        boolean offline = false;
        boolean kick = false;
        boolean triggerAllInDistance = false;

        GameWorld gameWorld = GameWorld.getByWorld(getWorld());
        EditWorld editWorld = EditWorld.getByWorld(getWorld());

        if (!updateSecond) {
            if (!getPlayer().getWorld().equals(getWorld())) {
                locationValid = false;

                if (isEditing()) {
                    if (editWorld != null) {
                        if (editWorld.getLobby() == null) {
                            teleportLocation = editWorld.getWorld().getSpawnLocation();
                        } else {
                            teleportLocation = editWorld.getLobby();
                        }
                    }

                } else if (gameWorld != null) {
                    DGroup dGroup = DGroup.getByPlayer(getPlayer());

                    teleportLocation = getCheckpoint();

                    if (teleportLocation == null) {
                        teleportLocation = dGroup.getGameWorld().getLocStart();
                    }

                    if (teleportLocation == null) {
                        teleportLocation = dGroup.getGameWorld().getLocLobby();
                    }

                    if (teleportLocation == null) {
                        teleportLocation = getWorld().getSpawnLocation();
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

            // Kick offline plugin.getDPlayers()
            if (getOfflineTime() > 0) {
                offline = true;

                if (getOfflineTime() < System.currentTimeMillis()) {
                    kick = true;
                }
            }

            triggerAllInDistance = true;
        }

        DPlayerUpdateEvent event = new DPlayerUpdateEvent(this, locationValid, teleportWolf, respawnInventory, offline, kick, triggerAllInDistance);

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

    /**
     * Delete this DPlayer. Creates a DGlobalPlayer to replace it!
     */
    public void delete() {
        if (player.isOnline()) {
            // Create a new DGlobalPlayer (outside a dungeon)
            new DGlobalPlayer(this);

        } else {
            plugin.getDPlayers().removePlayer(this);
        }
    }

    /* Statics */
    public static DPlayer getByPlayer(Player player) {
        for (DPlayer dPlayer : plugin.getDPlayers().getDPlayers()) {
            if (dPlayer.getPlayer().equals(player)) {
                return dPlayer;
            }
        }
        return null;
    }

    public static DPlayer getByName(String name) {
        for (DPlayer dPlayer : plugin.getDPlayers().getDPlayers()) {
            if (dPlayer.getPlayer().getName().equalsIgnoreCase(name)) {
                return dPlayer;
            }
        }
        return null;
    }

    public static CopyOnWriteArrayList<DPlayer> getByWorld(World world) {
        CopyOnWriteArrayList<DPlayer> dPlayers = new CopyOnWriteArrayList<>();

        for (DPlayer dPlayer : plugin.getDPlayers().getDPlayers()) {
            if (dPlayer.world == world) {
                dPlayers.add(dPlayer);
            }
        }

        return dPlayers;
    }

}
