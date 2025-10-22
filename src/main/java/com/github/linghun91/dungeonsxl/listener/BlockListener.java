package com.github.linghun91.dungeonsxl.listener;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.player.DungeonPlayer;
import com.github.linghun91.dungeonsxl.api.world.EditWorld;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.api.world.InstanceWorld;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;

/**
 * Listener for block events
 */
public class BlockListener implements Listener {
    
    private final DungeonsXL plugin;
    
    public BlockListener(DungeonsXL plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Optional<InstanceWorld> instance = plugin.getWorldManager().getInstance(event.getBlock().getWorld());
        
        if (instance.isEmpty()) return;
        
        if (instance.get() instanceof GameWorld gameWorld) {
            // In game world
            DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
            if (dPlayer == null) {
                event.setCancelled(true);
                return;
            }
            
            // Check if block breaking is allowed
            Boolean allowBreak = gameWorld.getGame().getDungeon().getGameRule("allowBlockBreak", Boolean.class);
            if (allowBreak == null || !allowBreak) {
                event.setCancelled(true);
                MessageUtil.sendError(player, "You cannot break blocks in this dungeon!");
            }
        } else if (instance.get() instanceof EditWorld) {
            // In edit world - allow if player has permission
            if (!player.hasPermission("dungeonsxl.edit")) {
                event.setCancelled(true);
                MessageUtil.sendError(player, "You don't have permission to edit!");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Optional<InstanceWorld> instance = plugin.getWorldManager().getInstance(event.getBlock().getWorld());
        
        if (instance.isEmpty()) return;
        
        if (instance.get() instanceof GameWorld gameWorld) {
            // In game world
            DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
            if (dPlayer == null) {
                event.setCancelled(true);
                return;
            }
            
            // Check if block placing is allowed
            Boolean allowPlace = gameWorld.getGame().getDungeon().getGameRule("allowBlockPlace", Boolean.class);
            if (allowPlace == null || !allowPlace) {
                event.setCancelled(true);
                MessageUtil.sendError(player, "You cannot place blocks in this dungeon!");
            }
        } else if (instance.get() instanceof EditWorld) {
            // In edit world - allow if player has permission
            if (!player.hasPermission("dungeonsxl.edit")) {
                event.setCancelled(true);
                MessageUtil.sendError(player, "You don't have permission to edit!");
            }
        }
    }
}
