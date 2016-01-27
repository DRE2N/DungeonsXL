package io.github.dre2n.dungeonsxl.global;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DPortal {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	private World world;
	private Block block1;
	private Block block2;
	private boolean active;
	private Player player;
	
	public DPortal(boolean active) {
		plugin.getDPortals().add(this);
		this.active = active;
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
	 * @return the block1
	 */
	public Block getBlock1() {
		return block1;
	}
	
	/**
	 * @param block1
	 * the block1 to set
	 */
	public void setBlock1(Block block1) {
		this.block1 = block1;
	}
	
	/**
	 * @return the block2
	 */
	public Block getBlock2() {
		return block2;
	}
	
	/**
	 * @param block2
	 * the block2 to set
	 */
	public void setBlock2(Block block2) {
		this.block2 = block2;
	}
	
	/**
	 * @return if the portal is active
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * @param active
	 * set the DPortal active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * @param player
	 * the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void create() {
		player = null;
		
		if (block1 == null || block2 == null) {
			plugin.getDPortals().remove(this);
			return;
		}
		
		int x1 = block1.getX(), y1 = block1.getY(), z1 = block1.getZ();
		int x2 = block2.getX(), y2 = block2.getY(), z2 = block2.getZ();
		int xcount = 0, ycount = 0, zcount = 0;
		
		if (x1 > x2) {
			xcount = -1;
		} else if (x1 < x2) {
			xcount = 1;
		}
		if (y1 > y2) {
			ycount = -1;
		} else if (y1 < y2) {
			ycount = 1;
		}
		if (z1 > z2) {
			zcount = -1;
		} else if (z1 < z2) {
			zcount = 1;
		}
		
		int xx = x1;
		do {
			int yy = y1;
			
			do {
				int zz = z1;
				
				do {
					Material type = world.getBlockAt(xx, yy, zz).getType();
					if (type == Material.AIR || type == Material.WATER || type == Material.STATIONARY_WATER || type == Material.LAVA || type == Material.STATIONARY_LAVA || type == Material.SAPLING
					        || type == Material.WEB || type == Material.LONG_GRASS || type == Material.DEAD_BUSH || type == Material.PISTON_EXTENSION || type == Material.YELLOW_FLOWER
					        || type == Material.RED_ROSE || type == Material.BROWN_MUSHROOM || type == Material.RED_MUSHROOM || type == Material.TORCH || type == Material.FIRE
					        || type == Material.CROPS || type == Material.REDSTONE_WIRE || type == Material.REDSTONE_TORCH_OFF || type == Material.SNOW || type == Material.REDSTONE_TORCH_ON) {
						world.getBlockAt(xx, yy, zz).setType(plugin.getMainConfig().useWaterPortal() ? Material.STATIONARY_WATER : Material.PORTAL);
					}
					
					zz = zz + zcount;
				} while (zz != z2 + zcount);
				
				yy = yy + ycount;
			} while (yy != y2 + ycount);
			
			xx = xx + xcount;
		} while (xx != x2 + xcount);
	}
	
	public void teleport(Player player) {
		DGroup dgroup = DGroup.getByPlayer(player);
		
		if (dgroup == null) {
			MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.ERROR_JOIN_GROUP));
			return;
		}
		
		if (dgroup.getGameWorld() == null) {
			dgroup.setGameWorld(GameWorld.load(DGroup.getByPlayer(player).getMapName()));
		}
		
		if (dgroup.getGameWorld() == null) {
			MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.ERROR_DUNGEON_NOT_EXIST, DGroup.getByPlayer(player).getMapName()));
			return;
		}
		
		if (dgroup.getGameWorld().getLocLobby() == null) {
			new DPlayer(player, dgroup.getGameWorld().getWorld(), dgroup.getGameWorld().getWorld().getSpawnLocation(), false);
			
		} else {
			new DPlayer(player, dgroup.getGameWorld().getWorld(), dgroup.getGameWorld().getLocLobby(), false);
		}
	}
	
	public void delete() {
		plugin.getDPortals().remove(this);
		
		int x1 = block1.getX(), y1 = block1.getY(), z1 = block1.getZ();
		int x2 = block2.getX(), y2 = block2.getY(), z2 = block2.getZ();
		int xcount = 0, ycount = 0, zcount = 0;
		
		if (x1 > x2) {
			xcount = -1;
		} else if (x1 < x2) {
			xcount = 1;
		}
		
		if (y1 > y2) {
			ycount = -1;
		} else if (y1 < y2) {
			ycount = 1;
		}
		
		if (z1 > z2) {
			zcount = -1;
		} else if (z1 < z2) {
			zcount = 1;
		}
		
		int xx = x1;
		do {
			int yy = y1;
			do {
				int zz = z1;
				do {
					Material type = world.getBlockAt(xx, yy, zz).getType();
					
					if (type == (plugin.getMainConfig().useWaterPortal() ? Material.STATIONARY_WATER : Material.PORTAL)) {
						world.getBlockAt(xx, yy, zz).setType(Material.AIR);
					}
					
					zz = zz + zcount;
				} while (zz != z2 + zcount);
				
				yy = yy + ycount;
			} while (yy != y2 + ycount);
			
			xx = xx + xcount;
		} while (xx != x2 + xcount);
	}
	
	// Statics
	public static DPortal getByLocation(Location location) {
		return getByBlock(location.getBlock());
	}
	
	public static DPortal getByBlock(Block block) {
		for (DPortal portal : plugin.getDPortals()) {
			int x1 = portal.block1.getX(), y1 = portal.block1.getY(), z1 = portal.block1.getZ();
			int x2 = portal.block2.getX(), y2 = portal.block2.getY(), z2 = portal.block2.getZ();
			int x3 = block.getX(), y3 = block.getY(), z3 = block.getZ();
			
			if (x1 > x2) {
				if (x3 < x2 || x3 > x1) {
					continue;
				}
				
			} else {
				if (x3 > x2 || x3 < x1) {
					continue;
				}
			}
			
			if (y1 > y2) {
				if (y3 < y2 || y3 > y1) {
					continue;
				}
				
			} else {
				if (y3 > y2 || y3 < y1) {
					continue;
				}
			}
			
			if (z1 > z2) {
				if (z3 < z2 || z3 > z1) {
					continue;
				}
			} else {
				if (z3 > z2 || z3 < z1) {
					continue;
				}
			}
			
			return portal;
		}
		
		return null;
	}
	
	public static DPortal getByPlayer(Player player) {
		for (DPortal portal : plugin.getDPortals()) {
			if (portal.player == player) {
				return portal;
			}
		}
		
		return null;
	}
	
	// Save and Load
	public static void save(FileConfiguration configFile) {
		int id = 0;
		for (DPortal dPortal : plugin.getDPortals()) {
			id++;
			
			if ( !dPortal.active) {
				continue;
			}
			
			String preString = "portal." + dPortal.world.getName() + "." + id;
			// Location1
			configFile.set(preString + ".loc1.x", dPortal.block1.getX());
			configFile.set(preString + ".loc1.y", dPortal.block1.getY());
			configFile.set(preString + ".loc1.z", dPortal.block1.getZ());
			// Location1
			configFile.set(preString + ".loc2.x", dPortal.block2.getX());
			configFile.set(preString + ".loc2.y", dPortal.block2.getY());
			configFile.set(preString + ".loc2.z", dPortal.block2.getZ());
		}
	}
	
	public static void load(FileConfiguration configFile) {
		for (World world : plugin.getServer().getWorlds()) {
			if ( !configFile.contains("portal." + world.getName())) {
				return;
			}
			
			int id = 0;
			String preString;
			do {
				id++;
				preString = "portal." + world.getName() + "." + id + ".";
				
				if (configFile.contains(preString)) {
					DPortal dPortal = new DPortal(true);
					dPortal.world = world;
					dPortal.block1 = world.getBlockAt(configFile.getInt(preString + "loc1.x"), configFile.getInt(preString + "loc1.y"), configFile.getInt(preString + "loc1.z"));
					dPortal.block2 = world.getBlockAt(configFile.getInt(preString + "loc2.x"), configFile.getInt(preString + "loc2.y"), configFile.getInt(preString + "loc2.z"));
					dPortal.create();
				}
				
			} while (configFile.contains(preString));
		}
	}
	
}
