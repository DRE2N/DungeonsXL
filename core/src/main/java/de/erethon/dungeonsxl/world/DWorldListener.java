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
import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.caliburn.mob.ExMob;
import de.erethon.commons.compatibility.Version;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
import de.erethon.dungeonsxl.api.world.EditWorld;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPlayerListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Daniel Saukel, Frank Baumann, Milan Albrecht
 */
public class DWorldListener implements Listener {

    private DungeonsXL plugin;
    private CaliburnAPI caliburn;

    public DWorldListener(DungeonsXL plugin) {
        this.plugin = plugin;
        caliburn = plugin.getCaliburn();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInit(WorldInitEvent event) {
        World world = event.getWorld();
        if (plugin.isInstance(world)) {
            world.setKeepSpawnInMemory(false);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        // EditWorld Signs
        EditWorld editWorld = plugin.getEditWorld(block.getWorld());
        if (editWorld != null) {
            editWorld.removeDungeonSign(event.getBlock());
            return;
        }

        // Deny GameWorld block breaking
        DGameWorld gameWorld = (DGameWorld) plugin.getGameWorld(block.getWorld());
        if (gameWorld != null) {
            if (gameWorld.onBreak(event)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (DPlayerListener.isCitizensNPC(player)) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        GameWorld gameWorld = plugin.getGameWorld(block.getWorld());
        if (gameWorld == null || gameWorld.isPlaying()) {
            return;
        }

        Map<ExItem, HashSet<ExItem>> blacklist = gameWorld.getDungeon().getRules().getState(GameRule.INTERACTION_BLACKLIST);
        if (blacklist == null) {
            return;
        }

        ExItem material = VanillaItem.get(block.getType());
        ExItem tool = caliburn.getExItem(getItemInHand(event));
        if (blacklist.containsKey(material)
                && (blacklist.get(material) == null
                || blacklist.get(material).isEmpty()
                || blacklist.get(material).contains(tool))) {
            event.setCancelled(true);
        }
    }

    private ItemStack getItemInHand(PlayerInteractEvent event) {
        PlayerInventory inventory = event.getPlayer().getInventory();
        if (Version.isAtLeast(Version.MC1_9)) {
            return event.getHand() == EquipmentSlot.HAND ? inventory.getItemInMainHand() : inventory.getItemInOffHand();
        } else {
            return inventory.getItemInHand();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        DGameWorld gameWorld = (DGameWorld) plugin.getGameWorld(block.getWorld());
        if (gameWorld == null) {
            return;
        }

        if (gameWorld.onPlace(event.getPlayer(), block, event.getBlockAgainst(), event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!plugin.isInstance(event.getBlock().getWorld())) {
            return;
        }

        if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockSpread(BlockSpreadEvent event) {
        Block block = event.getSource();

        if (plugin.isInstance(block.getWorld()) && VanillaItem.VINE.is(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (plugin.getGameWorld(event.getEntity().getWorld()) == null) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity) {
            // Disable Creeper explosions in gameWorlds
            event.setCancelled(true);
        } else {// TODO respect block breaking game rules
            // Disable drops from TNT
            event.setYield(0);
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
        GameWorld gameWorld = plugin.getGameWorld(entity.getWorld());
        if (gameWorld == null) {
            return;
        }
        GameRuleContainer rules = gameWorld.getDungeon().getRules();
        Set<ExMob> prot = interact ? rules.getState(GameRule.INTERACTION_PROTECTED_ENTITIES) : rules.getState(GameRule.DAMAGE_PROTECTED_ENTITIES);
        if (prot.contains(caliburn.getExMob(entity))) {
            event.setCancelled(true);
        }
    }

    // TODO: Is this necessary?
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (plugin.getGameWorld(event.getLocation().getWorld()) != null) {
            if (Category.SIGNS.containsItem(event.getEntity().getItemStack())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        GameWorld gameWorld = plugin.getGameWorld(event.getBlock().getWorld());
        if (gameWorld == null) {
            return;
        }

        if (!gameWorld.isPlaying()) {
            event.setCancelled(true);
            return;
        }

        Set<ExItem> blockFadeDisabled = gameWorld.getGame().getRules().getState(GameRule.BLOCK_FADE_DISABLED);
        if (blockFadeDisabled == null) {
            return;
        }
        if (gameWorld.getGame() != null && blockFadeDisabled.contains(VanillaItem.get(event.getBlock().getType()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        InstanceWorld instance = plugin.getInstanceWorld(event.getWorld());
        if (instance instanceof EditWorld && event.toWeatherState()) {
            event.setCancelled(true);
        } else if (instance instanceof GameWorld) {
            Boolean raining = ((GameWorld) instance).getDungeon().getRules().getState(GameRule.RAIN);
            if (raining == null) {
                return;
            }
            if ((raining && !event.toWeatherState()) || (!raining && event.toWeatherState())) {
                event.setCancelled(true);
            }
        }
    }

}
