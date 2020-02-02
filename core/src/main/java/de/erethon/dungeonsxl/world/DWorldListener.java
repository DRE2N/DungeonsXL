/*
 * Copyright (C) 2012-2020 Frank Baumann
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

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.category.Category;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.caliburn.mob.ExMob;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.game.Game;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
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
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldInitEvent;

/**
 * @author Daniel Saukel, Frank Baumann, Milan Albrecht
 */
public class DWorldListener implements Listener {

    private CaliburnAPI caliburn;
    private DWorldCache dWorlds;

    public DWorldListener(DungeonsXL plugin) {
        caliburn = plugin.getCaliburn();
        dWorlds = plugin.getDWorldCache();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInit(WorldInitEvent event) {
        World world = event.getWorld();
        if (dWorlds.isInstance(world)) {
            world.setKeepSpawnInMemory(false);
        }
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
        if (instance != null && VanillaItem.VINE.is(block)) {
            event.setCancelled(true);
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
        onTouch(event, event.getEntity(), false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onHangingBreak(HangingBreakEvent event) {
        onTouch(event, event.getEntity(), false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        onTouch(event, event.getRightClicked(), true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        onTouch(event, event.getRightClicked(), true);
    }

    /**
     * @param event    the event
     * @param entity   the entity
     * @param interact true = interact; false = break
     */
    public void onTouch(Cancellable event, Entity entity, boolean interact) {
        DGameWorld gameWorld = DGameWorld.getByWorld(entity.getWorld());
        if (gameWorld == null) {
            return;
        }
        Game game = Game.getByGameWorld(gameWorld);
        if (game == null) {
            return;
        }
        Set<ExMob> prot = interact ? game.getRules().getInteractionProtectedEntities() : game.getRules().getDamageProtectedEntities();
        if (prot.contains(caliburn.getExMob(entity))) {
            event.setCancelled(true);
        }
    }

    // TODO: Is this necessary?
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (DGameWorld.getByWorld(event.getLocation().getWorld()) != null) {
            if (Category.SIGNS.containsItem(event.getEntity().getItemStack())) {
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
            if (game == null) {
                return;
            }
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
