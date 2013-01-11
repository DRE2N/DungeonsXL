package com.dre.dungeonsxl.game;

import java.util.Random;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.dre.dungeonsxl.DMobType;

public class DMob {
	
	//Variables
	public LivingEntity entity;
	public DMobType type;
	public int live;
	
	public DMob(LivingEntity entity, int live, GameWorld gworld, DMobType type){
		gworld.dmobs.add(this);
		
		this.entity = entity;
		this.live = live;
		this.type = type;
		
		/* Max Health */
		if(live>0){
			this.entity.setMaxHealth(live);
			this.entity.setHealth(live);
		}
	}
	
	//Statics
	public static void onDeath (EntityDeathEvent event) {
		if (event.getEntity() instanceof LivingEntity) {
			LivingEntity victim = (LivingEntity) event.getEntity();
			GameWorld gworld = GameWorld.get(victim.getWorld());
			
			if (gworld!=null) {
				for (DMob dmob:gworld.dmobs) {
					if (dmob.entity == victim) {
						if (dmob.type != null) {
							for (ItemStack item:dmob.type.getDrops().keySet()) {
								Random randomGenerator = new Random();
								int random = randomGenerator.nextInt(100);
								
								if (dmob.type.getDrops().get(item)>random) {
									event.getDrops().add(item);
								}
							}
						}
						
						gworld.dmobs.remove(dmob);
						return;
					}
				}
			}
		}
	}
}
