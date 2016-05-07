/*
 * Copyright (C) 2012-2016 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.mob.DMob;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

/**
 * @author Frank Baumann, Tobias Schmitz, Milan Albrecht, Daniel Saukel
 */
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
            switch (event.getSpawnReason()) {
                case CHUNK_GEN:
                case JOCKEY:
                case MOUNT:
                case NATURAL:
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(EntityDeathEvent event) {
        World world = event.getEntity().getWorld();

        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = event.getEntity();
            GameWorld gameWorld = GameWorld.getByWorld(world);
            if (gameWorld != null) {
                if (gameWorld.isPlaying()) {
                    DMob dMob = DMob.getByEntity(entity);
                    if (dMob != null) {
                        event.getDrops().clear();
                        dMob.onDeath(event);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        World world = event.getEntity().getWorld();
        GameWorld gameWorld = GameWorld.getByWorld(world);

        if (gameWorld == null) {
            return;
        }

        // Deny all Damage in Lobby
        if (!gameWorld.isPlaying()) {
            event.setCancelled(true);
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        boolean dead = ((LivingEntity) event.getEntity()).getHealth() - event.getFinalDamage() <= 0;
        if (dead && DMob.getByEntity(event.getEntity()) != null) {
            String killer = null;

            if (event instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) event).getDamager();

                if (damager instanceof Projectile) {
                    if (((Projectile) damager).getShooter() instanceof Player) {
                        damager = (Player) ((Projectile) damager).getShooter();
                    }
                }

                if (damager instanceof Player) {
                    killer = damager.getName();
                }
            }

            gameWorld.getGame().addKill(killer);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        World world = event.getEntity().getWorld();
        GameWorld gameWorld = GameWorld.getByWorld(world);

        if (gameWorld == null) {
            return;
        }

        Game game = gameWorld.getGame();

        if (game == null) {
            return;
        }

        if (!game.hasStarted()) {
            return;
        }

        boolean pvp = game.getRules().isPlayerVersusPlayer();
        boolean friendlyFire = game.getRules().isFriendlyFire();

        Entity attackerEntity = event.getDamager();
        Entity attackedEntity = event.getEntity();

        if (attackerEntity instanceof Projectile) {
            attackerEntity = (Entity) ((Projectile) attackerEntity).getShooter();
        }

        Player attackerPlayer = null;
        Player attackedPlayer = null;

        DGroup attackerDGroup = null;
        DGroup attackedDGroup = null;

        if (attackerEntity instanceof LivingEntity && attackedEntity instanceof LivingEntity) {
            if (attackerEntity instanceof Player && attackedEntity instanceof Player) {
                attackerPlayer = (Player) attackerEntity;
                attackedPlayer = (Player) attackedEntity;

                attackerDGroup = DGroup.getByPlayer(attackerPlayer);
                attackedDGroup = DGroup.getByPlayer(attackedPlayer);

                if (!pvp) {
                    event.setCancelled(true);
                }

                if (attackerDGroup != null && attackedDGroup != null) {
                    if (!friendlyFire && attackerDGroup.equals(attackedDGroup)) {
                        event.setCancelled(true);
                    }
                }
            }

            // Check Dogs
            if (attackerEntity instanceof Player || attackedEntity instanceof Player) {
                for (DGamePlayer dPlayer : DGamePlayer.getByWorld(gameWorld.getWorld())) {
                    if (dPlayer.getWolf() != null) {
                        if (attackerEntity == dPlayer.getWolf() || attackedEntity == dPlayer.getWolf()) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }

            for (DGamePlayer dPlayer : DGamePlayer.getByWorld(gameWorld.getWorld())) {
                if (dPlayer.getWolf() != null) {
                    if (attackerEntity instanceof Player || attackedEntity instanceof Player) {
                        if (attackerEntity == dPlayer.getWolf() || attackedEntity == dPlayer.getWolf()) {
                            event.setCancelled(true);
                            return;
                        }

                    } else if (attackerEntity == dPlayer.getWolf() || attackedEntity == dPlayer.getWolf()) {
                        event.setCancelled(false);
                        return;
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
            if (!gameWorld.isPlaying()) {
                event.setCancelled(true);
            }
        }
    }

    // Zombie/skeleton combustion from the sun.
    @EventHandler(priority = EventPriority.NORMAL)
    public void onCombust(EntityCombustEvent event) {
        GameWorld gameWorld = GameWorld.getByWorld(event.getEntity().getWorld());
        if (gameWorld != null) {
            event.setCancelled(true);
        }
    }

    // Allow Other combustion
    @EventHandler(priority = EventPriority.HIGH)
    public void onCombustByEntity(EntityCombustByEntityEvent event) {
        GameWorld gameWorld = GameWorld.getByWorld(event.getEntity().getWorld());
        if (gameWorld != null) {
            if (event.isCancelled()) {
                event.setCancelled(false);
            }
        }
    }

    // Explosions
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        GameWorld gameWorld = GameWorld.getByWorld(event.getEntity().getWorld());

        if (gameWorld != null) {
            if (event.getEntity() instanceof LivingEntity) {
                // Disable Creeper explosions in gameWorlds
                event.setCancelled(true);
                return;

            } else {
                // Disable drops from TNT
                event.setYield(0);
            }
        }

        // Prevent GlobalProtection destroying
        List<Block> blocklist = event.blockList();
        for (Block block : blocklist) {
            if (DungeonsXL.getInstance().getGlobalProtections().isProtectedBlock(block)) {
                event.setCancelled(true);
            }
        }
    }

}
