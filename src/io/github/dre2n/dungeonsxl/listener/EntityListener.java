package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.dungeonsxl.config.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

public class EntityListener implements Listener {
	
	// Remove drops from breaking Signs
	@EventHandler(priority = EventPriority.HIGH)
	public void onItemSpawn(ItemSpawnEvent event) {
		if (GameWorld.getByWorld(event.getLocation().getWorld()) != null) {
			if (event.getEntity().getItemStack().getType() == Material.SIGN) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		World world = event.getLocation().getWorld();
		
		EditWorld editWorld = EditWorld.getByWorld(world);
		GameWorld gameWorld = GameWorld.getByWorld(world);
		
		if (editWorld != null || gameWorld != null) {
			if (event.getSpawnReason() == SpawnReason.CHUNK_GEN || event.getSpawnReason() == SpawnReason.BREEDING || event.getSpawnReason() == SpawnReason.NATURAL
			        || event.getSpawnReason() == SpawnReason.DEFAULT) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDeath(EntityDeathEvent event) {
		World world = event.getEntity().getWorld();
		
		if (event.getEntity() instanceof LivingEntity) {
			LivingEntity entity = event.getEntity();
			GameWorld gameWorld = GameWorld.getByWorld(world);
			if (gameWorld != null) {
				if (gameWorld.isPlaying()) {
					if (entity.getType() != EntityType.PLAYER) {
						event.getDrops().clear();
						DMob.onDeath(event);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		World world = event.getEntity().getWorld();
		GameWorld gameWorld = GameWorld.getByWorld(world);
		
		if (gameWorld == null) {
			return;
		}
		
		WorldConfig config = gameWorld.getConfig();
		
		// Deny all Damage in Lobby
		if ( !gameWorld.isPlaying()) {
			event.setCancelled(true);
		}
		
		// Deny all Damage from Players to Players
		if ( !(event instanceof EntityDamageByEntityEvent)) {
			return;
		}
		
		EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;
		Entity attackerEntity = sub.getDamager();
		Entity attackedEntity = sub.getEntity();
		
		if (attackerEntity instanceof Projectile) {
			attackerEntity = (Entity) ((Projectile) attackerEntity).getShooter();
		}
		
		Player attackerPlayer = null;
		Player attackedPlayer = null;
		
		DGroup attackerDGroup = null;
		DGroup attackedDGroup = null;
		
		if (attackerEntity instanceof Player && attackedEntity instanceof Player) {
			attackerPlayer = (Player) attackerEntity;
			attackedPlayer = (Player) attackedEntity;
			
			attackerDGroup = DGroup.getByPlayer(attackerPlayer);
			attackedDGroup = DGroup.getByPlayer(attackedPlayer);
			
			if (config.isPlayerVersusPlayer()) {
				Bukkit.broadcastMessage("pvp cancel");
				event.setCancelled(true);
			}
			
			if (attackerDGroup != null && attackedDGroup != null) {
				if (config.isFriendlyFire() && attackerDGroup.equals(attackedDGroup)) {
					Bukkit.broadcastMessage("ff cancel");
					event.setCancelled(true);
				}
			}
		}
		
		if (attackerEntity instanceof LivingEntity && attackedEntity instanceof LivingEntity) {
			if ( !(attackerEntity instanceof Player) && !(attackedEntity instanceof Player)) {
				event.setCancelled(true);
			}
			
			// Check Dogs
			if (attackerEntity instanceof Player || attackedEntity instanceof Player) {
				for (DPlayer dPlayer : DPlayer.getByWorld(gameWorld.getWorld())) {
					if (dPlayer.getWolf() != null) {
						if (attackerEntity == dPlayer.getWolf() || attackedEntity == dPlayer.getWolf()) {
							event.setCancelled(true);
							return;
						}
					}
				}
			}
			
			for (DPlayer dPlayer : DPlayer.getByWorld(gameWorld.getWorld())) {
				if (dPlayer.getWolf() != null) {
					if (attackerEntity instanceof Player || attackedEntity instanceof Player) {
						if (attackerEntity == dPlayer.getWolf() || attackedEntity == dPlayer.getWolf()) {
							event.setCancelled(true);
							return;
						}
						
					} else {
						if (attackerEntity == dPlayer.getWolf() || attackedEntity == dPlayer.getWolf()) {
							event.setCancelled(false);
							return;
						}
					}
				}
			}
		}
	}
	
	// Deny food in Lobby
	@EventHandler(priority = EventPriority.HIGH)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		World world = event.getEntity().getWorld();
		
		GameWorld gameWorld = GameWorld.getByWorld(world);
		if (gameWorld != null) {
			if ( !gameWorld.isPlaying()) {
				event.setCancelled(true);
			}
		}
	}
	
	// Zombie/skeleton combustion from the sun.
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityCombust(EntityCombustEvent event) {
		GameWorld gameWorld = GameWorld.getByWorld(event.getEntity().getWorld());
		if (gameWorld != null) {
			event.setCancelled(true);
		}
	}
	
	// Allow Other combustion
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
		GameWorld gameWorld = GameWorld.getByWorld(event.getEntity().getWorld());
		if (gameWorld != null) {
			if (event.isCancelled()) {
				event.setCancelled(false);
			}
		}
	}
	
	// Explosions
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		GameWorld gameWorld = GameWorld.getByWorld(event.getEntity().getWorld());
		
		if (gameWorld != null) {
			if (event.getEntity() instanceof LivingEntity) {
				// Disable Creeper explosions in gameditWorlds
				event.setCancelled(true);
				return;
				
			} else {
				// Disable drops from TNT
				event.setYield(0);
			}
			
		}
		
		// Prevent Portal and Sign Destroying
		List<Block> blocklist = event.blockList();
		for (Block block : blocklist) {
			// Portals
			if (block.getType() == Material.PORTAL) {
				if (DPortal.getByBlock(block) != null) {
					event.setCancelled(true);
					return;
				}
			}
			
			// Signs
			if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
				if (GroupSign.getSign(block) != null) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
