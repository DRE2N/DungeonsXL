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
	
	public DMob(LivingEntity entity, GameWorld gworld, DMobType type) {
		gworld.dMobs.add(this);
		
		this.entity = entity;
		this.type = type;
		
		/* Remove DropChance of equipment */
		this.entity.getEquipment().setHelmetDropChance(0);
		this.entity.getEquipment().setChestplateDropChance(0);
		this.entity.getEquipment().setLeggingsDropChance(0);
		this.entity.getEquipment().setBootsDropChance(0);
		this.entity.getEquipment().setItemInHandDropChance(0);
	}
	
	public DMob(LivingEntity entity, GameWorld gworld, DMobType type, String trigger) {
		gworld.dMobs.add(this);
		
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
		if (event.getEntity() instanceof LivingEntity) {
			LivingEntity victim = event.getEntity();
			GameWorld gworld = GameWorld.get(victim.getWorld());
			String name = null;
			
			if (gworld != null) {
				for (DMob dmob : gworld.dMobs) {
					if (dmob.entity == victim) {
						
						if (dmob.type != null) {
							for (ItemStack item : dmob.type.getDrops().keySet()) {
								Random randomGenerator = new Random();
								int random = randomGenerator.nextInt(100);
								
								if (dmob.type.getDrops().get(item) > random) {
									event.getDrops().add(item);
								}
							}
							name = dmob.type.getName();
							
						} else if (dmob.type == null && dmob.trigger != null) {// <=MythicMobs mob
							name = dmob.trigger;
							
						} else {
							name = victim.getType().getName();
						}
						
						MobTrigger trigger = MobTrigger.get(name, gworld);
						if (trigger != null) {
							trigger.onTrigger();
						}
						
						gworld.dMobs.remove(dmob);
						return;
					}
				}
			}
		}
	}
	
}
