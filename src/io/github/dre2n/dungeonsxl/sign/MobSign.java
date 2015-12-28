package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.mob.DMobType;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;

public class MobSign extends DSign {
	
	private DSignType type = DSignTypeDefault.MOB;
	
	// Variables
	private String mob;
	private int maxinterval = 1;
	private int interval = 0;
	private int amount = 1;
	private boolean initialized;
	private boolean active;
	private int taskId = -1;
	
	public MobSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	@Override
	public boolean check() {
		String lines[] = getSign().getLines();
		if (lines[1].equals("") || lines[2].equals("")) {
			return false;
		}
		
		if (lines[1] == null) {
			return false;
		}
		
		String[] atributes = lines[2].split(",");
		if (atributes.length == 2) {
			return true;
			
		} else {
			return false;
		}
	}
	
	@Override
	public void onInit() {
		String lines[] = getSign().getLines();
		if ( !lines[1].equals("") && !lines[2].equals("")) {
			String mob = lines[1];
			if (mob != null) {
				String[] attributes = lines[2].split(",");
				if (attributes.length == 2) {
					this.mob = mob;
					maxinterval = IntegerUtil.parseInt(attributes[0]);
					amount = IntegerUtil.parseInt(attributes[1]);
				}
			}
		}
		getSign().getBlock().setType(Material.AIR);
		
		initialized = true;
	}
	
	@Override
	public void onTrigger() {
		if ( !initialized || active) {
			return;
		}
		
		MobSpawnScheduler scheduler = new MobSpawnScheduler(this);
		
		taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, scheduler, 0L, 20L);
		
		active = true;
	}
	
	@Override
	public void onDisable() {
		if ( !initialized || !active) {
			return;
		}
		
		killTask();
		interval = 0;
		active = false;
	}
	
	public void killTask() {
		if ( !initialized || !active) {
			return;
		}
		
		if (taskId != -1) {
			plugin.getServer().getScheduler().cancelTask(taskId);
			taskId = -1;
		}
	}
	
	public class MobSpawnScheduler implements Runnable {
		private MobSign sign;
		
		public MobSpawnScheduler(MobSign sign) {
			this.sign = sign;
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			if (sign.interval <= 0) {
				World world = sign.getSign().getWorld();
				GameWorld gameWorld = GameWorld.getByWorld(world);
				
				if (gameWorld != null) {
					Location spawnLoc = sign.getSign().getLocation().add(0.5, 0, 0.5);
					
					// Check normal mobs
					if (EntityType.fromName(sign.mob) != null) {
						if (EntityType.fromName(sign.mob).isAlive()) {
							LivingEntity entity = (LivingEntity) world.spawnEntity(spawnLoc, EntityType.fromName(sign.mob));
							
							// Add Bow to normal Skeletons
							if (entity.getType() == EntityType.SKELETON) {
								Skeleton skeleton = (Skeleton) entity;
								if (skeleton.getSkeletonType() == SkeletonType.NORMAL) {
									skeleton.getEquipment().setItemInHand(new ItemStack(Material.BOW));
								}
							}
							
							// Disable Despawning
							entity.setRemoveWhenFarAway(false);
							
							new DMob(entity, sign.getGameWorld(), null);
						}
					}
					
					// Check custom mobs
					DMobType mobType = DMobType.getByName(sign.mob, gameWorld.getConfig().getMobTypes());
					
					if (mobType != null) {
						mobType.spawn(GameWorld.getByWorld(world), spawnLoc);
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
	public DSignType getType() {
		return type;
	}
	
}
