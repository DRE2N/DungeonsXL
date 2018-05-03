/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package de.erethon.dungeonsxl.world;

import de.erethon.dungeonsxl.game.Game;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * @author Daniel Saukel, Frank Baumann, Milan Albrecht
 */
public class DWorldListener implements Listener {

    DWorldCache dWorlds;

    public DWorldListener(DWorldCache dWorlds) {
        this.dWorlds = dWorlds;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        // DEditWorld Signs
        DEditWorld editWorld = DEditWorld.getByWorld(block.getWorld());
        if (editWorld != null) {
            editWorld.getSigns().remove(event.getBlock());
            return;
        }

        // Deny DGameWorld block breaking
        DGameWorld gameWorld = DGameWorld.getByWorld(block.getWorld());
        if (gameWorld != null) {
            if (gameWorld.onBreak(event)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        DGameWorld gameWorld = DGameWorld.getByWorld(block.getWorld());
        if (gameWorld == null) {
            return;
        }

        if (gameWorld.onPlace(event.getPlayer(), block, event.getBlockAgainst(), event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (dWorlds.getInstanceByWorld(event.getBlock().getWorld()) == null) {
            return;
        }

        if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockSpread(BlockSpreadEvent event) {
        Block block = event.getSource();

        DInstanceWorld instance = dWorlds.getInstanceByWorld(block.getWorld());
        if (instance != null && block.getType() == Material.VINE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        DInstanceWorld instance = dWorlds.getInstanceByWorld(event.getWorld());
        if (instance instanceof DGameWorld) {
            if (((DGameWorld) instance).getLoadedChunks().contains(event.getChunk())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        DGameWorld gameWorld = DGameWorld.getByWorld(event.getEntity().getWorld());

        if (gameWorld != null) {
            if (event.getEntity() instanceof LivingEntity) {
                // Disable Creeper explosions in gameWorlds
                event.setCancelled(true);
            } else {
                // Disable drops from TNT
                event.setYield(0);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        DGameWorld gameWorld = DGameWorld.getByWorld(event.getEntity().getWorld());
        if (gameWorld == null) {
            return;
        }
        Game game = Game.getByGameWorld(gameWorld);
        if (game.getRules().getDamageProtectedEntities().contains(event.getEntityType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        DGameWorld gameWorld = DGameWorld.getByWorld(event.getPlayer().getWorld());
        if (gameWorld == null) {
            return;
        }
        Game game = Game.getByGameWorld(gameWorld);
        if (game.getRules().getInteractionProtectedEntities().contains(event.getRightClicked().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (DGameWorld.getByWorld(event.getLocation().getWorld()) != null) {
            if (event.getEntity().getItemStack().getType() == Material.SIGN) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        DInstanceWorld dWorld = dWorlds.getInstanceByWorld(event.getWorld());
        if (dWorld instanceof DEditWorld && event.toWeatherState()) {
            event.setCancelled(true);
        } else if (dWorld instanceof DGameWorld) {
            Game game = Game.getByGameWorld((DGameWorld) dWorld);
            Boolean raining = game.getRules().isRaining();
            if (raining == null) {
                return;
            }
            if ((raining && !event.toWeatherState()) || (!raining && event.toWeatherState())) {
                event.setCancelled(true);
            }
        }
    }

}
