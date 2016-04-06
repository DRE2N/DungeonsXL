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
package io.github.dre2n.dungeonsxl.game;

import io.github.dre2n.commons.util.FileUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DungeonConfig;
import io.github.dre2n.dungeonsxl.config.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.event.gameworld.GameWorldLoadEvent;
import io.github.dre2n.dungeonsxl.event.gameworld.GameWorldStartGameEvent;
import io.github.dre2n.dungeonsxl.event.gameworld.GameWorldUnloadEvent;
import io.github.dre2n.dungeonsxl.event.requirement.RequirementCheckEvent;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.requirement.Requirement;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.trigger.RedstoneTrigger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class GameWorld {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();

    // Variables
    private Game game;

    private boolean tutorial;

    private CopyOnWriteArrayList<GamePlaceableBlock> placeableBlocks = new CopyOnWriteArrayList<>();
    private World world;
    private String mapName;
    private Location locLobby;
    private Location locStart;
    private boolean isPlaying = false;
    private int id;
    private CopyOnWriteArrayList<Material> secureObjects = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Chunk> loadedChunks = new CopyOnWriteArrayList<>();

    private CopyOnWriteArrayList<Sign> signClass = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DMob> dMobs = new CopyOnWriteArrayList<>();
    // TODO: Killed mobs
    private CopyOnWriteArrayList<GameChest> gameChests = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<DSign> dSigns = new CopyOnWriteArrayList<>();
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

    /**
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * @param game
     * the game to set
     */
    public void setGame(Game game) {
        this.game = game;
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
     * @return the locLobby
     */
    public Location getLocLobby() {
        return locLobby;
    }

    /**
     * @param locLobby
     * the locLobby to set
     */
    public void setLocLobby(Location locLobby) {
        this.locLobby = locLobby;
    }

    /**
     * @return the locStart
     */
    public Location getLocStart() {
        return locStart;
    }

    /**
     * @param locStart
     * the locStart to set
     */
    public void setLocStart(Location locStart) {
        this.locStart = locStart;
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
    public CopyOnWriteArrayList<Material> getSecureObjects() {
        return secureObjects;
    }

    /**
     * @param secureObjects
     * the secureObjects to set
     */
    public void setSecureObjects(CopyOnWriteArrayList<Material> secureObjects) {
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
     * @return the gameChests
     */
    public CopyOnWriteArrayList<GameChest> getGameChests() {
        return gameChests;
    }

    /**
     * @param gameChests
     * the gameChests to set
     */
    public void setGameChests(CopyOnWriteArrayList<GameChest> gameChests) {
        this.gameChests = gameChests;
    }

    /**
     * @return the dSigns
     */
    public CopyOnWriteArrayList<DSign> getDSigns() {
        return dSigns;
    }

    /**
     * @param dSigns
     * the dSigns to set
     */
    public void setDSigns(CopyOnWriteArrayList<DSign> dSigns) {
        this.dSigns = dSigns;
    }

    /**
     * @return the worldConfig
     */
    public WorldConfig getConfig() {
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
        GameWorldStartGameEvent event = new GameWorldStartGameEvent(this, game);

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
        if (RedstoneTrigger.hasTriggers(this)) {
            for (RedstoneTrigger trigger : RedstoneTrigger.getTriggersArray(this)) {
                trigger.onTrigger();
            }
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
        for (DPlayer dPlayer : DPlayer.getByWorld(world)) {
            MessageUtil.sendMessage(dPlayer.getPlayer(), message);
        }
    }

    public void delete() {
        GameWorldUnloadEvent event = new GameWorldUnloadEvent(this);

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

    // Statics
    public static GameWorld load(String name) {
        GameWorldLoadEvent event = new GameWorldLoadEvent(name);

        if (event.isCancelled()) {
            return null;
        }

        File file = new File(plugin.getDataFolder(), "/maps/" + name);

        if (file.exists()) {
            GameWorld gameWorld = new GameWorld();
            gameWorld.mapName = name;

            // Unload empty editWorlds
            for (EditWorld editWorld : plugin.getEditWorlds()) {
                if (editWorld.getWorld().getPlayers().isEmpty()) {
                    editWorld.delete();
                }
            }

            // Config einlesen
            gameWorld.worldConfig = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + gameWorld.mapName, "config.yml"));

            // Secure Objects
            gameWorld.secureObjects = gameWorld.worldConfig.getSecureObjects();

            // World
            FileUtil.copyDirectory(file, new File("DXL_Game_" + gameWorld.id), DungeonsXL.EXCLUDED_FILES);

            // Id File
            File idFile = new File("DXL_Game_" + gameWorld.id + "/.id_" + name);
            try {
                idFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            gameWorld.world = plugin.getServer().createWorld(WorldCreator.name("DXL_Game_" + gameWorld.id));

            ObjectInputStream os;
            try {
                os = new ObjectInputStream(new FileInputStream(new File(plugin.getDataFolder() + "/maps/" + gameWorld.mapName + "/DXLData.data")));

                int length = os.readInt();
                for (int i = 0; i < length; i++) {
                    int x = os.readInt();
                    int y = os.readInt();
                    int z = os.readInt();
                    Block block = gameWorld.world.getBlockAt(x, y, z);
                    gameWorld.checkSign(block);
                }

                os.close();

            } catch (FileNotFoundException exception) {
                plugin.getLogger().info("Could not find any sign data for the world \"" + name + "\"!");

            } catch (IOException exception) {
                exception.printStackTrace();
            }

            return gameWorld;
        }

        return null;
    }

    public static GameWorld getByWorld(World world) {
        for (GameWorld gameWorld : plugin.getGameWorlds()) {
            if (gameWorld.world.equals(world)) {
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

    public static boolean canPlayDungeon(String map, Player player) {
        if (player.hasPermission("dxl.ignoretimelimit")) {
            return true;
        }

        if (new File(plugin.getDataFolder() + "/maps/" + map).isDirectory()) {
            WorldConfig worldConfig = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + map, "config.yml"));

            if (worldConfig.getTimeToNextPlay() != 0) {
                // read PlayerConfig
                long time = getPlayerTime(map, player);
                if (time != -1) {
                    if (time + worldConfig.getTimeToNextPlay() * 1000 * 60 * 60 > System.currentTimeMillis()) {
                        return false;
                    }
                }
            }

        } else {
            return false;
        }

        return true;
    }

    public static boolean canPlayDungeon(String dungeon, DGroup dGroup) {
        if (dGroup.getCaptain().hasPermission("dxl.ignoretimelimit")) {
            return true;
        }

        for (Player player : dGroup.getPlayers()) {
            if (!canPlayDungeon(dungeon, player)) {
                return false;
            }
        }

        return true;
    }

    public static long getPlayerTime(String dungeon, Player player) {
        File file = new File(plugin.getDataFolder() + "/maps/" + dungeon, "players.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
        if (playerConfig.contains(player.getUniqueId().toString())) {
            return playerConfig.getLong(player.getUniqueId().toString());
        }
        if (playerConfig.contains(player.getName())) {
            return playerConfig.getLong(player.getName());
        }
        return -1;
    }

    public static boolean checkRequirements(String map, Player player) {
        if (player.hasPermission("dxl.ignorerequirements")) {
            return true;
        }

        if (new File(plugin.getDataFolder() + "/maps/" + map).isDirectory() == false) {
            return false;
        }

        WorldConfig worldConfig = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + map, "config.yml"));

        for (Requirement requirement : worldConfig.getRequirements()) {
            RequirementCheckEvent event = new RequirementCheckEvent(requirement, player);
            if (event.isCancelled()) {
                continue;
            }

            if (!requirement.check(player)) {
                return false;
            }
        }

        if (worldConfig.getFinished() != null && worldConfig.getFinishedAll() != null) {
            if (!worldConfig.getFinished().isEmpty()) {

                long bestTime = 0;
                int numOfNeeded = 0;
                boolean doneTheOne = false;

                if (worldConfig.getFinished().size() == worldConfig.getFinishedAll().size()) {
                    doneTheOne = true;
                }

                for (String played : worldConfig.getFinished()) {
                    for (String dungeonName : new File(plugin.getDataFolder() + "/maps").list()) {
                        if (new File(plugin.getDataFolder() + "/maps/" + dungeonName).isDirectory()) {
                            if (played.equalsIgnoreCase(dungeonName) || played.equalsIgnoreCase("any")) {

                                Long time = getPlayerTime(dungeonName, player);
                                if (time != -1) {
                                    if (worldConfig.getFinishedAll().contains(played)) {
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

                } else if (worldConfig.getTimeLastPlayed() != 0) {
                    if (System.currentTimeMillis() - bestTime > worldConfig.getTimeLastPlayed() * (long) 3600000) {
                        return false;
                    }
                }

                if (numOfNeeded < worldConfig.getFinishedAll().size() || !doneTheOne) {
                    return false;
                }

            }
        }
        return true;
    }

    public static boolean checkRequirements(String map, DGroup dGroup) {
        if (dGroup.getCaptain().hasPermission("dxl.ignorerequirements")) {
            return true;
        }

        for (Player player : dGroup.getPlayers()) {
            if (!checkRequirements(map, player)) {
                return false;
            }
        }

        return true;
    }

}
