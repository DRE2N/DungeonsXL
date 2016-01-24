package io.github.dre2n.dungeonsxl.task;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.mob.DMobType;
import io.github.dre2n.dungeonsxl.sign.MobSign;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MobSpawnTask extends BukkitRunnable {
	
	private MobSign sign;
	
	public MobSpawnTask(MobSign sign) {
		this.sign = sign;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if (sign.getInterval() <= 0) {
			World world = sign.getSign().getWorld();
			GameWorld gameWorld = GameWorld.getByWorld(world);
			
			if (gameWorld != null) {
				Location spawnLoc = sign.getSign().getLocation().add(0.5, 0, 0.5);
				
				// Check normal mobs
				if (EntityType.fromName(sign.getMob()) != null) {
					if (EntityType.fromName(sign.getMob()).isAlive()) {
						LivingEntity entity = (LivingEntity) world.spawnEntity(spawnLoc, EntityType.fromName(sign.getMob()));
						
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
				DMobType mobType = DMobType.getByName(sign.getMob(), gameWorld.getConfig().getMobTypes());
				
				if (mobType != null) {
					mobType.spawn(GameWorld.getByWorld(world), spawnLoc);
				}
				
				// Set the amount
				if (sign.getAmount() != -1) {
					if (sign.getAmount() > 1) {
						sign.setAmount(sign.getAmount() - 1);
					} else {
						sign.killTask();
						sign.remove();
					}
				}
				
				sign.setInterval(sign.getMaxinterval());
			} else {
				sign.killTask();
			}
		}
		sign.setInterval(sign.getInterval() - 1);
	}
	
}
