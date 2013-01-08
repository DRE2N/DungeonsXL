package com.dre.dungeonsxl.game;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DMob {
	
	//Variables
	public LivingEntity entity;
	public int live;
	
	public DMob(LivingEntity entity, int live, GameWorld gworld){
		gworld.dmobs.add(this);
		
		this.entity=entity;
		this.live=live;
	}
	
	//Statics
	public static void onDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof LivingEntity){
			LivingEntity victim=(LivingEntity) event.getEntity();
			
			GameWorld gworld=GameWorld.get(victim.getWorld());
			
			if(gworld!=null){
				for(DMob dmob:gworld.dmobs){
					if(dmob.entity==victim){
						dmob.live=dmob.live-event.getDamage();
						dmob.entity.damage(1);
						
						
						dmob.entity.setHealth(dmob.entity.getMaxHealth());
						
						if(event instanceof EntityDamageByEntityEvent){
							EntityDamageByEntityEvent eByEEvent=(EntityDamageByEntityEvent) event;
							if(dmob.entity instanceof Monster && eByEEvent.getDamager() instanceof LivingEntity){
								Monster mob=(Monster)dmob.entity;
								mob.setTarget((LivingEntity) eByEEvent.getDamager());
							}
						}
						
						
						if(dmob.live<=0){
							dmob.entity.damage(dmob.entity.getMaxHealth());
							gworld.dmobs.remove(dmob);
						}
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
