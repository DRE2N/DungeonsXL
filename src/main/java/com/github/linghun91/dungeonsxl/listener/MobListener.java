package com.github.linghun91.dungeonsxl.listener;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.player.DungeonPlayer;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.api.world.InstanceWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Optional;

/**
 * Listener for mob events
 */
public class MobListener implements Listener {
    
    private final DungeonsXL plugin;
    
    public MobListener(DungeonsXL plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Optional<InstanceWorld> instance = plugin.getWorldManager().getInstance(event.getLocation().getWorld());
        
        if (instance.isEmpty()) return;
        
        if (instance.get() instanceof GameWorld gameWorld) {
            Entity entity = event.getEntity();
            if (entity instanceof LivingEntity living) {
                // Apply mob health multiplier
                Double healthMultiplier = gameWorld.getGame().getDungeon()
                    .getGameRule("mobHealthMultiplier", Double.class);
                if (healthMultiplier != null && healthMultiplier != 1.0) {
                    double newHealth = living.getMaxHealth() * healthMultiplier;
                    living.setMaxHealth(newHealth);
                    living.setHealth(newHealth);
                }
                
                // Track dungeon mob
                plugin.getMobManager().registerDungeonMob(living, gameWorld);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Optional<InstanceWorld> instance = plugin.getWorldManager().getInstance(entity.getWorld());
        
        if (instance.isEmpty()) return;
        
        if (instance.get() instanceof GameWorld gameWorld) {
            // Handle dungeon mob death
            plugin.getMobManager().handleMobDeath(entity, gameWorld);
            
            // Apply experience multiplier
            Double expMultiplier = gameWorld.getGame().getDungeon()
                .getGameRule("expMultiplier", Double.class);
            if (expMultiplier != null) {
                event.setDroppedExp((int) (event.getDroppedExp() * expMultiplier));
            }
            
            // Check if killed by player
            Player killer = event.getEntity().getKiller();
            if (killer != null) {
                DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(killer);
                if (dPlayer != null && dPlayer.getGroup() != null) {
                    // Award points
                    Integer killPoints = gameWorld.getGame().getDungeon()
                        .getGameRule("killPoints", Integer.class);
                    if (killPoints != null) {
                        dPlayer.getGroup().addScore(killPoints);
                    }
                }
            }
            
            // Notify trigger manager
            gameWorld.getGame().getTriggerManager().handleMobKill(entity);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Optional<InstanceWorld> instance = plugin.getWorldManager().getInstance(event.getEntity().getWorld());
        
        if (instance.isEmpty()) return;
        
        if (instance.get() instanceof GameWorld gameWorld) {
            // Handle PvP
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                Boolean allowPvP = gameWorld.getGame().getDungeon().getGameRule("allowPvP", Boolean.class);
                if (allowPvP == null || !allowPvP) {
                    event.setCancelled(true);
                }
            }
            
            // Apply damage multipliers
            if (event.getDamager() instanceof LivingEntity) {
                Double damageMultiplier = gameWorld.getGame().getDungeon()
                    .getGameRule("damageMultiplier", Double.class);
                if (damageMultiplier != null && damageMultiplier != 1.0) {
                    event.setDamage(event.getDamage() * damageMultiplier);
                }
            }
        }
    }
}
