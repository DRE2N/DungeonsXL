package io.github.dre2n.dungeonsxl.global;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.WorldConfig;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class GameSign {
	
	protected static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	// Sign Labels
	public static final String IS_PLAYING = ChatColor.DARK_RED + "Is Playing";
	public static final String FULL = ChatColor.DARK_RED + "Full";
	public static final String JOIN_GROUP = ChatColor.DARK_GREEN + "Join Game";
	public static final String NEW_GROUP = ChatColor.DARK_GREEN + "New Game";
	
	// Variables
	private Game[] games;
	private boolean multiFloor;
	private String dungeonName;
	private String mapName;
	private int maxGroupsPerGame;
	private Block startSign;
	private int directionX = 0, directionZ = 0;
	private int verticalSigns;
	
	public GameSign(Block startSign, String identifier, int maxGames, int maxGroupsPerGame, boolean multiFloor) {
		plugin.getGameSigns().add(this);
		
		this.startSign = startSign;
		games = new Game[maxGames];
		this.setMultiFloor(multiFloor);
		if (multiFloor) {
			dungeonName = identifier;
			Dungeon dungeon = plugin.getDungeons().getDungeon(identifier);
			if (dungeon != null) {
				mapName = dungeon.getConfig().getStartFloor();
			} else {
				mapName = "invalid";
			}
		} else {
			mapName = identifier;
		}
		this.maxGroupsPerGame = maxGroupsPerGame;
		verticalSigns = (int) Math.ceil((float) (1 + maxGroupsPerGame) / 4);
		
		@SuppressWarnings("deprecation")
		int[] direction = getDirection(this.startSign.getData());
		directionX = direction[0];
		directionZ = direction[1];
		
		update();
	}
	
	/**
	 * @return the dungeonName
	 */
	public String getDungeonName() {
		return dungeonName;
	}
	
	/**
	 * @param dungeonName
	 * the dungeonName to set
	 */
	public void setDungeonName(String dungeonName) {
		this.dungeonName = dungeonName;
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
	 * @return the multiFloor
	 */
	public boolean isMultiFloor() {
		return multiFloor;
	}
	
	/**
	 * @param multiFloor
	 * the multiFloor to set
	 */
	public void setMultiFloor(boolean multiFloor) {
		this.multiFloor = multiFloor;
	}
	
	public void delete() {
		plugin.getGameSigns().remove(this);
	}
	
	public void update() {
		int i = 0;
		for (Game game : games) {
			if ( !(startSign.getRelative(i * directionX, 0, i * directionZ).getState() instanceof Sign)) {
				i++;
				continue;
			}
			
			Sign sign = (Sign) startSign.getRelative(i * directionX, 0, i * directionZ).getState();
			
			// Reset Signs
			sign.setLine(0, "");
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
			
			int yy = -1;
			while (sign.getBlock().getRelative(0, yy, 0).getState() instanceof Sign) {
				Sign subsign = (Sign) sign.getBlock().getRelative(0, yy, 0).getState();
				subsign.setLine(0, "");
				subsign.setLine(1, "");
				subsign.setLine(2, "");
				subsign.setLine(3, "");
				subsign.update();
				yy--;
			}
			
			// Set Signs
			if (game != null) {
				if (game.getDGroups().get(0).isPlaying()) {
					sign.setLine(0, IS_PLAYING);
					
				} else if (game.getDGroups().size() >= maxGroupsPerGame) {
					sign.setLine(0, FULL);
					
				} else {
					sign.setLine(0, JOIN_GROUP);
				}
				
				int j = 1;
				Sign rowSign = sign;
				
				for (DGroup dGroup : game.getDGroups()) {
					if (j > 3) {
						j = 0;
						rowSign = (Sign) sign.getBlock().getRelative(0, -1, 0).getState();
					}
					
					if (rowSign != null) {
						rowSign.setLine(j, dGroup.getName());
					}
					
					j++;
					rowSign.update();
				}
				
			} else {
				sign.setLine(0, NEW_GROUP);
			}
			
			sign.update();
			
			i++;
		}
	}
	
	// Static
	
	@SuppressWarnings("deprecation")
	public static GameSign tryToCreate(Block startSign, String mapName, int maxGames, int maxGroupsPerGame, boolean multiFloor) {
		World world = startSign.getWorld();
		int direction = startSign.getData();
		int x = startSign.getX(), y = startSign.getY(), z = startSign.getZ();
		
		int verticalSigns = (int) Math.ceil((float) (1 + maxGroupsPerGame) / 4);
		
		CopyOnWriteArrayList<Block> changeBlocks = new CopyOnWriteArrayList<Block>();
		
		int xx, yy, zz;
		switch (direction) {
			case 2:
				zz = z;
				
				for (yy = y; yy > y - verticalSigns; yy--) {
					for (xx = x; xx > x - maxGames; xx--) {
						Block block = world.getBlockAt(xx, yy, zz);
						
						if (block.getType() != Material.AIR && block.getType() != Material.WALL_SIGN) {
							return null;
						}
						
						if (block.getRelative(0, 0, 1).getType() == Material.AIR) {
							return null;
						}
						
						changeBlocks.add(block);
					}
				}
				
				break;
			
			case 3:
				zz = z;
				for (yy = y; yy > y - verticalSigns; yy--) {
					for (xx = x; xx < x + maxGames; xx++) {
						
						Block block = world.getBlockAt(xx, yy, zz);
						if (block.getType() != Material.AIR && block.getType() != Material.WALL_SIGN) {
							return null;
						}
						
						if (block.getRelative(0, 0, -1).getType() == Material.AIR) {
							return null;
						}
						
						changeBlocks.add(block);
					}
				}
				
				break;
			
			case 4:
				xx = x;
				for (yy = y; yy > y - verticalSigns; yy--) {
					for (zz = z; zz < z + maxGames; zz++) {
						
						Block block = world.getBlockAt(xx, yy, zz);
						if (block.getType() != Material.AIR && block.getType() != Material.WALL_SIGN) {
							return null;
						}
						
						if (block.getRelative(1, 0, 0).getType() == Material.AIR) {
							return null;
						}
						
						changeBlocks.add(block);
					}
				}
				break;
			
			case 5:
				xx = x;
				for (yy = y; yy > y - verticalSigns; yy--) {
					for (zz = z; zz > z - maxGames; zz--) {
						
						Block block = world.getBlockAt(xx, yy, zz);
						if (block.getType() != Material.AIR && block.getType() != Material.WALL_SIGN) {
							return null;
						}
						
						if (block.getRelative( -1, 0, 0).getType() == Material.AIR) {
							return null;
						}
						
						changeBlocks.add(block);
					}
				}
				
				break;
		}
		
		for (Block block : changeBlocks) {
			block.setTypeIdAndData(68, startSign.getData(), true);
		}
		
		GameSign sign = new GameSign(startSign, mapName, maxGames, maxGroupsPerGame, multiFloor);
		
		return sign;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isRelativeSign(Block block, int x, int z) {
		GameSign gameSign = getSign(block.getRelative(x, 0, z));
		if (gameSign == null) {
			return false;
		}
		
		if (x == -1 && gameSign.startSign.getData() == 4) {
			return true;
		}
		
		if (x == 1 && gameSign.startSign.getData() == 5) {
			return true;
		}
		
		if (z == -1 && gameSign.startSign.getData() == 2) {
			return true;
		}
		
		if (z == 1 && gameSign.startSign.getData() == 3) {
			return true;
		}
		
		return false;
	}
	
	public static GameSign getSign(Block block) {
		if ( !(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)) {
			return null;
		}
		
		int x = block.getX(), y = block.getY(), z = block.getZ();
		for (GameSign gameSign : plugin.getGameSigns()) {
			int sx1 = gameSign.startSign.getX(), sy1 = gameSign.startSign.getY(), sz1 = gameSign.startSign.getZ();
			int sx2 = sx1 + (gameSign.games.length - 1) * gameSign.directionX;
			int sy2 = sy1 - gameSign.verticalSigns + 1;
			int sz2 = sz1 + (gameSign.games.length - 1) * gameSign.directionZ;
			
			if (sx1 > sx2) {
				if (x < sx2 || x > sx1) {
					continue;
				}
				
			} else if (sx1 < sx2) {
				if (x > sx2 || x < sx1) {
					continue;
				}
				
			} else {
				if (x != sx1) {
					continue;
				}
			}
			
			if (sy1 > sy2) {
				if (y < sy2 || y > sy1) {
					continue;
				}
				
			} else {
				if (y != sy1) {
					continue;
				}
			}
			
			if (sz1 > sz2) {
				if (z < sz2 || z > sz1) {
					continue;
				}
				
			} else if (sz1 < sz2) {
				if (z > sz2 || z < sz1) {
					continue;
				}
				
			} else {
				if (z != sz1) {
					continue;
				}
			}
			
			return gameSign;
		}
		
		return null;
	}
	
	public static boolean playerInteract(Block block, Player player) {
		int x = block.getX(), y = block.getY(), z = block.getZ();
		GameSign gameSign = getSign(block);
		
		if (gameSign == null) {
			return false;
		}
		
		DGroup dGroup = DGroup.getByPlayer(player);
		
		if ( !dGroup.getCaptain().equals(player)) {
			MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.ERROR_NOT_CAPTAIN));
			return true;
		}
		
		if (Game.getByDGroup(dGroup) != null) {
			MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.ERROR_LEAVE_GAME));
			return true;
		}
		
		if ( !GameWorld.canPlayDungeon(gameSign.mapName, dGroup)) {
			File file = new File(plugin.getDataFolder() + "/maps/" + gameSign.mapName, "config.yml");
			if (file != null) {
				WorldConfig confReader = new WorldConfig(file);
				if (confReader != null) {
					dGroup.sendMessage(plugin.getMessageConfig().getMessage(Messages.ERROR_COOLDOWN, String.valueOf(confReader.getTimeToNextPlay())));
				}
			}
			
			return true;
		}
		
		if ( !GameWorld.checkRequirements(gameSign.mapName, dGroup)) {
			dGroup.sendMessage(plugin.getMessageConfig().getMessage(Messages.ERROR_REQUIREMENTS));
			return true;
		}
		
		int sx1 = gameSign.startSign.getX(), sy1 = gameSign.startSign.getY(), sz1 = gameSign.startSign.getZ();
		
		Block topBlock = block.getRelative(0, sy1 - y, 0);
		
		int column;
		if (gameSign.directionX != 0) {
			column = Math.abs(x - sx1);
			
		} else {
			column = Math.abs(z - sz1);
		}
		
		if ( !(topBlock.getState() instanceof Sign)) {
			return true;
		}
		
		Sign topSign = (Sign) topBlock.getState();
		
		if (topSign.getLine(0).equals(NEW_GROUP)) {
			gameSign.games[column] = new Game(dGroup);
			gameSign.update();
			
		} else if (topSign.getLine(0).equals(JOIN_GROUP)) {
			gameSign.games[column].addDGroup(dGroup);
			gameSign.update();
		}
		
		return true;
	}
	
	public static void updatePerGame(Game gameSearch) {
		
		for (GameSign gameSign : plugin.getGameSigns()) {
			int i = 0;
			for (Game game : gameSign.games) {
				if (game == null) {
					continue;
				}
				
				if (game == gameSearch) {
					if (gameSearch.isEmpty()) {
						gameSign.games[i] = null;
					}
					gameSign.update();
				}
				
				i++;
			}
		}
	}
	
	public static int[] getDirection(byte data) {
		int[] direction = new int[2];
		
		switch (data) {
			case 2:
				direction[0] = -1;
				break;
			
			case 3:
				direction[0] = 1;
				break;
			
			case 4:
				direction[1] = 1;
				break;
			
			case 5:
				direction[1] = -1;
				break;
		}
		return direction;
	}
	
	// Save and Load
	public static void save(FileConfiguration configFile) {
		int id = 0;
		
		for (GameSign gameSign : plugin.getGameSigns()) {
			id++;
			String preString = "gamesign." + gameSign.startSign.getWorld().getName() + "." + id;
			
			// Location
			configFile.set(preString + ".x", gameSign.startSign.getX());
			configFile.set(preString + ".y", gameSign.startSign.getY());
			configFile.set(preString + ".z", gameSign.startSign.getZ());
			
			// Etc.
			if (gameSign.isMultiFloor()) {
				configFile.set(preString + ".dungeon", gameSign.dungeonName);
				
			} else {
				configFile.set(preString + ".dungeon", gameSign.mapName);
			}
			
			configFile.set(preString + ".maxGames", gameSign.games.length);
			configFile.set(preString + ".maxGroupsPerGame", gameSign.maxGroupsPerGame);
			configFile.set(preString + ".multiFloor", gameSign.isMultiFloor());
		}
	}
	
	public static void load(FileConfiguration configFile) {
		for (World world : plugin.getServer().getWorlds()) {
			if ( !configFile.contains("gamesign." + world.getName())) {
				continue;
			}
			
			int id = 0;
			String preString;
			do {
				id++;
				preString = "gamesign." + world.getName() + "." + id + ".";
				if (configFile.contains(preString)) {
					String mapName = configFile.getString(preString + ".dungeon");
					int maxGames = configFile.getInt(preString + ".maxGames");
					int maxGroupsPerGame = configFile.getInt(preString + ".maxGroupsPerGame");
					boolean multiFloor = false;
					if (configFile.contains(preString + ".multiFloor")) {
						multiFloor = configFile.getBoolean(preString + ".multiFloor");
					}
					Block startSign = world.getBlockAt(configFile.getInt(preString + ".x"), configFile.getInt(preString + ".y"), configFile.getInt(preString + ".z"));
					
					new GameSign(startSign, mapName, maxGames, maxGroupsPerGame, multiFloor);
				}
			} while (configFile.contains(preString));
		}
	}
	
}
