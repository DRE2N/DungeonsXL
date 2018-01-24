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
package io.github.dre2n.dungeonsxl.world;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
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

    @EventHandler
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

    @EventHandler
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

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (dWorlds.getInstanceByWorld(event.getBlock().getWorld()) == null) {
            return;
        }

        if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
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

    @EventHandler
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

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        DGameWorld gameWorld = DGameWorld.getByWorld(event.getEntity().getWorld());
        if (gameWorld != null) {
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
        if (dWorlds.getInstanceByWorld(event.getWorld()) != null) {
            if (event.toWeatherState()) {
                event.setCancelled(true);
            }
        }
    }

}
