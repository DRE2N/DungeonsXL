package com.dre.dungeonsxl.signs;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;

import com.dre.dungeonsxl.DMobType;
import com.dre.dungeonsxl.game.DMob;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNMob extends DSign {

	public static String name = "Mob";
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

	public SIGNMob(Sign sign, GameWorld gworld) {
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
		sign.getBlock().setTypeId(0);

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
		private SIGNMob sign;

		public MobSpawnScheduler(SIGNMob sign) {
			this.sign = sign;
		}

		@Override
		public void run() {
			if (sign.interval <= 0) {
				World world = sign.sign.getWorld();
				GameWorld gworld = GameWorld.get(world);

				if (gworld != null) {

					// Check normal mobs
					if (EntityType.fromName(sign.mob) != null) {
						if (EntityType.fromName(sign.mob).isAlive()) {
							LivingEntity entity = (LivingEntity) world.spawnEntity(sign.sign.getLocation(), EntityType.fromName(sign.mob));

							// Add Bow to normal Skeletons
							if (entity.getType() == EntityType.SKELETON) {
								Skeleton skeleton = (Skeleton) entity;
								if (skeleton.getSkeletonType() == SkeletonType.NORMAL) {
									skeleton.getEquipment().setItemInHand(new ItemStack(Material.BOW));
								}
							}

							new DMob(entity, sign.gworld, null);
						}
					}

					// Check custom mobs
					DMobType mobType = DMobType.get(sign.mob, gworld.config.getMobTypes());

					if (mobType != null) {
						mobType.spawn(GameWorld.get(world), sign.sign.getLocation());
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
}
