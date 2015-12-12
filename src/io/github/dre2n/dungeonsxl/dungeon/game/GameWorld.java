package io.github.dre2n.dungeonsxl.dungeon.game;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.dungeon.DungeonConfig;
import io.github.dre2n.dungeonsxl.dungeon.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.trigger.RedstoneTrigger;
import io.github.dre2n.dungeonsxl.util.FileUtil;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

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

public class GameWorld {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	// Variables placeable
	public boolean isTutorial;
	
	public CopyOnWriteArrayList<GamePlaceableBlock> placeableBlocks = new CopyOnWriteArrayList<GamePlaceableBlock>();
	public World world;
	public String dungeonname;
	public Location locLobby;
	public Location locStart;
	public boolean isPlaying = false;
	public int id;
	public CopyOnWriteArrayList<Material> secureObjects = new CopyOnWriteArrayList<Material>();
	public CopyOnWriteArrayList<Chunk> loadedChunks = new CopyOnWriteArrayList<Chunk>();
	
	public CopyOnWriteArrayList<Sign> signClass = new CopyOnWriteArrayList<Sign>();
	public CopyOnWriteArrayList<DMob> dMobs = new CopyOnWriteArrayList<DMob>();
	public CopyOnWriteArrayList<GameChest> gameChests = new CopyOnWriteArrayList<GameChest>();
	public CopyOnWriteArrayList<DSign> dSigns = new CopyOnWriteArrayList<DSign>();
	private WorldConfig worldConfig;
	
	public GameWorld() {
		plugin.getGameWorlds().add(this);
		
		// ID
		id = -1;
		int i = -1;
		while (id == -1) {
			i++;
			boolean exist = false;
			for (GameWorld gWorld : plugin.getGameWorlds()) {
				if (gWorld.id == i) {
					exist = true;
					break;
				}
			}
			if ( !exist) {
				id = i;
			}
		}
	}
	
	public void checkSign(Block block) {
		if (block.getState() instanceof Sign) {
			Sign sign = (Sign) block.getState();
			dSigns.add(DSign.create(sign, this));
		}
	}
	
	public void startGame() {
		isPlaying = true;
		
		for (DSign dSign : dSigns) {
			if (dSign != null) {
				if ( !dSign.isOnDungeonInit()) {
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
				if ( !dSign.hasTriggers()) {
					dSign.onTrigger();
				}
			}
		}
	}
	
	public void msg(String msg) {
		for (DPlayer dplayer : DPlayer.get(world)) {
			MessageUtil.sendMessage(dplayer.player, msg);
		}
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
			if (dungeonConfig.getFloors().contains(this) || dungeonConfig.getStartFloor().equals(this) || dungeonConfig.getEndFloor().equals(this)) {
				return dungeon;
			}
		}
		
		return null;
	}
	
	// Statics
	
	public static GameWorld get(World world) {
		for (GameWorld gWorld : plugin.getGameWorlds()) {
			if (gWorld.world.equals(world)) {
				return gWorld;
			}
		}
		
		return null;
	}
	
	public static void deleteAll() {
		for (GameWorld gWorld : plugin.getGameWorlds()) {
			gWorld.delete();
		}
	}
	
	public static boolean canPlayDungeon(String dungeon, Player player) {
		
		if (player.hasPermission("dungeonsxl.ignoretimelimit")) {
			return true;
		}
		
		if (new File(plugin.getDataFolder() + "/maps/" + dungeon).isDirectory()) {
			WorldConfig worldConfig = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + dungeon, "config.yml"));
			
			if (worldConfig.getTimeToNextPlay() != 0) {
				// read PlayerConfig
				Long time = getPlayerTime(dungeon, player);
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
	
	public static boolean checkRequirements(String dungeon, Player player) {
		/* if (plugin.permission.has(player, "dungeonsxl.ignoreRequirements") || player.isOp()) {
		 * return true; } */
		
		if (new File(plugin.getDataFolder() + "/maps/" + dungeon).isDirectory() == false) {
			return false;
		}
		
		WorldConfig worldConfig = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + dungeon, "config.yml"));
		
		if (plugin.getMainConfig().enableEconomy()) {
			if ( !(DungeonsXL.getPlugin().economy.getBalance(player) >= worldConfig.getFee())) {
				return false;
			}
		}
		
		if (worldConfig.getFinished() != null && worldConfig.getFinishedAll() != null) {
			if ( !worldConfig.getFinished().isEmpty()) {
				
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
				} else {
					if (worldConfig.getTimeLastPlayed() != 0) {
						if (System.currentTimeMillis() - bestTime > worldConfig.getTimeLastPlayed() * (long) 3600000) {
							return false;
						}
					}
				}
				
				if (numOfNeeded < worldConfig.getFinishedAll().size() || !doneTheOne) {
					return false;
				}
				
			}
		}
		return true;
	}
	
	public static long getPlayerTime(String dungeon, Player player) {
		File file = new File(plugin.getDataFolder() + "/maps/" + dungeon, "players.yml");
		
		if ( !file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
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
	
	public void delete() {
		plugin.getGameWorlds().remove(this);
		plugin.getServer().unloadWorld(world, true);
		File dir = new File("DXL_Game_" + id);
		FileUtil.removeDirectory(dir);
	}
	
	public static GameWorld load(String name) {
		
		File file = new File(plugin.getDataFolder(), "/maps/" + name);
		
		if (file.exists()) {
			GameWorld gWorld = new GameWorld();
			gWorld.dungeonname = name;
			
			// Unload empty eworlds
			for (EditWorld eworld : plugin.getEditWorlds()) {
				if (eworld.world.getPlayers().isEmpty()) {
					eworld.delete();
				}
			}
			
			// Config einlesen
			gWorld.worldConfig = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + gWorld.dungeonname, "config.yml"));
			
			// Secure Objects
			gWorld.secureObjects = gWorld.worldConfig.getSecureObjects();
			
			// World
			FileUtil.copyDirectory(file, new File("DXL_Game_" + gWorld.id));
			
			// Id File
			File idFile = new File("DXL_Game_" + gWorld.id + "/.id_" + name);
			try {
				idFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			gWorld.world = plugin.getServer().createWorld(WorldCreator.name("DXL_Game_" + gWorld.id));
			
			ObjectInputStream os;
			try {
				os = new ObjectInputStream(new FileInputStream(new File(plugin.getDataFolder() + "/maps/" + gWorld.dungeonname + "/DXLData.data")));
				
				int length = os.readInt();
				for (int i = 0; i < length; i++) {
					int x = os.readInt();
					int y = os.readInt();
					int z = os.readInt();
					Block block = gWorld.world.getBlockAt(x, y, z);
					gWorld.checkSign(block);
				}
				
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return gWorld;
		}
		
		return null;
	}
	
	public static void update() {
		for (GameWorld gWorld : plugin.getGameWorlds()) {
			// Update Spiders
			for (LivingEntity mob : gWorld.world.getLivingEntities()) {
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
	}
	
}
