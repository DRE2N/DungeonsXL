package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MythicMobsSign extends DSign {
	
	public static String name = "MythicMobs";
	public String buildPermissions = "dxl.sign.mob";
	public boolean onDungeonInit = false;
	
	// Variables
	private String mob;
	private int maxinterval = 1;
	private int interval = 0;
	private int amount = 1;
	private boolean initialized;
	private boolean active;
	private int taskId = -1;
	private Location spawnLoc;
	private LivingEntity mythicMob;
	private ArrayList<Entity> mythicMobs = new ArrayList<Entity>();
	
	public MythicMobsSign(Sign sign, GameWorld gWorld) {
		super(sign, gWorld);
	}
	
	@Override
	public boolean check() {
		String lines[] = getSign().getLines();
		if ( !lines[1].equals("") && !lines[2].equals("")) {
			if (lines[1] != null) {
				String[] atributes = lines[2].split(",");
				if (atributes.length == 2) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void onInit() {
		String lines[] = getSign().getLines();
		if ( !lines[1].equals("") && !lines[2].equals("")) {
			String mob = lines[1];
			if (mob != null) {
				String[] atributes = lines[2].split(",");
				if (atributes.length == 2) {
					this.mob = mob;
					maxinterval = IntegerUtil.parseInt(atributes[0]);
					amount = IntegerUtil.parseInt(atributes[1]);
				}
			}
		}
		getSign().getBlock().setType(Material.AIR);
		
		initialized = true;
	}
	
	@Override
	public void onTrigger() {
		if (initialized && !active) {
			MobSpawnScheduler scheduler = new MobSpawnScheduler(this);
			
			taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, scheduler, 0L, 20L);
			
			active = true;
		}
	}
	
	@Override
	public void onDisable() {
		if (initialized && active) {
			killTask();
			interval = 0;
			active = false;
		}
	}
	
	public void killTask() {
		if (initialized && active) {
			if (taskId != -1) {
				plugin.getServer().getScheduler().cancelTask(taskId);
				taskId = -1;
			}
		}
	}
	
	public class MobSpawnScheduler implements Runnable {
		private MythicMobsSign sign;
		
		public MobSpawnScheduler(MythicMobsSign sign) {
			this.sign = sign;
		}
		
		@Override
		public void run() {
			if (sign.interval <= 0) {
				World world = sign.getSign().getWorld();
				GameWorld gWorld = GameWorld.get(world);
				
				if (gWorld != null) {
					spawnLoc = sign.getSign().getLocation().add(0.5, 0, 0.5);
					double x = spawnLoc.getX();
					double y = spawnLoc.getY();
					double z = spawnLoc.getZ();
					
					String command = "mm mobs spawn " + mob + " " + amount + " DXL_Game_" + gWorld.id + "," + x + "," + y + "," + z;
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
					
					setMythicMobs();
					if (mythicMob != null) {
						new DMob(mythicMob, sign.getGWorld(), null, mob);
					}
					
					// Set the amount
					if (amount != -1) {
						if (amount > 1) {
							amount--;
						} else {
							killTask();
							sign.remove();
						}
					}
					
					sign.interval = sign.maxinterval;
				} else {
					sign.killTask();
				}
			}
			sign.interval--;
		}
	}
	
	@Override
	public String getPermissions() {
		return buildPermissions;
	}
	
	@Override
	public boolean isOnDungeonInit() {
		return onDungeonInit;
	}
	
	private void setMythicMobs() {
		for (Entity entity : spawnLoc.getChunk().getEntities()) {
			if (entity.getLocation().getX() >= spawnLoc.getX() - 1 && entity.getLocation().getX() <= spawnLoc.getX() + 1 && entity.getLocation().getY() >= spawnLoc.getY() - 1
			        && entity.getLocation().getY() <= spawnLoc.getY() + 1 && entity.getLocation().getZ() >= spawnLoc.getZ() - 1 && entity.getLocation().getZ() <= spawnLoc.getZ() + 1
			        && !mythicMobs.contains(entity) && entity.isCustomNameVisible() && !(entity instanceof Player)) {
				mythicMob = (LivingEntity) entity;
				mythicMobs.add(entity);
				org.bukkit.Bukkit.broadcastMessage("[DXL Debug] MythicMob counter: " + mythicMobs.size());
				return;
			}
		}
	}
}
