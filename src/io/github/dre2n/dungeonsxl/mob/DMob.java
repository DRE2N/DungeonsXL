package io.github.dre2n.dungeonsxl.mob;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.trigger.MobTrigger;

import java.util.Random;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DMob {
	
	// Variables
	private LivingEntity entity;
	private DMobType type;
	
	private String trigger;
	
	public DMob(LivingEntity entity, GameWorld gameWorld, DMobType type) {
		gameWorld.getDMobs().add(this);
		
		this.entity = entity;
		this.type = type;
		
		/* Remove DropChance of equipment */
		this.entity.getEquipment().setHelmetDropChance(0);
		this.entity.getEquipment().setChestplateDropChance(0);
		this.entity.getEquipment().setLeggingsDropChance(0);
		this.entity.getEquipment().setBootsDropChance(0);
		this.entity.getEquipment().setItemInHandDropChance(0);
	}
	
	public DMob(LivingEntity entity, GameWorld gameWorld, DMobType type, String trigger) {
		gameWorld.getDMobs().add(this);
		
		this.entity = entity;
		this.type = type;
		this.trigger = trigger;
		
		/* Remove DropChance of equipment */
		this.entity.getEquipment().setHelmetDropChance(0);
		this.entity.getEquipment().setChestplateDropChance(0);
		this.entity.getEquipment().setLeggingsDropChance(0);
		this.entity.getEquipment().setBootsDropChance(0);
		this.entity.getEquipment().setItemInHandDropChance(0);
	}
	
	// Statics
	@SuppressWarnings("deprecation")
	public static void onDeath(EntityDeathEvent event) {
		if ( !(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		
		LivingEntity victim = event.getEntity();
		GameWorld gameWorld = GameWorld.getByWorld(victim.getWorld());
		String name = null;
		
		if (gameWorld == null) {
			return;
		}
		
		for (DMob dMob : gameWorld.getDMobs()) {
			if (dMob.entity == victim) {
				
				if (dMob.type != null) {
					for (ItemStack itemStack : dMob.type.getDrops().keySet()) {
						Random randomGenerator = new Random();
						int random = randomGenerator.nextInt(100);
						
						if (dMob.type.getDrops().get(itemStack) > random) {
							event.getDrops().add(itemStack);
						}
					}
					name = dMob.type.getName();
					
				} else if (dMob.type == null && dMob.trigger != null) {// <=MythicMobs mob
					name = dMob.trigger;
					
				} else {
					name = victim.getType().getName();
				}
				
				MobTrigger trigger = MobTrigger.get(name, gameWorld);
				if (trigger != null) {
					trigger.onTrigger();
				}
				
				gameWorld.getDMobs().remove(dMob);
				return;
			}
		}
	}
	
}
