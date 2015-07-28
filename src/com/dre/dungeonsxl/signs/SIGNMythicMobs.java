package com.dre.dungeonsxl.signs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.Location;

import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNMythicMobs extends DSign {

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

	public SIGNMythicMobs(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		String lines[] = sign.getLines();
		if (!lines[1].equals("") && !lines[2].equals("")) {
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
		String lines[] = sign.getLines();
		if (!lines[1].equals("") && !lines[2].equals("")) {
			String mob = lines[1];
			if (mob != null) {
				String[] atributes = lines[2].split(",");
				if (atributes.length == 2) {
					this.mob = mob;
					this.maxinterval = p.parseInt(atributes[0]);
					this.amount = p.parseInt(atributes[1]);
				}
			}
		}
		sign.getBlock().setType(Material.AIR);

		initialized = true;
	}

	@Override
	public void onTrigger() {
		if (initialized && !active) {
			MobSpawnScheduler scheduler = new MobSpawnScheduler(this);

			taskId = p.getServer().getScheduler().scheduleSyncRepeatingTask(p, scheduler, 0L, 20L);

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
				p.getServer().getScheduler().cancelTask(taskId);
				taskId = -1;
			}
		}
	}

	public class MobSpawnScheduler implements Runnable {
		private SIGNMythicMobs sign;

		public MobSpawnScheduler(SIGNMythicMobs sign) {
			this.sign = sign;
		}

		@Override
		public void run() {
			if (sign.interval <= 0) {
				World world = sign.sign.getWorld();
				GameWorld gworld = GameWorld.get(world);

				if (gworld != null) {
					Location spawnLoc = sign.sign.getLocation().add(0.5, 0, 0.5);
					double x = spawnLoc.getX();
					double y = spawnLoc.getY();
					double z = spawnLoc.getZ();

					String command = "mm mobs spawn " + mob + " " + amount + " DXL_Game_" + gworld.id + ","+ x + "," + y + "," + z;
					
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);

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
}
