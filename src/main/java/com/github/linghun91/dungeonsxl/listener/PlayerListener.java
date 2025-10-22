package com.github.linghun91.dungeonsxl.listener;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.player.DungeonPlayer;
import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.api.world.InstanceWorld;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.Optional;

/**
 * Listener for player events in dungeons
 */
public class PlayerListener implements Listener {
    
    private final DungeonsXL plugin;
    
    public PlayerListener(DungeonsXL plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check if player was in a dungeon before disconnecting
        DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
        if (dPlayer != null && dPlayer.isInDungeon()) {
            // Handle reconnection logic
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                MessageUtil.send(player, "&eYou were in a dungeon. Reconnecting...");
                // Could teleport back or remove from dungeon depending on rules
            }, 20L);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
        
        if (dPlayer != null && dPlayer.isInDungeon()) {
            // Save player state
            plugin.getPlayerManager().savePlayerData(dPlayer);
            
            // Check if should remove from dungeon
            GameWorld world = dPlayer.getGameWorld();
            if (world != null) {
                Boolean removeOnLogout = world.getGame().getDungeon().getGameRule("removeOnLogout", Boolean.class);
                if (removeOnLogout != null && removeOnLogout) {
                    dPlayer.leave();
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
        
        if (dPlayer == null || !dPlayer.isInDungeon()) return;
        
        GameWorld world = dPlayer.getGameWorld();
        if (world == null) return;
        
        // Handle death according to game rules
        Boolean keepInventory = world.getGame().getDungeon().getGameRule("keepInventoryOnDeath", Boolean.class);
        if (keepInventory != null && keepInventory) {
            event.setKeepInventory(true);
            event.getDrops().clear();
        }
        
        // Handle lives
        if (dPlayer.getGroup() != null) {
            dPlayer.getGroup().removeLife();
            int lives = dPlayer.getGroup().getLives();
            
            if (lives <= 0) {
                // Game over
                world.getGame().sendMessage("&cGroup " + dPlayer.getGroup().getColor().name() + " has run out of lives!");
                world.getGame().end(false);
            } else {
                world.getGame().sendMessage("&e" + player.getName() + " died! Lives remaining: " + lives);
            }
        }
        
        // Set respawn location to checkpoint or bed
        Location respawnLoc = dPlayer.getCheckpoint();
        if (respawnLoc == null) {
            respawnLoc = world.getStartLocation();
        }
        
        if (respawnLoc != null) {
            final Location finalLoc = respawnLoc;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                player.spigot().respawn();
                player.teleport(finalLoc);
            }, 1L);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
        
        if (dPlayer == null || !dPlayer.isInDungeon()) return;
        
        Location respawnLoc = dPlayer.getCheckpoint();
        if (respawnLoc == null && dPlayer.getGameWorld() != null) {
            respawnLoc = dPlayer.getGameWorld().getStartLocation();
        }
        
        if (respawnLoc != null) {
            event.setRespawnLocation(respawnLoc);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
        
        if (dPlayer == null || !dPlayer.isInDungeon()) return;
        
        // Prevent teleporting out of dungeon unless allowed
        Optional<InstanceWorld> fromWorld = plugin.getWorldManager().getInstance(event.getFrom().getWorld());
        Optional<InstanceWorld> toWorld = plugin.getWorldManager().getInstance(event.getTo().getWorld());
        
        if (fromWorld.isPresent() && !toWorld.isPresent()) {
            // Trying to teleport out
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
                Boolean allowTeleport = dPlayer.getGameWorld().getGame().getDungeon()
                    .getGameRule("allowTeleportOut", Boolean.class);
                if (allowTeleport == null || !allowTeleport) {
                    event.setCancelled(true);
                    MessageUtil.sendError(player, "You cannot teleport out of the dungeon!");
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
        
        if (dPlayer == null || !dPlayer.isInDungeon()) return;
        
        GameWorld world = dPlayer.getGameWorld();
        if (world == null) return;
        
        Boolean globalChat = world.getGame().getDungeon().getGameRule("globalChat", Boolean.class);
        if (globalChat != null && !globalChat) {
            // Restrict chat to dungeon players only
            event.setCancelled(true);
            
            String format = String.format("[%s] <%s> %s",
                dPlayer.getGroup() != null ? dPlayer.getGroup().getColor().getChatColor() + dPlayer.getGroup().getColor().name() : "Solo",
                player.getName(),
                event.getMessage()
            );
            
            world.getGame().getPlayers().forEach(dp -> {
                if (dp.getPlayer() != null && dp.getPlayer().isOnline()) {
                    dp.getPlayer().sendMessage(format);
                }
            });
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
        
        if (dPlayer == null || !dPlayer.isInDungeon()) return;
        
        if (event.getClickedBlock() != null) {
            // Handle sign interactions
            if (event.getClickedBlock().getState() instanceof org.bukkit.block.Sign) {
                plugin.getSignManager().handleSignClick(dPlayer, event.getClickedBlock());
            }
            
            // Handle trigger interactions
            GameWorld world = dPlayer.getGameWorld();
            if (world != null) {
                world.getGame().getTriggerManager().handleInteraction(dPlayer, event.getClickedBlock());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
        
        if (dPlayer == null || !dPlayer.isInDungeon()) return;
        
        GameWorld world = dPlayer.getGameWorld();
        if (world != null) {
            Boolean allowDropItems = world.getGame().getDungeon().getGameRule("allowDropItems", Boolean.class);
            if (allowDropItems != null && !allowDropItems) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
        
        if (dPlayer == null || !dPlayer.isInDungeon()) return;
        
        // Could add logic for special dungeon items here
    }
}
