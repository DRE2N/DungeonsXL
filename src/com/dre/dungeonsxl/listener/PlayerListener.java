package com.dre.dungeonsxl.listener;

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
import com.dre.dungeonsxl.DGSign;
import com.dre.dungeonsxl.DGroup;
import com.dre.dungeonsxl.DLootInventory;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.DPortal;
import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.EditWorld;
import com.dre.dungeonsxl.LeaveSign;
import com.dre.dungeonsxl.game.GameChest;
import com.dre.dungeonsxl.game.GameWorld;
import com.dre.dungeonsxl.trigger.InteractTrigger;
import com.dre.dungeonsxl.trigger.UseItemTrigger;

public class PlayerListener implements Listener {
	public P p = P.p;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block clickedBlock = event.getClickedBlock();

		if (clickedBlock != null) {
			// Block Enderchests
			if (GameWorld.get(player.getWorld()) != null || EditWorld.get(player.getWorld()) != null) {
				if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
					if (clickedBlock.getType() == Material.ENDER_CHEST) {
						if (!player.isOp()) {
							p.msg(player, p.language.get("Error_Enderchest"));
							event.setCancelled(true);
						}
					}
				}
			}

			// Block Dispensers
			if (GameWorld.get(player.getWorld()) != null) {
				if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
					if (clickedBlock.getType() == Material.DISPENSER) {
						if (!player.isOp()) {
							p.msg(player, p.language.get("Error_Dispenser"));
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
					for (DPortal dportal : DPortal.portals) {
						if (!dportal.isActive) {
							if (dportal.player == player) {
								if (dportal.block1 == null) {
									dportal.block1 = event.getClickedBlock();
									p.msg(player, p.language.get("Player_PortalProgress"));
								} else if (dportal.block2 == null) {
									dportal.block2 = event.getClickedBlock();
									dportal.isActive = true;
									dportal.create();
									p.msg(player, p.language.get("Player_PortalCreated"));
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

			if (clickedBlock.getTypeId() == 68 || clickedBlock.getTypeId() == 63) {
				// Check Group Signs
				if (DGSign.playerInteract(event.getClickedBlock(), player)) {
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
							trigger.onTrigger(player);
						}

						// Class Signs
						for (Sign classSign : gworld.signClass) {
							if (classSign != null) {
								if (classSign.getLocation().distance(clickedBlock.getLocation()) < 1) {
									if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
										dplayer.setClass(ChatColor.stripColor(classSign.getLine(1)));
									} else {
										p.msg(player, p.language.get("Error_Leftklick"));
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
			if (!dgroup.isPlaying) {
				event.setCancelled(true);
				return;
			}
			if (!DPlayer.get(player).isReady) {
				event.setCancelled(true);
				return;
			}

			DPlayer dplayer = DPlayer.get(player);
			GameWorld gworld = GameWorld.get(dplayer.world);
			if (dplayer != null) {
				for (Material material : gworld.config.getSecureObjects()) {
					if (material == event.getItemDrop().getItemStack().getType()) {
						event.setCancelled(true);
						p.msg(player, p.language.get("Error_Drop"));
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
						event.setRespawnLocation(dgroup.getGworld().locStart);

						// Da einige Plugins einen anderen Respawn setzen wird
						// ein Scheduler gestartet der den Player nach einer
						// Sekunde teleportiert.
						p.getServer().getScheduler().scheduleSyncDelayedTask(p, new RespawnRunnable(player, dgroup.getGworld().locStart), 10);

						if (dplayer.wolf != null) {
							dplayer.wolf.teleport(dgroup.getGworld().locStart);
						}
					} else {
						event.setRespawnLocation(dplayer.checkpoint);

						// Da einige Plugins einen anderen Respawn setzen wird
						// ein Scheduler gestartet der den Player nach einer
						// Sekunde teleportiert.
						p.getServer().getScheduler().scheduleSyncDelayedTask(p, new RespawnRunnable(player, dplayer.checkpoint), 10);

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
				if (!player.isOp()) {
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
				int timeUntilKickOfflinePlayer = gWorld.config.getTimeUntilKickOfflinePlayer();

				if (timeUntilKickOfflinePlayer == 0) {
					dPlayer.leave();
				} else if (timeUntilKickOfflinePlayer > 0) {
					dPlayer.msg(p.language.get("Player_Offline", dPlayer.player.getName(), "" + timeUntilKickOfflinePlayer));
					dPlayer.offlineTime = System.currentTimeMillis() + timeUntilKickOfflinePlayer * 1000;
				} else {
					dPlayer.msg(p.language.get("Player_OfflineNeverKick", dPlayer.player.getName()));
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
		if (p.mainConfig.tutorialActivated) {
			if (DPlayer.get(player) == null) {
				if (p.mainConfig.tutorialDungeon != null && p.mainConfig.tutorialStartGroup != null && p.mainConfig.tutorialEndGroup != null) {
					for (String group : p.permission.getPlayerGroups(player)) {
						if (p.mainConfig.tutorialStartGroup.equalsIgnoreCase(group)) {
							DGroup dgroup = new DGroup(player, p.mainConfig.tutorialDungeon);

							if (dgroup.getGworld() == null) {
								dgroup.setGworld(GameWorld.load(DGroup.get(player).getDungeonname()));
								dgroup.getGworld().isTutorial = true;
							}

							if (dgroup.getGworld() != null) {
								if (dgroup.getGworld().locLobby == null) {
									new DPlayer(player, dgroup.getGworld().world, dgroup.getGworld().world.getSpawnLocation(), false);
								} else {
									new DPlayer(player, dgroup.getGworld().world, dgroup.getGworld().locLobby, false);
								}
							} else {
								p.msg(player, p.language.get("Error_TutorialNotExist"));
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent event) {
		DPlayer dplayer = DPlayer.get(event.getEntity());
		if (dplayer != null) {
			dplayer.respawnInventory = event.getEntity().getInventory().getContents();
			dplayer.respawnArmor = event.getEntity().getInventory().getArmorContents();
			// Delete all drops
			for (ItemStack istack : event.getDrops()) {
				istack.setTypeId(0);
			}
		}
	}

	// Deny Player Cmds
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		if (p.permission.has(event.getPlayer(), "dungeonsxl.cmd") || event.getPlayer().isOp()) {
			return;
		}

		DPlayer dplayer = DPlayer.get(event.getPlayer());
		if (dplayer != null) {
			if (!dplayer.isEditing) {
				String[] splittedCmd = event.getMessage().split(" ");
				if (!splittedCmd[0].equalsIgnoreCase("/dungeon") && !splittedCmd[0].equalsIgnoreCase("/dungeonsxl") && !splittedCmd[0].equalsIgnoreCase("/dxl")) {
					p.msg(event.getPlayer(), p.language.get("Error_Cmd"));
					event.setCancelled(true);
				}
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
		for (DLootInventory inventory : DLootInventory.LootInventorys) {
			if (event.getView() == inventory.inventoryView) {
				if (System.currentTimeMillis() - inventory.time > 500) {
					for (ItemStack istack : inventory.inventory.getContents()) {
						if (istack != null) {
							player.getWorld().dropItem(player.getLocation(), istack);
						}
					}

					DLootInventory.LootInventorys.remove(inventory);
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
						&& player.getLocation().getBlock().getRelative(1, 0, 0).getType() != Material.PORTAL && player.getLocation().getBlock().getRelative(-1, 0, 0).getType() != Material.PORTAL
						&& player.getLocation().getBlock().getRelative(0, 0, 1).getType() != Material.PORTAL && player.getLocation().getBlock().getRelative(0, 0, -1).getType() != Material.PORTAL) {
					inventory.inventoryView = inventory.player.openInventory(inventory.inventory);
					inventory.time = System.currentTimeMillis();
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
			if (this.player.getLocation().distance(this.location) > 2) {
				this.player.teleport(this.location);
			}

			DPlayer dplayer = DPlayer.get(this.player);

			if (dplayer != null) {
				// Respawn Items
				if (dplayer.respawnInventory != null || dplayer.respawnArmor != null) {
					this.player.getInventory().setContents(dplayer.respawnInventory);
					this.player.getInventory().setArmorContents(dplayer.respawnArmor);
					dplayer.respawnInventory = null;
					dplayer.respawnArmor = null;
				}
				// DungeonsXL.p.updateInventory(this.player);
			}
		}
	}
}
