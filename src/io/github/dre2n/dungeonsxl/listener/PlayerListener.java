package io.github.dre2n.dungeonsxl.listener;

import java.util.ArrayList;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.DLootInventory;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GameChest;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupCreateEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerDeathEvent;
import io.github.dre2n.dungeonsxl.file.DMessages;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.global.LeaveSign;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import io.github.dre2n.dungeonsxl.trigger.UseItemTrigger;
import io.github.dre2n.dungeonsxl.util.MiscUtil;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	DMessages dMessages = plugin.getDMessages();
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		final DPlayer dPlayer = DPlayer.getByPlayer(player);
		
		GameWorld gameWorld = GameWorld.getByWorld(player.getLocation().getWorld());
		if (gameWorld == null) {
			return;
		}
		
		WorldConfig dConfig = gameWorld.getConfig();
		
		if (dPlayer == null) {
			return;
		}
		
		DPlayerDeathEvent dPlayerDeathEvent = new DPlayerDeathEvent(dPlayer, event, 1);
		
		if (dPlayerDeathEvent.isCancelled()) {
			return;
		}
		
		dPlayer.setLives(dPlayer.getLives() - dPlayerDeathEvent.getLostLives());
		
		if (dPlayer.getLives() == 0 && dPlayer.isReady()) {
			MessageUtil.broadcastMessage(dMessages.getMessage(Messages.PLAYER_DEATH_KICK, player.getName()));
			
			// TODO: This Runnable is a workaround for a bug I couldn't find, yet...
			new BukkitRunnable() {
				public void run() {
					dPlayer.leave();
				}
			}.runTaskLater(plugin, 1L);
			
		} else if ( !(dPlayer.getLives() == -1)) {
			MessageUtil.sendMessage(player, dMessages.getMessage(Messages.PLAYER_DEATH, String.valueOf(dPlayer.getLives())));
			
		} else if (dConfig != null) {
			if (dConfig.getKeepInventoryOnDeath()) {
				dPlayer.setRespawnInventory(event.getEntity().getInventory().getContents());
				dPlayer.setRespawnArmor(event.getEntity().getInventory().getArmorContents());
				// Delete all drops
				for (ItemStack istack : event.getDrops()) {
					istack.setType(Material.AIR);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block clickedBlock = event.getClickedBlock();
		
		if (plugin.getInBreakMode().contains(player)) {
			return;
		}
		
		if (clickedBlock != null) {
			// Block Enderchests
			if (GameWorld.getByWorld(player.getWorld()) != null || EditWorld.getByWorld(player.getWorld()) != null) {
				if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
					if (clickedBlock.getType() == Material.ENDER_CHEST) {
						if ( !player.hasPermission("dxl.bypass")) {
							MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_ENDERCHEST));
							event.setCancelled(true);
						}
						
					} else if (clickedBlock.getType() == Material.BED_BLOCK) {
						if ( !player.hasPermission("dxl.bypass")) {
							MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_BED));
							event.setCancelled(true);
						}
					}
				}
			}
			
			// Block Dispensers
			if (GameWorld.getByWorld(player.getWorld()) != null) {
				if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
					if (clickedBlock.getType() == Material.DISPENSER) {
						if ( !player.hasPermission("dxl.bypass")) {
							MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_DISPENSER));
							event.setCancelled(true);
						}
					}
				}
			}
		}
		
		// Check Portals
		if (event.getItem() != null) {
			ItemStack item = event.getItem();
			if (item.getType() == Material.WOOD_SWORD) {
				if (clickedBlock != null) {
					for (DPortal dportal : plugin.getDPortals()) {
						if ( !dportal.isActive()) {
							if (dportal.getPlayer() == player) {
								if (dportal.getBlock1() == null) {
									dportal.setBlock1(event.getClickedBlock());
									MessageUtil.sendMessage(player, dMessages.getMessage(Messages.PLAYER_PORTAL_PROGRESS));
									
								} else if (dportal.getBlock2() == null) {
									dportal.setBlock2(event.getClickedBlock());
									dportal.setActive(true);
									dportal.create();
									MessageUtil.sendMessage(player, dMessages.getMessage(Messages.PLAYER_PORTAL_CREATED));
								}
								event.setCancelled(true);
							}
						}
					}
				}
			}
			
			// Copy/Paste a Sign and Block-info
			if (EditWorld.getByWorld(player.getWorld()) != null) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (item.getType() == Material.STICK) {
						DPlayer dPlayer = DPlayer.getByPlayer(player);
						if (dPlayer != null) {
							dPlayer.poke(clickedBlock);
							event.setCancelled(true);
						}
					}
				}
			}
			
			// Trigger UseItem Signs
			GameWorld gameWorld = GameWorld.getByWorld(player.getWorld());
			if (gameWorld != null) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
					if (UseItemTrigger.hasTriggers(gameWorld)) {
						String name = null;
						if (item.hasItemMeta()) {
							if (item.getItemMeta().hasDisplayName()) {
								name = item.getItemMeta().getDisplayName();
								
							} else if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.BOOK_AND_QUILL) {
								if (item.getItemMeta() instanceof BookMeta) {
									BookMeta meta = (BookMeta) item.getItemMeta();
									if (meta.hasTitle()) {
										name = meta.getTitle();
									}
								}
							}
						}
						if (name == null) {
							name = item.getType().toString();
						}
						UseItemTrigger trigger = UseItemTrigger.get(name, gameWorld);
						if (trigger != null) {
							trigger.onTrigger(player);
						}
					}
				}
			}
		}
		
		// Check Signs
		if (clickedBlock != null) {
			
			if (clickedBlock.getType() == Material.WALL_SIGN || clickedBlock.getType() == Material.SIGN_POST) {
				// Check Group Signs
				if (GroupSign.playerInteract(event.getClickedBlock(), player)) {
					event.setCancelled(true);
				}
				
				// Leave Sign
				if (LeaveSign.playerInteract(event.getClickedBlock(), player)) {
					event.setCancelled(true);
				}
				
				DPlayer dPlayer = DPlayer.getByPlayer(player);
				if (dPlayer != null) {
					
					// Check GameWorld Signs
					GameWorld gameWorld = GameWorld.getByWorld(player.getWorld());
					if (gameWorld != null) {
						
						// Trigger InteractTrigger
						InteractTrigger trigger = InteractTrigger.get(clickedBlock, gameWorld);
						if (trigger != null) {
							if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
								trigger.onTrigger(player);
							} else {
								MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_LEFT_CLICK));
							}
						}
						
						// Class Signs
						for (Sign classSign : gameWorld.getSignClass()) {
							if (classSign != null) {
								if (classSign.getLocation().distance(clickedBlock.getLocation()) < 1) {
									if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
										dPlayer.setDClass(ChatColor.stripColor(classSign.getLine(1)));
									} else {
										MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_LEFT_CLICK));
									}
									return;
								}
							}
						}
					}
				}
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		// Deny dropping things at the lobby
		DGroup dGroup = DGroup.getByPlayer(player);
		if (dGroup == null) {
			return;
		}
		
		if ( !dGroup.isPlaying()) {
			event.setCancelled(true);
			return;
		}
		
		if ( !DPlayer.getByPlayer(player).isReady()) {
			event.setCancelled(true);
			return;
		}
		
		DPlayer dPlayer = DPlayer.getByPlayer(player);
		GameWorld gameWorld = GameWorld.getByWorld(dPlayer.getWorld());
		
		if (dPlayer != null) {
			for (Material material : gameWorld.getConfig().getSecureObjects()) {
				if (material == event.getItemDrop().getItemStack().getType()) {
					event.setCancelled(true);
					MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_DROP));
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		DPlayer dPlayer = DPlayer.getByPlayer(player);
		
		if (dPlayer == null) {
			return;
		}
		
		if (dPlayer.isEditing()) {
			EditWorld editWorld = EditWorld.getByWorld(dPlayer.getWorld());
			if (editWorld == null) {
				return;
			}
			
			if (editWorld.getLobby() == null) {
				event.setRespawnLocation(editWorld.getWorld().getSpawnLocation());
				
			} else {
				event.setRespawnLocation(editWorld.getLobby());
			}
			
		} else {
			GameWorld gameWorld = GameWorld.getByWorld(dPlayer.getWorld());
			
			if (gameWorld == null) {
				return;
			}
			
			DGroup dGroup = DGroup.getByPlayer(dPlayer.getPlayer());
			
			if (dPlayer.getCheckpoint() == null) {
				event.setRespawnLocation(dGroup.getGameWorld().getLocStart());
				
				// Da einige Plugins einen anderen Respawn setzen wird
				// ein Scheduler gestartet der den Player nach einer
				// Sekunde teleportiert.
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new RespawnRunnable(player, dGroup.getGameWorld().getLocStart()), 10);
				
				if (dPlayer.getWolf() != null) {
					dPlayer.getWolf().teleport(dGroup.getGameWorld().getLocStart());
				}
				
			} else {
				event.setRespawnLocation(dPlayer.getCheckpoint());
				
				// Da einige Plugins einen anderen Respawn setzen wird
				// ein Scheduler gestartet der den Player nach einer
				// Sekunde teleportiert.
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new RespawnRunnable(player, dPlayer.getCheckpoint()), 10);
				
				if (dPlayer.getWolf() != null) {
					dPlayer.getWolf().teleport(dPlayer.getCheckpoint());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPortal(PlayerPortalEvent event) {
		Player player = event.getPlayer();
		Location location = event.getFrom();
		DPortal dportal = DPortal.getByLocation(location);
		
		if (dportal != null) {
			event.setCancelled(true);
			dportal.teleport(player);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		DPlayer dPlayer = DPlayer.getByPlayer(player);
		
		if (dPlayer == null) {
			return;
		}
		
		if (dPlayer.getWorld() == event.getTo().getWorld()) {
			return;
		}
		
		if ( !player.hasPermission("dxl.bypass")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		DPlayer dPlayer = DPlayer.getByPlayer(player);
		if (dPlayer == null) {
			return;
		}
		
		if (dPlayer.isInDungeonChat()) {
			dPlayer.sendMessage(player.getDisplayName() + ": " + event.getMessage());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		DPlayer dPlayer = DPlayer.getByPlayer(player);
		
		if (dPlayer == null) {
			return;
		}
		
		DGroup dGroup = DGroup.getByPlayer(player);
		
		// Check GameWorld
		GameWorld gameWorld = GameWorld.getByWorld(player.getWorld());
		if (gameWorld != null) {
			int timeUntilKickOfflinePlayer = gameWorld.getConfig().getTimeUntilKickOfflinePlayer();
			
			if (timeUntilKickOfflinePlayer == 0) {
				dPlayer.leave();
				
			} else if (timeUntilKickOfflinePlayer > 0) {
				dGroup.sendMessage(dMessages.getMessage(Messages.PLAYER_OFFLINE, dPlayer.getPlayer().getName(), "" + timeUntilKickOfflinePlayer), player);
				dPlayer.setOfflineTime(System.currentTimeMillis() + timeUntilKickOfflinePlayer * 1000);
				
			} else {
				dGroup.sendMessage(dMessages.getMessage(Messages.PLAYER_OFFLINE_NEVER, dPlayer.getPlayer().getName()), player);
			}
			
		} else if (dPlayer.isEditing()) {
			dPlayer.leave();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		// Check dPlayers
		DPlayer dPlayer = DPlayer.getByName(player.getName());
		if (dPlayer != null) {
			DGroup dGroup = DGroup.getByPlayer(dPlayer.getPlayer());
			if (dGroup != null) {
				dGroup.getPlayers().remove(dPlayer.getPlayer());
				dGroup.getPlayers().add(player);
			}
			dPlayer.setPlayer(player);
			
			// Check offlineTime
			dPlayer.setOfflineTime(0);
		}
		
		// Tutorial Mode
		if ( !plugin.getMainConfig().isTutorialActivated()) {
			return;
		}
		
		if (DPlayer.getByPlayer(player) != null) {
			return;
		}
		
		if (plugin.getPermissionProvider() == null) {
			return;
		}
		
		if ((plugin.getMainConfig().getTutorialDungeon() == null || plugin.getMainConfig().getTutorialStartGroup() == null || plugin.getMainConfig().getTutorialEndGroup() == null)) {
			return;
		}
		
		for (String group : plugin.getPermissionProvider().getPlayerGroups(player)) {
			if ( !plugin.getMainConfig().getTutorialStartGroup().equalsIgnoreCase(group)) {
				continue;
			}
			
			DGroup dGroup = new DGroup(player, plugin.getMainConfig().getTutorialDungeon(), false);
			
			DGroupCreateEvent createEvent = new DGroupCreateEvent(dGroup, player, DGroupCreateEvent.Cause.GROUP_SIGN);
			
			if (createEvent.isCancelled()) {
				dGroup = null;
			}
			
			if (dGroup == null) {
				continue;
			}
			
			if (dGroup.getGameWorld() == null) {
				dGroup.setGameWorld(GameWorld.load(DGroup.getByPlayer(player).getMapName()));
				dGroup.getGameWorld().setTutorial(true);
			}
			
			if (dGroup.getGameWorld() == null) {
				MessageUtil.sendMessage(player, dMessages.getMessage(Messages.ERROR_TUTORIAL_NOT_EXIST));
				continue;
			}
			
			if (dGroup.getGameWorld().getLocLobby() != null) {
				new DPlayer(player, dGroup.getGameWorld().getWorld(), dGroup.getGameWorld().getLocLobby(), false);
			}
		}
	}
	
	// Deny Player Cmds
	@EventHandler(priority = EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().hasPermission("dxl.bypass")) {
			return;
		}
		
		DPlayer dPlayer = DPlayer.getByPlayer(event.getPlayer());
		if (dPlayer == null) {
			return;
		}
		
		String command = event.getMessage().toLowerCase();
		ArrayList<String> commandWhitelist = new ArrayList<String>();
		
		GameWorld gameWorld = GameWorld.getByWorld(dPlayer.getWorld());
		
		if (dPlayer.isEditing()) {
			if (event.getPlayer().hasPermission("dxl.cmdedit")) {
				return;
				
			} else {
				commandWhitelist.addAll(plugin.getMainConfig().getEditCommandWhitelist());
			}
			
		} else if (gameWorld != null) {
			if (gameWorld.getConfig() != null) {
				commandWhitelist.addAll(gameWorld.getConfig().getGameCommandWhitelist());
			}
		}
		
		commandWhitelist.add("dungeonsxl");
		commandWhitelist.add("dungeon");
		commandWhitelist.add("dxl");
		
		event.setCancelled(true);
		
		for (String whitelistEntry : commandWhitelist) {
			if (command.equals('/' + whitelistEntry.toLowerCase()) || command.startsWith('/' + whitelistEntry.toLowerCase() + ' ')) {
				event.setCancelled(false);
			}
		}
		
		if (event.isCancelled()) {
			MessageUtil.sendMessage(event.getPlayer(), dMessages.getMessage(Messages.ERROR_CMD));
		}
	}
	
	// Inventory Events
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryOpen(InventoryOpenEvent event) {
		GameChest.onOpenInventory(event);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		
		for (DLootInventory inventory : plugin.getDLootInventories()) {
			if (event.getView() != inventory.getInventoryView()) {
				continue;
			}
			
			if (System.currentTimeMillis() - inventory.getTime() <= 500) {
				continue;
			}
			
			for (ItemStack istack : inventory.getInventory().getContents()) {
				if (istack != null) {
					player.getWorld().dropItem(player.getLocation(), istack);
				}
			}
			
			plugin.getDLootInventories().remove(inventory);
		}
	}
	
	// Player move
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		DLootInventory inventory = DLootInventory.getByPlayer(player);
		
		if (inventory == null) {
			return;
		}
		
		if (player.getLocation().getBlock().getType() == (plugin.getMainConfig().useWaterPortal() ? Material.STATIONARY_WATER : Material.PORTAL)) {
			return;
		}
		
		if (player.getLocation().getBlock().getRelative(0, 1, 0).getType() != (plugin.getMainConfig().useWaterPortal() ? Material.STATIONARY_WATER : Material.PORTAL) && player.getLocation().getBlock().getRelative(0, -1, 0).getType() != (plugin.getMainConfig().useWaterPortal() ? Material.STATIONARY_WATER : Material.PORTAL)
		        && player.getLocation().getBlock().getRelative(1, 0, 0).getType() != (plugin.getMainConfig().useWaterPortal() ? Material.STATIONARY_WATER : Material.PORTAL) && player.getLocation().getBlock().getRelative( -1, 0, 0).getType() != (plugin.getMainConfig().useWaterPortal() ? Material.STATIONARY_WATER : Material.PORTAL)
		        && player.getLocation().getBlock().getRelative(0, 0, 1).getType() != (plugin.getMainConfig().useWaterPortal() ? Material.STATIONARY_WATER : Material.PORTAL) && player.getLocation().getBlock().getRelative(0, 0, -1).getType() != (plugin.getMainConfig().useWaterPortal() ? Material.STATIONARY_WATER : Material.PORTAL)) {
			inventory.setInventoryView(inventory.getPlayer().openInventory(inventory.getInventory()));
			inventory.setTime(System.currentTimeMillis());
		}
	}
	
	// Etc. ---------------------------------
	
	public class RespawnRunnable implements Runnable {
		private Player player;
		private Location location;
		
		public RespawnRunnable(Player player, Location location) {
			this.location = location;
			this.player = player;
		}
		
		@Override
		public void run() {
			if (player.getLocation().distance(location) > 2) {
				MiscUtil.secureTeleport(player, location);
			}
			
			DPlayer dPlayer = DPlayer.getByPlayer(player);
			
			if (dPlayer == null) {
				return;
			}
			
			// Respawn Items
			if (dPlayer.getRespawnInventory() != null || dPlayer.getRespawnArmor() != null) {
				player.getInventory().setContents(dPlayer.getRespawnInventory());
				player.getInventory().setArmorContents(dPlayer.getRespawnArmor());
				dPlayer.setRespawnInventory(null);
				dPlayer.setRespawnArmor(null);
			}
		}
	}
	
}
