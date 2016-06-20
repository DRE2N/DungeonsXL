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
package io.github.dre2n.dungeonsxl.world;

import io.github.dre2n.commons.util.FileUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DungeonConfig;
import io.github.dre2n.dungeonsxl.config.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.event.gameworld.GameWorldLoadEvent;
import io.github.dre2n.dungeonsxl.event.gameworld.GameWorldStartGameEvent;
import io.github.dre2n.dungeonsxl.event.gameworld.GameWorldUnloadEvent;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GamePlaceableBlock;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.reward.RewardChest;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.sign.DSignType;
import io.github.dre2n.dungeonsxl.sign.DSignTypeDefault;
import io.github.dre2n.dungeonsxl.sign.MobSign;
import io.github.dre2n.dungeonsxl.sign.StartSign;
import io.github.dre2n.dungeonsxl.trigger.ProgressTrigger;
import io.github.dre2n.dungeonsxl.trigger.RedstoneTrigger;
import io.github.dre2n.dungeonsxl.trigger.Trigger;
import io.github.dre2n.dungeonsxl.trigger.TriggerType;
import io.github.dre2n.dungeonsxl.trigger.TriggerTypeDefault;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class GameWorld {

    static DungeonsXL plugin = DungeonsXL.getInstance();

    // Variables
    private boolean tutorial;

    private CopyOnWriteArrayList<GamePlaceableBlock> placeableBlocks = new CopyOnWriteArrayList<>();
    private World world;
    private String mapName;
    private Location locLobby;
    private boolean isPlaying = false;
    private int id;
    private List<ItemStack> secureObjects = new ArrayList<>();
    private CopyOnWriteArrayList<Chunk> loadedChunks = new CopyOnWriteArrayList<>();

    private CopyOnWriteArrayList<Sign> signClass = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DMob> dMobs = new CopyOnWriteArrayList<>();
    // TODO: Killed mobs
    private CopyOnWriteArrayList<RewardChest> rewardChests = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DSign> dSigns = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Trigger> triggers = new CopyOnWriteArrayList<>();
    private WorldConfig worldConfig;

    public GameWorld() {
        plugin.getGameWorlds().add(this);

        // ID
        id = -1;
        int i = -1;
        while (id == -1) {
            i++;
            boolean exist = false;
            for (GameWorld gameWorld : plugin.getGameWorlds()) {
                if (gameWorld.id == i) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                id = i;
            }
        }
    }

    public GameWorld(String name) {
        this();
        load(name);
    }

    /**
     * @return
     * the Game connected to the GameWorld
     */
    public Game getGame() {
        for (Game game : plugin.getGames()) {
            if (game.getWorld() == this) {
                return game;
            }
        }

        return null;
    }

    /**
     * @return the tutorial
     */
    public boolean isTutorial() {
        return tutorial;
    }

    /**
     * @param tutorial
     * if the GameWorld is the tutorial
     */
    public void setTutorial(boolean tutorial) {
        this.tutorial = tutorial;
    }

    /**
     * @return the placeableBlocks
     */
    public CopyOnWriteArrayList<GamePlaceableBlock> getPlaceableBlocks() {
        return placeableBlocks;
    }

    /**
     * @param placeableBlocks
     * the placeableBlocks to set
     */
    public void setPlaceableBlocks(CopyOnWriteArrayList<GamePlaceableBlock> placeableBlocks) {
        this.placeableBlocks = placeableBlocks;
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
     * @return the mapName
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @param mapName
     * the mapName to set
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * @return the location of the lobby
     */
    public Location getLobbyLocation() {
        return locLobby;
    }

    /**
     * @param location
     * the location of the lobby to set
     */
    public void setLobbyLocation(Location location) {
        this.locLobby = location;
    }

    /**
     * @return the start location
     */
    public Location getStartLocation(DGroup dGroup) {
        int index = getGame().getDGroups().indexOf(dGroup);

        // Try the matching location
        for (DSign dSign : dSigns) {
            if (dSign.getType() == DSignTypeDefault.START) {
                if (((StartSign) dSign).getId() == index) {
                    return dSign.getSign().getLocation();
                }
            }
        }

        // Try any location
        for (DSign dSign : dSigns) {
            if (dSign.getType() == DSignTypeDefault.START) {
                return dSign.getSign().getLocation();
            }
        }

        // Lobby location as fallback
        if (locLobby != null) {
            return locLobby;
        }

        return world.getSpawnLocation();
    }

    /**
     * @return the isPlaying
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * @param isPlaying
     * the isPlaying to set
     */
    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     * the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the secureObjects
     */
    public List<ItemStack> getSecureObjects() {
        return secureObjects;
    }

    /**
     * @param secureObjects
     * the secureObjects to set
     */
    public void setSecureObjects(List<ItemStack> secureObjects) {
        this.secureObjects = secureObjects;
    }

    /**
     * @return the loadedChunks
     */
    public CopyOnWriteArrayList<Chunk> getLoadedChunks() {
        return loadedChunks;
    }

    /**
     * @param loadedChunks
     * the loadedChunks to set
     */
    public void setLoadedChunks(CopyOnWriteArrayList<Chunk> loadedChunks) {
        this.loadedChunks = loadedChunks;
    }

    /**
     * @return the signClass
     */
    public CopyOnWriteArrayList<Sign> getSignClass() {
        return signClass;
    }

    /**
     * @param signClass
     * the signClass to set
     */
    public void setSignClass(CopyOnWriteArrayList<Sign> signClass) {
        this.signClass = signClass;
    }

    /**
     * @return the dMobs
     */
    public CopyOnWriteArrayList<DMob> getDMobs() {
        return dMobs;
    }

    /**
     * @param dMob
     * the dMob to add
     */
    public void addDMob(DMob dMob) {
        dMobs.add(dMob);
    }

    /**
     * @param dMob
     * the dMob to remove
     */
    public void removeDMob(DMob dMob) {
        dMobs.remove(dMob);
    }

    /**
     * @return the rewardChests
     */
    public CopyOnWriteArrayList<RewardChest> getRewardChests() {
        return rewardChests;
    }

    /**
     * @param rewardChests
     * the rewardChests to set
     */
    public void setRewardChests(CopyOnWriteArrayList<RewardChest> rewardChests) {
        this.rewardChests = rewardChests;
    }

    /**
     * @return the dSigns
     */
    public CopyOnWriteArrayList<DSign> getDSigns() {
        return dSigns;
    }

    /**
     * @return the triggers with the type
     */
    public List<DSign> getDSigns(DSignType type) {
        List<DSign> dSignsOfType = new ArrayList<>();
        for (DSign dSign : dSigns) {
            if (dSign.getType() == type) {
                dSignsOfType.add(dSign);
            }
        }
        return dSignsOfType;
    }

    /**
     * @param dSigns
     * the dSigns to set
     */
    public void setDSigns(CopyOnWriteArrayList<DSign> dSigns) {
        this.dSigns = dSigns;
    }

    /**
     * @return the triggers
     */
    public CopyOnWriteArrayList<Trigger> getTriggers() {
        return triggers;
    }

    /**
     * @return the triggers with the type
     */
    public List<Trigger> getTriggers(TriggerType type) {
        List<Trigger> triggersOfType = new ArrayList<>();
        for (Trigger trigger : triggers) {
            if (trigger.getType() == type) {
                triggersOfType.add(trigger);
            }
        }
        return triggersOfType;
    }

    /**
     * @param trigger
     * the trigger to add
     */
    public void addTrigger(Trigger trigger) {
        triggers.add(trigger);
    }

    /**
     * @param trigger
     * the trigger to remove
     */
    public void removeTrigger(Trigger trigger) {
        triggers.remove(trigger);
    }

    /**
     * @return the potential amount of mobs in the world
     */
    public int getMobCount() {
        int mobCount = 0;

        signs:
        for (DSign dSign : dSigns) {
            if (!(dSign instanceof MobSign)) {
                continue;
            }

            for (Trigger trigger : dSign.getTriggers()) {
                if (trigger.getType() == TriggerTypeDefault.PROGRESS) {
                    if (((ProgressTrigger) trigger).getFloorCount() > getGame().getFloorCount()) {
                        break signs;
                    }
                }
            }

            mobCount += ((MobSign) dSign).getInitialAmount();
        }

        return mobCount;
    }

    /**
     * @return the worldConfig
     */
    public WorldConfig getConfig() {
        if (worldConfig == null) {
            return plugin.getMainConfig().getDefaultWorldConfig();
        }

        return worldConfig;
    }

    /**
     * @param worldConfig
     * the worldConfig to set
     */
    public void setConfig(WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
    }

    /**
     * @return the Dungeon that contains the GameWorld
     */
    public Dungeon getDungeon() {
        for (Dungeon dungeon : plugin.getDungeons().getDungeons()) {
            DungeonConfig dungeonConfig = dungeon.getConfig();
            if (dungeonConfig.getFloors().contains(mapName) || dungeonConfig.getStartFloor().equals(mapName) || dungeonConfig.getEndFloor().equals(mapName)) {
                return dungeon;
            }
        }

        return null;
    }

    public void checkSign(Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            dSigns.add(DSign.create(sign, this));
        }
    }

    public void startGame() {
        GameWorldStartGameEvent event = new GameWorldStartGameEvent(this, getGame());
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        isPlaying = true;

        for (DSign dSign : dSigns) {
            if (dSign != null) {
                if (!dSign.getType().isOnDungeonInit()) {
                    dSign.onInit();
                }
            }
        }

        for (Trigger trigger : getTriggers(TriggerTypeDefault.REDSTONE)) {
            ((RedstoneTrigger) trigger).onTrigger();
        }

        for (DSign dSign : dSigns) {
            if (dSign != null) {
                if (!dSign.hasTriggers()) {
                    dSign.onTrigger();
                }
            }
        }
    }

    public void sendMessage(String message) {
        for (DGamePlayer dPlayer : DGamePlayer.getByWorld(world)) {
            MessageUtil.sendMessage(dPlayer.getPlayer(), message);
        }
    }

    public void delete() {
        GameWorldUnloadEvent event = new GameWorldUnloadEvent(this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        plugin.getGameWorlds().remove(this);
        plugin.getServer().unloadWorld(world, true);
        File dir = new File("DXL_Game_" + id);
        FileUtil.removeDirectory(dir);
    }

    public void update() {
        if (getWorld() == null) {
            return;
        }

        // Update Spiders
        for (LivingEntity mob : getWorld().getLivingEntities()) {
            if (mob.getType() == EntityType.SPIDER || mob.getType() == EntityType.CAVE_SPIDER) {
                Spider spider = (Spider) mob;
                if (spider.getTarget() != null) {
                    if (spider.getTarget().getType() == EntityType.PLAYER) {
                        continue;
                    }
                }
                for (Entity player : spider.getNearbyEntities(10, 10, 10)) {
                    if (player.getType() == EntityType.PLAYER) {
                        spider.setTarget((LivingEntity) player);
                    }
                }
            }
        }
    }

    public void load(String name) {
        GameWorldLoadEvent event = new GameWorldLoadEvent(name);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        File file = new File(plugin.getDataFolder(), "/maps/" + name);

        if (file.exists()) {
            mapName = name;

            // Unload empty editWorlds
            for (EditWorld editWorld : plugin.getEditWorlds()) {
                if (editWorld.getWorld().getPlayers().isEmpty()) {
                    editWorld.delete();
                }
            }

            // Config einlesen
            worldConfig = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + mapName, "config.yml"));

            // Secure Objects
            secureObjects = worldConfig.getSecureObjects();

            if (Bukkit.getWorld("DXL_Game_" + id) == null) {

                // World
                FileUtil.copyDirectory(file, new File("DXL_Game_" + id), DungeonsXL.EXCLUDED_FILES);

                // Id File
                File idFile = new File("DXL_Game_" + id + "/.id_" + name);
                try {
                    idFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                world = plugin.getServer().createWorld(WorldCreator.name("DXL_Game_" + id));

                ObjectInputStream os;
                try {
                    os = new ObjectInputStream(new FileInputStream(new File(plugin.getDataFolder() + "/maps/" + mapName + "/DXLData.data")));

                    int length = os.readInt();
                    for (int i = 0; i < length; i++) {
                        int x = os.readInt();
                        int y = os.readInt();
                        int z = os.readInt();
                        Block block = world.getBlockAt(x, y, z);
                        checkSign(block);
                    }

                    os.close();

                } catch (FileNotFoundException exception) {
                    MessageUtil.log(plugin, "Could not find any sign data for the world \"" + name + "\"!");

                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    /* Statics */
    public static GameWorld getByWorld(World world) {
        for (GameWorld gameWorld : plugin.getGameWorlds()) {
            if (gameWorld.getWorld() != null && gameWorld.getWorld().equals(world)) {
                return gameWorld;
            }
        }

        return null;
    }

    public static void deleteAll() {
        for (GameWorld gameWorld : plugin.getGameWorlds()) {
            gameWorld.delete();
        }
    }

}
