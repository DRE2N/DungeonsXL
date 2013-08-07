package com.dre.dungeonsxl.game;

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

import com.dre.dungeonsxl.DConfig;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.signs.DSign;
import com.dre.dungeonsxl.EditWorld;
import com.dre.dungeonsxl.trigger.RedstoneTrigger;

public class GameWorld {
	private static P p = P.p;
	public static CopyOnWriteArrayList<GameWorld> gworlds = new CopyOnWriteArrayList<GameWorld>();

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
	public CopyOnWriteArrayList<DMob> dmobs = new CopyOnWriteArrayList<DMob>();
	public CopyOnWriteArrayList<GameChest> gchests = new CopyOnWriteArrayList<GameChest>();
	public CopyOnWriteArrayList<DSign> dSigns = new CopyOnWriteArrayList<DSign>();
	public DConfig config;

	public GameWorld() {
		gworlds.add(this);

		// ID
		this.id = -1;
		int i = -1;
		while (this.id == -1) {
			i++;
			boolean exist = false;
			for (GameWorld gworld : gworlds) {
				if (gworld.id == i) {
					exist = true;
					break;
				}
			}
			if (!exist)
				this.id = i;
		}
	}

	public void checkSign(Block block) {
		if ((block.getState() instanceof Sign)) {
			Sign sign = (Sign) block.getState();
			dSigns.add(DSign.create(sign, this));
		}
	}

	public void startGame() {
		this.isPlaying = true;

		for (DSign dSign : this.dSigns) {
			if (dSign != null) {
				if (!dSign.isOnDungeonInit()) {
					dSign.onInit();
				}
			}
		}
		if (RedstoneTrigger.hasTriggers(this)) {
			for (RedstoneTrigger trigger : RedstoneTrigger.getTriggersArray(this)) {
				trigger.onTrigger();
			}
		}
		for (DSign dSign : this.dSigns) {
			if (dSign != null) {
				if (!dSign.hasTriggers()) {
					dSign.onTrigger();
				}
			}
		}
	}

	public void msg(String msg) {
		for (DPlayer dplayer : DPlayer.get(this.world)) {
			p.msg(dplayer.player, msg);
		}
	}

	// Static
	public static GameWorld get(World world) {
		for (GameWorld gworld : gworlds) {
			if (gworld.world.equals(world)) {
				return gworld;
			}
		}

		return null;
	}

	public static void deleteAll() {
		for (GameWorld gworld : gworlds) {
			gworld.delete();
		}
	}

	public static boolean canPlayDungeon(String dungeon, Player player) {

		if (p.permission.has(player, "dungeonsxl.ignoretimelimit") || player.isOp()) {
			return true;
		}

		if (new File(p.getDataFolder() + "/dungeons/" + dungeon).isDirectory()) {
			DConfig config = new DConfig(new File(p.getDataFolder() + "/dungeons/" + dungeon, "config.yml"));

			if (config.getTimeToNextPlay() != 0) {
				// read PlayerConfig
				Long time = getPlayerTime(dungeon, player.getName());
				if (time != -1) {
					if (time + (config.getTimeToNextPlay() * 1000 * 60 * 60) > System.currentTimeMillis()) {
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
		/*if (p.permission.has(player, "dungeonsxl.ignoreRequirements") || player.isOp()) {
			return true;
		}*/

		if (new File(p.getDataFolder() + "/dungeons/" + dungeon).isDirectory() == false) {
			return false;
		}

		DConfig config = new DConfig(new File(p.getDataFolder() + "/dungeons/" + dungeon, "config.yml"));
		if (config.getFinished() != null && config.getFinishedAll() != null) {
			if (!config.getFinished().isEmpty()) {

				long bestTime = 0;
				int numOfNeeded = 0;
				boolean doneTheOne = false;

				if (config.getFinished().size() == config.getFinishedAll().size()) {
					doneTheOne = true;
				}

				for (String played : config.getFinished()) {
					for (String dungeonName : new File(p.getDataFolder() + "/dungeons").list()) {
						if (new File(p.getDataFolder() + "/dungeons/" + dungeonName).isDirectory()) {
							if (played.equalsIgnoreCase(dungeonName) || played.equalsIgnoreCase("any")) {

								Long time = getPlayerTime(dungeonName, player.getName());
								if (time != -1) {
									if (config.getFinishedAll().contains(played)) {
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
					if (config.getTimeLastPlayed() != 0) {
						if (System.currentTimeMillis() - bestTime > config.getTimeLastPlayed() * (long) 3600000) {
							return false;
						}
					}
				}

				if (numOfNeeded < config.getFinishedAll().size() || !doneTheOne) {
					return false;
				}

			}
		}
		return true;
	}

	public static long getPlayerTime(String dungeon, String name) {
		File file = new File(p.getDataFolder() + "/dungeons/" + dungeon, "players.yml");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
		if (playerConfig.contains(name)) {
			return playerConfig.getLong(name);
		}
		return -1;
	}

	public void delete() {
		gworlds.remove(this);
		p.getServer().unloadWorld(this.world, true);
		File dir = new File("DXL_Game_" + this.id);
		p.removeDirectory(dir);
	}

	public static GameWorld load(String name) {

		File file = new File(p.getDataFolder(), "/dungeons/" + name);

		if (file.exists()) {
			GameWorld gworld = new GameWorld();
			gworld.dungeonname = name;

			// Unload empty eworlds
			for (EditWorld eworld : EditWorld.eworlds) {
				if (eworld.world.getPlayers().isEmpty()) {
					eworld.delete();
				}
			}

			// Config einlesen
			gworld.config = new DConfig(new File(p.getDataFolder() + "/dungeons/" + gworld.dungeonname, "config.yml"));

			// Secure Objects
			gworld.secureObjects = gworld.config.getSecureObjects();

			// World
			p.copyDirectory(file, new File("DXL_Game_" + gworld.id));

			// Id File
			File idFile = new File("DXL_Game_" + gworld.id + "/.id_" + name);
			try {
				idFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			gworld.world = p.getServer().createWorld(WorldCreator.name("DXL_Game_" + gworld.id));

			ObjectInputStream os;
			try {
				os = new ObjectInputStream(new FileInputStream(new File(p.getDataFolder() + "/dungeons/" + gworld.dungeonname + "/DXLData.data")));

				int length = os.readInt();
				for (int i = 0; i < length; i++) {
					int x = os.readInt();
					int y = os.readInt();
					int z = os.readInt();
					Block block = gworld.world.getBlockAt(x, y, z);
					gworld.checkSign(block);
				}

				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return gworld;
		}

		return null;
	}

	public static void update() {
		for (GameWorld gworld : gworlds) {
			// Update Spiders
			for (LivingEntity mob : gworld.world.getLivingEntities()) {
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
