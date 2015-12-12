package io.github.dre2n.dungeonsxl.player;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.DLootInventory;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.trigger.DistanceTrigger;
import io.github.dre2n.dungeonsxl.util.MessageUtil;
import io.github.dre2n.dungeonsxl.util.MiscUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class DPlayer {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	// Variables
	public Player player;
	public World world;
	
	public boolean isinTestMode = false;
	
	public DSavePlayer savePlayer;
	
	public boolean isEditing;
	public boolean isInDungeonChat = false;
	public boolean isReady = false;
	public boolean isFinished = false;
	
	public DClass dclass;
	public Location checkpoint;
	public Wolf wolf;
	public int wolfRespawnTime = 30;
	public long offlineTime;
	public ItemStack[] respawnInventory;
	public ItemStack[] respawnArmor;
	public String[] linesCopy;
	
	public Inventory treasureInv = DungeonsXL.getPlugin().getServer().createInventory(player, 45, plugin.getDMessages().get("Player_Treasures"));
	public double treasureMoney = 0;
	
	public int initialLives = -1;
	public int lives;
	
	public DPlayer(Player player, World world, Location teleport, boolean isEditing) {
		plugin.getDPlayers().add(this);
		
		this.player = player;
		this.world = world;
		
		double health = ((Damageable) player).getHealth();
		
		savePlayer = new DSavePlayer(player.getName(), player.getUniqueId(), player.getLocation(), player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getLevel(),
		        player.getTotalExperience(), (int) health, player.getFoodLevel(), player.getFireTicks(), player.getGameMode(), player.getActivePotionEffects());
		
		this.isEditing = isEditing;
		
		if (this.isEditing) {
			this.player.setGameMode(GameMode.CREATIVE);
			clearPlayerData();
			
		} else {
			this.player.setGameMode(GameMode.SURVIVAL);
			WorldConfig dConfig = GameWorld.get(world).getConfig();
			if ( !dConfig.getKeepInventoryOnEnter()) {
				clearPlayerData();
			}
			if (dConfig.isLobbyDisabled()) {
				ready();
			}
			initialLives = GameWorld.get(world).getConfig().getInitialLives();
			lives = initialLives;
		}
		
		MiscUtil.secureTeleport(this.player, teleport);
	}
	
	public void clearPlayerData() {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setHealth(20);
		player.setFoodLevel(20);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}
	
	public void escape() {
		remove(this);
		savePlayer.reset(false);
	}
	
	public void leave() {
		remove(this);
		
		if ( !isEditing) {
			WorldConfig dConfig = GameWorld.get(world).getConfig();
			if (isFinished) {
				savePlayer.reset(dConfig.getKeepInventoryOnFinish());
			} else {
				savePlayer.reset(dConfig.getKeepInventoryOnEscape());
			}
		} else {
			savePlayer.reset(false);
		}
		
		if (isEditing) {
			EditWorld eworld = EditWorld.get(world);
			if (eworld != null) {
				eworld.save();
			}
		} else {
			GameWorld gworld = GameWorld.get(world);
			DGroup dgroup = DGroup.get(player);
			if (dgroup != null) {
				dgroup.removePlayer(player);
			}
			
			// Belohnung
			if ( !isinTestMode) {// Nur wenn man nicht am Testen ist
				if (isFinished) {
					addTreasure();
					plugin.economy.depositPlayer(player, treasureMoney);
					
					// Set Time
					File file = new File(plugin.getDataFolder() + "/maps/" + gworld.dungeonname, "players.yml");
					
					if ( !file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
					
					playerConfig.set(player.getUniqueId().toString(), System.currentTimeMillis());
					
					try {
						playerConfig.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// Tutorial Permissions
					if (gworld.isTutorial) {
						plugin.permission.playerAddGroup(player, plugin.getMainConfig().getTutorialEndGroup());
						plugin.permission.playerRemoveGroup(player, plugin.getMainConfig().getTutorialStartGroup());
					}
				}
			}
			
			// Give Secure Objects other Players
			if (dgroup != null) {
				if ( !dgroup.isEmpty()) {
					int i = 0;
					Player groupplayer;
					do {
						groupplayer = dgroup.getPlayers().get(i);
						if (groupplayer != null) {
							for (ItemStack istack : player.getInventory()) {
								if (istack != null) {
									if (gworld.secureObjects.contains(istack.getType())) {
										groupplayer.getInventory().addItem(istack);
									}
								}
							}
						}
						i++;
					} while (groupplayer == null);
				}
			}
		}
	}
	
	public void ready() {
		isReady = true;
		
		DGroup dgroup = DGroup.get(player);
		if ( !dgroup.isPlaying()) {
			if (dgroup != null) {
				for (Player player : dgroup.getPlayers()) {
					DPlayer dplayer = get(player);
					if ( !dplayer.isReady) {
						return;
					}
				}
			}
			
			dgroup.startGame();
		} else {
			respawn();
		}
	}
	
	public void respawn() {
		DGroup dgroup = DGroup.get(player);
		if (checkpoint == null) {
			MiscUtil.secureTeleport(player, dgroup.getGworld().locStart);
		} else {
			MiscUtil.secureTeleport(player, checkpoint);
		}
		if (wolf != null) {
			wolf.teleport(player);
		}
		
		// Respawn Items
		if (GameWorld.get(world).getConfig().getKeepInventoryOnDeath()) {
			org.bukkit.Bukkit.broadcastMessage("deactivated code triggered");
			if (respawnInventory != null || respawnArmor != null) {
				player.getInventory().setContents(respawnInventory);
				player.getInventory().setArmorContents(respawnArmor);
				respawnInventory = null;
				respawnArmor = null;
			}
			// P.plugin.updateInventory(this.player);
		}
	}
	
	public void finishFloor() {
		MessageUtil.sendMessage(player, plugin.getDMessages().get("Player_FinishedDungeon"));
		isFinished = true;
		
		DGroup dgroup = DGroup.get(player);
		if (dgroup == null) {
			return;
		}
		
		if ( !dgroup.isPlaying()) {
			return;
		}
		
		for (Player player : dgroup.getPlayers()) {
			DPlayer dplayer = get(player);
			if ( !dplayer.isFinished) {
				MessageUtil.sendMessage(this.player, plugin.getDMessages().get("Player_WaitForOtherPlayers"));
				return;
			}
		}
		
		for (Player player : dgroup.getPlayers()) {
			DPlayer dPlayer = get(player);
			dPlayer.isFinished = false;
		}
		
		dgroup.isPlaying();
	}
	
	public void finish() {
		MessageUtil.sendMessage(player, plugin.getDMessages().get("Player_FinishedDungeon"));
		isFinished = true;
		
		DGroup dgroup = DGroup.get(player);
		if (dgroup != null) {
			if (dgroup.isPlaying()) {
				for (Player player : dgroup.getPlayers()) {
					DPlayer dplayer = get(player);
					if ( !dplayer.isFinished) {
						MessageUtil.sendMessage(this.player, plugin.getDMessages().get("Player_WaitForOtherPlayers"));
						return;
					}
				}
				
				for (Player player : dgroup.getPlayers()) {
					DPlayer dplayer = get(player);
					dplayer.leave();
				}
			}
		}
	}
	
	public void msg(String msg) {
		if (isEditing) {
			EditWorld eworld = EditWorld.get(world);
			eworld.msg(msg);
			for (Player player : plugin.getChatSpyers()) {
				if ( !eworld.world.getPlayers().contains(player)) {
					MessageUtil.sendMessage(player, ChatColor.GREEN + "[Chatspy] " + ChatColor.WHITE + msg);
				}
			}
		} else {
			GameWorld gworld = GameWorld.get(world);
			gworld.msg(msg);
			for (Player player : plugin.getChatSpyers()) {
				if ( !gworld.world.getPlayers().contains(player)) {
					MessageUtil.sendMessage(player, ChatColor.GREEN + "[Chatspy] " + ChatColor.WHITE + msg);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void poke(Block block) {
		if (block.getState() instanceof Sign) {
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();
			if (lines[0].equals("") && lines[1].equals("") && lines[2].equals("") && lines[3].equals("")) {
				if (linesCopy != null) {
					SignChangeEvent event = new SignChangeEvent(block, player, linesCopy);
					plugin.getServer().getPluginManager().callEvent(event);
					if ( !event.isCancelled()) {
						sign.setLine(0, event.getLine(0));
						sign.setLine(1, event.getLine(1));
						sign.setLine(2, event.getLine(2));
						sign.setLine(3, event.getLine(3));
						sign.update();
					}
				}
			} else {
				linesCopy = lines;
				MessageUtil.sendMessage(player, plugin.getDMessages().get("Player_SignCopied"));
			}
		} else {
			String info = "" + block.getType();
			if (block.getData() != 0) {
				info = info + "," + block.getData();
			}
			MessageUtil.sendMessage(player, plugin.getDMessages().get("Player_BlockInfo", info));
		}
	}
	
	public void setClass(String classname) {
		GameWorld gworld = GameWorld.get(player.getWorld());
		if (gworld == null) {
			return;
		}
		
		DClass dclass = gworld.getConfig().getClass(classname);
		if (dclass != null) {
			if (this.dclass != dclass) {
				this.dclass = dclass;
				
				/* Set Dog */
				if (wolf != null) {
					wolf.remove();
					wolf = null;
				}
				
				if (dclass.hasDog()) {
					wolf = (Wolf) world.spawnEntity(player.getLocation(), EntityType.WOLF);
					wolf.setTamed(true);
					wolf.setOwner(player);
					
					double maxHealth = ((Damageable) wolf).getMaxHealth();
					wolf.setHealth(maxHealth);
				}
				
				/* Delete Inventory */
				player.getInventory().clear();
				player.getInventory().setArmorContents(null);
				player.getInventory().setItemInHand(new ItemStack(Material.AIR));
				
				// Remove Potion Effects
				for (PotionEffect effect : player.getActivePotionEffects()) {
					player.removePotionEffect(effect.getType());
				}
				
				// Reset lvl
				player.setTotalExperience(0);
				player.setLevel(0);
				
				/* Set Inventory */
				for (ItemStack istack : dclass.getItems()) {
					
					// Leggings
					if (istack.getType() == Material.LEATHER_LEGGINGS || istack.getType() == Material.CHAINMAIL_LEGGINGS || istack.getType() == Material.IRON_LEGGINGS
					        || istack.getType() == Material.DIAMOND_LEGGINGS || istack.getType() == Material.GOLD_LEGGINGS) {
						player.getInventory().setLeggings(istack);
					}
					// Helmet
					else if (istack.getType() == Material.LEATHER_HELMET || istack.getType() == Material.CHAINMAIL_HELMET || istack.getType() == Material.IRON_HELMET
					        || istack.getType() == Material.DIAMOND_HELMET || istack.getType() == Material.GOLD_HELMET) {
						player.getInventory().setHelmet(istack);
					}
					// Chestplate
					else if (istack.getType() == Material.LEATHER_CHESTPLATE || istack.getType() == Material.CHAINMAIL_CHESTPLATE || istack.getType() == Material.IRON_CHESTPLATE
					        || istack.getType() == Material.DIAMOND_CHESTPLATE || istack.getType() == Material.GOLD_CHESTPLATE) {
						player.getInventory().setChestplate(istack);
					}
					// Boots
					else if (istack.getType() == Material.LEATHER_BOOTS || istack.getType() == Material.CHAINMAIL_BOOTS || istack.getType() == Material.IRON_BOOTS
					        || istack.getType() == Material.DIAMOND_BOOTS || istack.getType() == Material.GOLD_BOOTS) {
						player.getInventory().setBoots(istack);
					}
					
					else {
						player.getInventory().addItem(istack);
					}
				}
			}
		}
	}
	
	public void setCheckpoint(Location checkpoint) {
		this.checkpoint = checkpoint;
	}
	
	public void addTreasure() {
		new DLootInventory(player, treasureInv.getContents());
	}
	
	// Static
	public static void remove(DPlayer player) {
		plugin.getDPlayers().remove(player);
	}
	
	public static DPlayer get(Player player) {
		for (DPlayer dplayer : plugin.getDPlayers()) {
			if (dplayer.player.equals(player)) {
				return dplayer;
			}
		}
		return null;
	}
	
	public static DPlayer get(String name) {
		for (DPlayer dplayer : plugin.getDPlayers()) {
			if (dplayer.player.getName().equalsIgnoreCase(name)) {
				return dplayer;
			}
		}
		return null;
	}
	
	public static CopyOnWriteArrayList<DPlayer> get(World world) {
		CopyOnWriteArrayList<DPlayer> dPlayers = new CopyOnWriteArrayList<DPlayer>();
		
		for (DPlayer dplayer : plugin.getDPlayers()) {
			if (dplayer.world == world) {
				dPlayers.add(dplayer);
			}
		}
		
		return dPlayers;
	}
	
	public static void update(boolean updateSecond) {
		for (DPlayer dplayer : plugin.getDPlayers()) {
			if ( !updateSecond) {
				if ( !dplayer.player.getWorld().equals(dplayer.world)) {
					if (dplayer.isEditing) {
						EditWorld eworld = EditWorld.get(dplayer.world);
						if (eworld != null) {
							if (eworld.lobby == null) {
								MiscUtil.secureTeleport(dplayer.player, eworld.world.getSpawnLocation());
							} else {
								MiscUtil.secureTeleport(dplayer.player, eworld.lobby);
							}
						}
					} else {
						GameWorld gworld = GameWorld.get(dplayer.world);
						if (gworld != null) {
							DGroup dgroup = DGroup.get(dplayer.player);
							if (dplayer.checkpoint == null) {
								MiscUtil.secureTeleport(dplayer.player, dgroup.getGworld().locStart);
								if (dplayer.wolf != null) {
									dplayer.wolf.teleport(dgroup.getGworld().locStart);
								}
							} else {
								MiscUtil.secureTeleport(dplayer.player, dplayer.checkpoint);
								if (dplayer.wolf != null) {
									dplayer.wolf.teleport(dplayer.checkpoint);
								}
							}
							
							// Respawn Items
							if (dplayer.respawnInventory != null || dplayer.respawnArmor != null) {
								dplayer.player.getInventory().setContents(dplayer.respawnInventory);
								dplayer.player.getInventory().setArmorContents(dplayer.respawnArmor);
								dplayer.respawnInventory = null;
								dplayer.respawnArmor = null;
							}
						}
					}
				}
			} else {
				GameWorld gworld = GameWorld.get(dplayer.world);
				
				if (gworld != null) {
					// Update Wolf
					if (dplayer.wolf != null) {
						if (dplayer.wolf.isDead()) {
							if (dplayer.wolfRespawnTime <= 0) {
								dplayer.wolf = (Wolf) dplayer.world.spawnEntity(dplayer.player.getLocation(), EntityType.WOLF);
								dplayer.wolf.setTamed(true);
								dplayer.wolf.setOwner(dplayer.player);
								dplayer.wolfRespawnTime = 30;
							}
							dplayer.wolfRespawnTime--;
						}
					}
					
					// Kick offline plugin.getDPlayers()
					if (dplayer.offlineTime > 0) {
						if (dplayer.offlineTime < System.currentTimeMillis()) {
							dplayer.leave();
						}
					}
					
					// Check Distance Trigger Signs
					DistanceTrigger.triggerAllInDistance(dplayer.player, gworld);
				}
			}
		}
	}
	
}
