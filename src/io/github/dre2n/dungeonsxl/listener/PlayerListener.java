package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.DLootInventory;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GameChest;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.global.LeaveSign;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import io.github.dre2n.dungeonsxl.trigger.UseItemTrigger;
import io.github.dre2n.dungeonsxl.util.MessageUtil;
import io.github.dre2n.dungeonsxl.util.MiscUtil;

import org.bukkit.Bukkit;
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
	
	public DungeonsXL plugin = DungeonsXL.getPlugin();
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		final DPlayer dPlayer = DPlayer.get(player);
		
		GameWorld gameWorld = GameWorld.get(player.getLocation().getWorld());
		if (gameWorld != null) {
			WorldConfig dConfig = gameWorld.getConfig();
			
			if (dPlayer != null) {
				dPlayer.lives = dPlayer.lives - 1;
				
				if (dPlayer.lives == 0 && dPlayer.isReady) {
					Bukkit.broadcastMessage(plugin.getDMessages().get("Player_DeathKick").replaceAll("v1", player.getName()).replaceAll("&", "\u00a7"));
					// TODO: This Runnable is a workaround for a bug I couldn't find, yet...
					new BukkitRunnable() {
						public void run() {
							dPlayer.leave();
						}
					}.runTaskLater(plugin, 1L);
					
				} else if ( !(dPlayer.lives == -1)) {
					MessageUtil.sendMessage(player, plugin.getDMessages().get("Player_Death").replaceAll("v1", String.valueOf(dPlayer.lives)));
					
				} else if (dConfig != null) {
					if (dConfig.getKeepInventoryOnDeath()) {
						dPlayer.respawnInventory = event.getEntity().getInventory().getContents();
						dPlayer.respawnArmor = event.getEntity().getInventory().getArmorContents();
						// Delete all drops
						for (ItemStack istack : event.getDrops()) {
							istack.setType(Material.AIR);
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block clickedBlock = event.getClickedBlock();
		
		if (clickedBlock != null) {
			// Block Enderchests
			if (GameWorld.get(player.getWorld()) != null || EditWorld.get(player.getWorld()) != null) {
				if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
					if (clickedBlock.getType() == Material.ENDER_CHEST) {
						if ( !player.isOp()) {// TODO: Permission
							MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_Enderchest"));
							event.setCancelled(true);
						}
						
					} else if (clickedBlock.getType() == Material.BED_BLOCK) {
						if ( !player.isOp()) {// TODO: Permission
							MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_Bed"));
							event.setCancelled(true);
						}
					}
				}
			}
			
			// Block Dispensers
			if (GameWorld.get(player.getWorld()) != null) {
				if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
					if (clickedBlock.getType() == Material.DISPENSER) {
						if ( !player.isOp()) {
							MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_Dispenser"));
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
									MessageUtil.sendMessage(player, plugin.getDMessages().get("Player_PortalProgress"));
									
								} else if (dportal.getBlock2() == null) {
									dportal.setBlock2(event.getClickedBlock());
									dportal.setActive(true);
									dportal.create();
									MessageUtil.sendMessage(player, plugin.getDMessages().get("Player_PortalCreated"));
								}
								event.setCancelled(true);
							}
						}
					}
				}
			}
			
			// Copy/Paste a Sign and Block-info
			if (EditWorld.get(player.getWorld()) != null) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (item.getType() == Material.STICK) {
						DPlayer dplayer = DPlayer.get(player);
						if (dplayer != null) {
							dplayer.poke(clickedBlock);
							event.setCancelled(true);
						}
					}
				}
			}
			
			// Trigger UseItem Signs
			GameWorld gworld = GameWorld.get(player.getWorld());
			if (gworld != null) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
					if (UseItemTrigger.hasTriggers(gworld)) {
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
						UseItemTrigger trigger = UseItemTrigger.get(name, gworld);
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
				
				DPlayer dplayer = DPlayer.get(player);
				if (dplayer != null) {
					
					// Check GameWorld Signs
					GameWorld gworld = GameWorld.get(player.getWorld());
					if (gworld != null) {
						
						// Trigger InteractTrigger
						InteractTrigger trigger = InteractTrigger.get(clickedBlock, gworld);
						if (trigger != null) {
							if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
								trigger.onTrigger(player);
							} else {
								MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_Leftklick"));
							}
						}
						
						// Class Signs
						for (Sign classSign : gworld.signClass) {
							if (classSign != null) {
								if (classSign.getLocation().distance(clickedBlock.getLocation()) < 1) {
									if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
										dplayer.setClass(ChatColor.stripColor(classSign.getLine(1)));
									} else {
										MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_Leftklick"));
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
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		// Deny dropping things at the lobby
		DGroup dgroup = DGroup.get(player);
		if (dgroup != null) {
			if ( !dgroup.isPlaying()) {
				event.setCancelled(true);
				return;
			}
			if ( !DPlayer.get(player).isReady) {
				event.setCancelled(true);
				return;
			}
			
			DPlayer dplayer = DPlayer.get(player);
			GameWorld gworld = GameWorld.get(dplayer.world);
			if (dplayer != null) {
				for (Material material : gworld.getConfig().getSecureObjects()) {
					if (material == event.getItemDrop().getItemStack().getType()) {
						event.setCancelled(true);
						MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_Drop"));
						return;
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		DPlayer dplayer = DPlayer.get(player);
		
		if (dplayer != null) {
			if (dplayer.isEditing) {
				EditWorld eworld = EditWorld.get(dplayer.world);
				if (eworld != null) {
					if (eworld.lobby == null) {
						event.setRespawnLocation(eworld.world.getSpawnLocation());
						
					} else {
						event.setRespawnLocation(eworld.lobby);
					}
				}
				
			} else {
				GameWorld gworld = GameWorld.get(dplayer.world);
				
				if (gworld != null) {
					DGroup dgroup = DGroup.get(dplayer.player);
					
					if (dplayer.checkpoint == null) {
						event.setRespawnLocation(dgroup.getGWorld().locStart);
						
						// Da einige Plugins einen anderen Respawn setzen wird
						// ein Scheduler gestartet der den Player nach einer
						// Sekunde teleportiert.
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new RespawnRunnable(player, dgroup.getGWorld().locStart), 10);
						
						if (dplayer.wolf != null) {
							dplayer.wolf.teleport(dgroup.getGWorld().locStart);
						}
						
					} else {
						event.setRespawnLocation(dplayer.checkpoint);
						
						// Da einige Plugins einen anderen Respawn setzen wird
						// ein Scheduler gestartet der den Player nach einer
						// Sekunde teleportiert.
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new RespawnRunnable(player, dplayer.checkpoint), 10);
						
						if (dplayer.wolf != null) {
							dplayer.wolf.teleport(dplayer.checkpoint);
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPortalEvent(PlayerPortalEvent event) {
		Player player = event.getPlayer();
		Location location = event.getFrom();
		DPortal dportal = DPortal.get(location);
		
		if (dportal != null) {
			event.setCancelled(true);
			dportal.teleport(player);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		DPlayer dplayer = DPlayer.get(player);
		
		if (dplayer != null) {
			if (dplayer.world != event.getTo().getWorld()) {
				if ( !player.isOp()) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		DPlayer dplayer = DPlayer.get(player);
		if (dplayer != null) {
			if (dplayer.isInDungeonChat) {
				dplayer.msg(player.getDisplayName() + ": " + event.getMessage());
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		DPlayer dPlayer = DPlayer.get(player);
		
		if (dPlayer != null) {
			// Check GameWorld
			GameWorld gWorld = GameWorld.get(player.getWorld());
			if (gWorld != null) {
				int timeUntilKickOfflinePlayer = gWorld.getConfig().getTimeUntilKickOfflinePlayer();
				
				if (timeUntilKickOfflinePlayer == 0) {
					dPlayer.leave();
					
				} else if (timeUntilKickOfflinePlayer > 0) {
					dPlayer.msg(plugin.getDMessages().get("Player_Offline", dPlayer.player.getName(), "" + timeUntilKickOfflinePlayer));
					dPlayer.offlineTime = System.currentTimeMillis() + timeUntilKickOfflinePlayer * 1000;
					
				} else {
					dPlayer.msg(plugin.getDMessages().get("Player_OfflineNeverKick", dPlayer.player.getName()));
				}
				
			} else if (dPlayer.isEditing) {
				dPlayer.leave();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		// Check dplayers
		DPlayer dplayer = DPlayer.get(player.getName());
		if (dplayer != null) {
			DGroup dgroup = DGroup.get(dplayer.player);
			if (dgroup != null) {
				dgroup.getPlayers().remove(dplayer.player);
				dgroup.getPlayers().add(player);
			}
			dplayer.player = player;
			
			// Check offlineTime
			dplayer.offlineTime = 0;
		}
		
		// Tutorial Mode
		if (plugin.getMainConfig().isTutorialActivated()) {
			if (DPlayer.get(player) == null) {
				if (plugin.getMainConfig().getTutorialDungeon() != null && plugin.getMainConfig().getTutorialStartGroup() != null && plugin.getMainConfig().getTutorialEndGroup() != null) {
					for (String group : plugin.getPermissionProvider().getPlayerGroups(player)) {
						if (plugin.getMainConfig().getTutorialStartGroup().equalsIgnoreCase(group)) {
							DGroup dgroup = new DGroup(player, plugin.getMainConfig().getTutorialDungeon(), false);
							
							if (dgroup.getGWorld() == null) {
								dgroup.setGWorld(GameWorld.load(DGroup.get(player).getMapName()));
								dgroup.getGWorld().isTutorial = true;
							}
							
							if (dgroup.getGWorld() != null) {
								if (dgroup.getGWorld().locLobby == null) {
									
								} else {
									new DPlayer(player, dgroup.getGWorld().world, dgroup.getGWorld().locLobby, false);
								}
								
							} else {
								MessageUtil.sendMessage(player, plugin.getDMessages().get("Error_TutorialNotExist"));
							}
						}
					}
				}
			}
		}
	}
	
	// Deny Player Cmds
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().hasPermission("dungeonsxl.cmd")) {
			return;
		}
		
		DPlayer dplayer = DPlayer.get(event.getPlayer());
		if (dplayer != null) {
			if (dplayer.isEditing && event.getPlayer().hasPermission("dungeonsxl.cmdedit") || event.getPlayer().isOp()) {
				return;
			}
			
			String[] splittedCmd = event.getMessage().split(" ");
			if ( !splittedCmd[0].equalsIgnoreCase("/dungeon") && !splittedCmd[0].equalsIgnoreCase("/dungeonsxl") && !splittedCmd[0].equalsIgnoreCase("/dxl")) {
				MessageUtil.sendMessage(event.getPlayer(), plugin.getDMessages().get("Error_Cmd"));
				event.setCancelled(true);
			}
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
			if (event.getView() == inventory.getInventoryView()) {
				if (System.currentTimeMillis() - inventory.getTime() > 500) {
					for (ItemStack istack : inventory.getInventory().getContents()) {
						if (istack != null) {
							player.getWorld().dropItem(player.getLocation(), istack);
						}
					}
					
					plugin.getDLootInventories().remove(inventory);
				}
			}
		}
	}
	
	// Player move
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		DLootInventory inventory = DLootInventory.get(player);
		
		if (inventory != null) {
			if (player.getLocation().getBlock().getType() != Material.PORTAL) {
				if (player.getLocation().getBlock().getRelative(0, 1, 0).getType() != Material.PORTAL && player.getLocation().getBlock().getRelative(0, -1, 0).getType() != Material.PORTAL
				        && player.getLocation().getBlock().getRelative(1, 0, 0).getType() != Material.PORTAL && player.getLocation().getBlock().getRelative( -1, 0, 0).getType() != Material.PORTAL
				        && player.getLocation().getBlock().getRelative(0, 0, 1).getType() != Material.PORTAL && player.getLocation().getBlock().getRelative(0, 0, -1).getType() != Material.PORTAL) {
					inventory.setInventoryView(inventory.getPlayer().openInventory(inventory.getInventory()));
					inventory.setTime(System.currentTimeMillis());
				}
			}
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
			
			DPlayer dplayer = DPlayer.get(player);
			
			if (dplayer != null) {
				// Respawn Items
				if (dplayer.respawnInventory != null || dplayer.respawnArmor != null) {
					player.getInventory().setContents(dplayer.respawnInventory);
					player.getInventory().setArmorContents(dplayer.respawnArmor);
					dplayer.respawnInventory = null;
					dplayer.respawnArmor = null;
				}
			}
		}
	}
}
