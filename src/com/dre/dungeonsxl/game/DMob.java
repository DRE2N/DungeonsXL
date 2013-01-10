package com.dre.dungeonsxl.game;

import java.util.Random;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
		
		this.entity=entity;
		this.live=live;
		this.type = type;
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
	
	public static void onDeath(EntityDeathEvent event){
		if(event.getEntity() instanceof LivingEntity){
			LivingEntity victim=(LivingEntity) event.getEntity();
			
			GameWorld gworld=GameWorld.get(victim.getWorld());
			
			if(gworld!=null){
				for(DMob dmob:gworld.dmobs){
					if(dmob.entity==victim){
						if(dmob.type!=null){
							for(ItemStack item:dmob.type.getDrops().keySet()){
								Random randomGenerator = new Random();
								int random = randomGenerator.nextInt(101);
								if(dmob.type.getDrops().get(item)<=random){
									event.getDrops().add(item);
								}
							}
						}
						return;
					}
				}
			}
		}
	}
}
