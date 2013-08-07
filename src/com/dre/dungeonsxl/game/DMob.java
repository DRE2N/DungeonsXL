package com.dre.dungeonsxl.game;

import java.util.Random;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.dre.dungeonsxl.DMobType;
import com.dre.dungeonsxl.trigger.MobTrigger;

public class DMob {

	// Variables
	public LivingEntity entity;
	public DMobType type;

	public DMob(LivingEntity entity, GameWorld gworld, DMobType type) {
		gworld.dmobs.add(this);

		this.entity = entity;
		this.type = type;

		/* Remove DropChance of equipment */
		this.entity.getEquipment().setHelmetDropChance(0);
		this.entity.getEquipment().setChestplateDropChance(0);
		this.entity.getEquipment().setLeggingsDropChance(0);
		this.entity.getEquipment().setBootsDropChance(0);
		this.entity.getEquipment().setItemInHandDropChance(0);
	}

	// Statics
	public static void onDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof LivingEntity) {
			LivingEntity victim = (LivingEntity) event.getEntity();
			GameWorld gworld = GameWorld.get(victim.getWorld());
			String name = null;

			if (gworld != null) {
				for (DMob dmob : gworld.dmobs) {
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
						} else {
							name = victim.getType().getName();
						}

						MobTrigger trigger = MobTrigger.get(name, gworld);
						if (trigger != null) {
							trigger.onTrigger();
						}

						gworld.dmobs.remove(dmob);
						return;
					}
				}
			}
		}
	}
}
