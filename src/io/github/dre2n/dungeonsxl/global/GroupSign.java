package io.github.dre2n.dungeonsxl.global;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.dungeon.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class GroupSign {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	// Sign Labels
	public static final String strIsPlaying = ChatColor.DARK_RED + "Is Playing";
	public static final String strFull = ChatColor.DARK_RED + "Full";
	public static final String strJoinGrp = ChatColor.DARK_GREEN + "Join Group";
	public static final String strNewGrp = ChatColor.DARK_GREEN + "New Group";
	
	// Variables
	private DGroup[] dgroups;
	private boolean multiFloor;
	private String dungeonName;
	private String mapName;
	private int maxPlayersPerGroup;
	private Block startSign;
	private int directionX = 0, directionZ = 0;
	private int verticalSigns;
	
	public GroupSign(Block startSign, String identifier, int maxGroups, int maxPlayersPerGroup, boolean multiFloor) {
		plugin.getGroupSigns().add(this);
		
		this.startSign = startSign;
		dgroups = new DGroup[maxGroups];
		this.multiFloor = multiFloor;
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
		this.maxPlayersPerGroup = maxPlayersPerGroup;
		verticalSigns = (int) Math.ceil((float) (1 + maxPlayersPerGroup) / 4);
		
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
	
	public void update() {
		int i = 0;
		for (DGroup dgroup : dgroups) {
			if (startSign.getRelative(i * directionX, 0, i * directionZ).getState() instanceof Sign) {
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
				if (dgroup != null) {
					if (dgroup.isPlaying()) {
						sign.setLine(0, strIsPlaying);
					} else if (dgroup.getPlayers().size() >= maxPlayersPerGroup) {
						sign.setLine(0, strFull);
					} else {
						sign.setLine(0, strJoinGrp);
					}
					int j = 1;
					Sign rowSign = sign;
					for (Player player : dgroup.getPlayers()) {
						if (j > 3) {
							j = 0;
							rowSign = (Sign) sign.getBlock().getRelative(0, -1, 0).getState();
						}
						if (rowSign != null) {
							rowSign.setLine(j, player.getName());
						}
						j++;
						rowSign.update();
					}
				} else {
					sign.setLine(0, strNewGrp);
				}
				sign.update();
			}
			i++;
		}
	}
	
	// Static
	
	@SuppressWarnings("deprecation")
	public static GroupSign tryToCreate(Block startSign, String mapName, int maxGroups, int maxPlayersPerGroup, boolean multiFloor) {
		World world = startSign.getWorld();
		int direction = startSign.getData();
		int x = startSign.getX(), y = startSign.getY(), z = startSign.getZ();
		
		int verticalSigns = (int) Math.ceil((float) (1 + maxPlayersPerGroup) / 4);
		
		CopyOnWriteArrayList<Block> changeBlocks = new CopyOnWriteArrayList<Block>();
		
		int xx, yy, zz;
		switch (direction) {
			case 2:
				zz = z;
				for (yy = y; yy > y - verticalSigns; yy--) {
					for (xx = x; xx > x - maxGroups; xx--) {
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
					for (xx = x; xx < x + maxGroups; xx++) {
						
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
					for (zz = z; zz < z + maxGroups; zz++) {
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
					for (zz = z; zz > z - maxGroups; zz--) {
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
		GroupSign sign = new GroupSign(startSign, mapName, maxGroups, maxPlayersPerGroup, multiFloor);
		
		return sign;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isRelativeSign(Block block, int x, int z) {
		GroupSign dgsign = getSign(block.getRelative(x, 0, z));
		if (dgsign != null) {
			if (x == -1 && dgsign.startSign.getData() == 4) {
				return true;
			}
			if (x == 1 && dgsign.startSign.getData() == 5) {
				return true;
			}
			if (z == -1 && dgsign.startSign.getData() == 2) {
				return true;
			}
			if (z == 1 && dgsign.startSign.getData() == 3) {
				return true;
			}
		}
		
		return false;
	}
	
	public static GroupSign getSign(Block block) {
		if (block.getType() == Material.WALL_SIGN) {
			int x = block.getX(), y = block.getY(), z = block.getZ();
			for (GroupSign dgsign : plugin.getGroupSigns()) {
				int sx1 = dgsign.startSign.getX(), sy1 = dgsign.startSign.getY(), sz1 = dgsign.startSign.getZ();
				int sx2 = sx1 + (dgsign.dgroups.length - 1) * dgsign.directionX;
				int sy2 = sy1 - dgsign.verticalSigns + 1;
				int sz2 = sz1 + (dgsign.dgroups.length - 1) * dgsign.directionZ;
				
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
				
				return dgsign;
			}
		}
		return null;
	}
	
	public static boolean playerInteract(Block block, Player player) {
		int x = block.getX(), y = block.getY(), z = block.getZ();
		GroupSign dgsign = getSign(block);
		if (dgsign != null) {
			if (GameWorld.canPlayDungeon(dgsign.mapName, player)) {
				if (GameWorld.checkRequirements(dgsign.mapName, player)) {
					int sx1 = dgsign.startSign.getX(), sy1 = dgsign.startSign.getY(), sz1 = dgsign.startSign.getZ();
					
					Block topBlock = block.getRelative(0, sy1 - y, 0);
					
					int column;
					if (dgsign.directionX != 0) {
						column = Math.abs(x - sx1);
					} else {
						column = Math.abs(z - sz1);
					}
					
					if (topBlock.getState() instanceof Sign) {
						Sign topSign = (Sign) topBlock.getState();
						if (topSign.getLine(0).equals(strNewGrp)) {
							if (DGroup.get(player) == null) {
								dgsign.dgroups[column] = new DGroup(player, dgsign.mapName, dgsign.multiFloor);
								dgsign.update();
							}
							
						} else if (topSign.getLine(0).equals(strJoinGrp)) {
							if (DGroup.get(player) == null) {
								dgsign.dgroups[column].addPlayer(player);
								dgsign.update();
							}
						}
						
					}
					
				} else {
					MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().get("Error_Requirements"));
				}
				
			} else {
				File file = new File(DungeonsXL.getPlugin().getDataFolder() + "/maps/" + dgsign.mapName, "config.yml");
				if (file != null) {
					WorldConfig confReader = new WorldConfig(file);
					if (confReader != null) {
						MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().get("Error_Cooldown", "" + confReader.getTimeToNextPlay()));
					}
				}
			}
			return true;
		}
		
		return false;
	}
	
	public static void updatePerGroup(DGroup dgroupsearch) {
		
		for (GroupSign dgsign : plugin.getGroupSigns()) {
			int i = 0;
			for (DGroup dgroup : dgsign.dgroups) {
				if (dgroup != null) {
					if (dgroup == dgroupsearch) {
						if (dgroupsearch.isEmpty()) {
							dgsign.dgroups[i] = null;
						}
						dgsign.update();
					}
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
		
		for (GroupSign dgsign : plugin.getGroupSigns()) {
			id++;
			String preString = "groupsign." + dgsign.startSign.getWorld().getName() + "." + id;
			
			// Location
			configFile.set(preString + ".x", dgsign.startSign.getX());
			configFile.set(preString + ".y", dgsign.startSign.getY());
			configFile.set(preString + ".z", dgsign.startSign.getZ());
			
			// Etc.
			if (dgsign.multiFloor) {
				configFile.set(preString + ".dungeon", dgsign.dungeonName);
			} else {
				configFile.set(preString + ".dungeon", dgsign.mapName);
			}
			configFile.set(preString + ".maxGroups", dgsign.dgroups.length);
			configFile.set(preString + ".maxPlayersPerGroup", dgsign.maxPlayersPerGroup);
			configFile.set(preString + ".multiFloor", dgsign.multiFloor);
		}
	}
	
	public static void load(FileConfiguration configFile) {
		for (World world : DungeonsXL.getPlugin().getServer().getWorlds()) {
			if (configFile.contains("groupsign." + world.getName())) {
				int id = 0;
				String preString;
				do {
					id++;
					preString = "groupsign." + world.getName() + "." + id + ".";
					if (configFile.contains(preString)) {
						String mapName = configFile.getString(preString + ".dungeon");
						int maxGroups = configFile.getInt(preString + ".maxGroups");
						int maxPlayersPerGroup = configFile.getInt(preString + ".maxPlayersPerGroup");
						boolean multiFloor = false;
						if (configFile.contains("multiFloor")) {
							multiFloor = configFile.getBoolean("multiFloor");
						}
						Block startSign = world.getBlockAt(configFile.getInt(preString + ".x"), configFile.getInt(preString + ".y"), configFile.getInt(preString + ".z"));
						
						new GroupSign(startSign, mapName, maxGroups, maxPlayersPerGroup, multiFloor);
					}
				} while (configFile.contains(preString));
			}
		}
		
	}
	
}
