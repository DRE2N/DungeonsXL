package com.dre.dungeonsxl.listener;

import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.dre.dungeonsxl.DGSign;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.DPortal;
import com.dre.dungeonsxl.EditWorld;
import com.dre.dungeonsxl.game.DMob;
import com.dre.dungeonsxl.game.GameWorld;

public class EntityListener implements Listener{

	//Remove drops from breaking Signs
	@EventHandler(priority = EventPriority.HIGH)
	public void onItemSpawn(ItemSpawnEvent event){
		if(GameWorld.get(event.getLocation().getWorld()) != null){
			if(event.getEntity().getItemStack().getTypeId()==323){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onCreatureSpawn(CreatureSpawnEvent event){
		World world=event.getLocation().getWorld();
		
		EditWorld eworld = EditWorld.get(world);
		GameWorld gworld = GameWorld.get(world);
		
		if(eworld != null || gworld!=null){
			if(
				event.getSpawnReason() == SpawnReason.CHUNK_GEN ||
				event.getSpawnReason() == SpawnReason.BREEDING ||
				event.getSpawnReason() == SpawnReason.NATURAL ||
				event.getSpawnReason() == SpawnReason.DEFAULT)
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDeath(EntityDeathEvent event){
		World world=event.getEntity().getWorld();
		
		if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
			GameWorld gworld = GameWorld.get(world);
			if(gworld!=null){
				if(gworld.isPlaying){
					if(entity.getType() != EntityType.PLAYER){
						event.getDrops().clear();
						DMob.onDeath(event);
					}
				}
			}
        }
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event){
		World world=event.getEntity().getWorld();
		GameWorld gworld=GameWorld.get(world);
		if(gworld!=null){
			//Deny all Damage in Lobby
			if(!gworld.isPlaying){
				event.setCancelled(true);
			}
			//Deny all Damage from Players to Players
			if (event instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent)event;
				Entity entity = sub.getDamager();
				Entity entity2 = sub.getEntity();
				
				if(entity instanceof Projectile)
				{
					entity = ((Projectile) entity).getShooter();
				}
				
				if(entity instanceof Player && entity2 instanceof Player)
				{
					event.setCancelled(true);
				}
				
				if(entity instanceof LivingEntity && entity2 instanceof LivingEntity){
					if(!(entity instanceof Player) && !(entity2 instanceof Player)){
						event.setCancelled(true);
					}

					//Check Dogs
					if(entity instanceof Player || entity2 instanceof Player){
						for(DPlayer dplayer:DPlayer.get(gworld.world)){
							if(dplayer.wolf!=null){
								if(entity==dplayer.wolf || entity2==dplayer.wolf){
									event.setCancelled(true);
									return;
								}
							}
						}
					}
					
					for(DPlayer dplayer:DPlayer.get(gworld.world)){
						if(dplayer.wolf!=null){
							if(entity instanceof Player || entity2 instanceof Player){
								if(entity==dplayer.wolf || entity2==dplayer.wolf){
									event.setCancelled(true);
									return;
								}
							}else{
								if(entity==dplayer.wolf || entity2==dplayer.wolf){
									event.setCancelled(false);
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	//Deny food in Lobby
	@EventHandler(priority = EventPriority.HIGH)
	public void onFoodLevelChange(FoodLevelChangeEvent event){
		World world=event.getEntity().getWorld();
		
		GameWorld gworld=GameWorld.get(world);
		if(gworld!=null){
			if(!gworld.isPlaying){
				event.setCancelled(true);
			}
		}
	}
	
	// Zombie/skeleton combustion from the sun.
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityCombust (EntityCombustEvent event) {
		GameWorld gworld = GameWorld.get(event.getEntity().getWorld());
		if (gworld != null) {
			event.setCancelled(true);
		}
	}
	
	//Allow Other combustion
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityCombustByEntity(EntityCombustByEntityEvent  event){
		GameWorld gworld=GameWorld.get(event.getEntity().getWorld());
		if(gworld!=null){
			if(event.isCancelled()){
				event.setCancelled(false);
			}
		}
	}
	
	//Explosions
	@EventHandler
	public void onEntityExplode (EntityExplodeEvent event) {
		GameWorld gworld=GameWorld.get(event.getEntity().getWorld());
		
		if(gworld!=null){
			if(event.getEntity() instanceof LivingEntity){
				//Disable Creeper explosions in gameworlds
				event.setCancelled(true);
				return;
			}else{
				//Disable drops from TNT
				event.setYield(0);
			}
			
			
		}
		
		//Prevent Portal and Sign Destroying
		List<Block> blocklist=event.blockList ();
		for(Block block:blocklist){
			//Portals
			if(block.getTypeId()==90){
				if(DPortal.get(block)!=null){
					event.setCancelled(true);
					return;
				}
			}
			
			//Signs
			if(block.getTypeId()==68 || block.getTypeId()==63){
				if(DGSign.getSign(block)!=null){
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
