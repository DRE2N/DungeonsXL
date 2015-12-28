package io.github.dre2n.dungeonsxl.player;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.DungeonConfig;
import io.github.dre2n.dungeonsxl.dungeon.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.DLootInventory;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.trigger.DistanceTrigger;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;
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
	private Player player;
	private World world;
	
	private boolean isInTestMode = false;
	
	private DSavePlayer savePlayer;
	
	private boolean isEditing;
	private boolean isInDungeonChat = false;
	private boolean isReady = false;
	private boolean isFinished = false;
	
	private DClass dClass;
	private Location checkpoint;
	private Wolf wolf;
	private int wolfRespawnTime = 30;
	private long offlineTime;
	private ItemStack[] respawnInventory;
	private ItemStack[] respawnArmor;
	private String[] linesCopy;
	
	private Inventory treasureInv = DungeonsXL.getPlugin().getServer().createInventory(getPlayer(), 45, plugin.getDMessages().get("Player_Treasures"));
	private double treasureMoney = 0;
	
	private int initialLives = -1;
	private int lives;
	
	public DPlayer(Player player, World world, Location teleport, boolean isEditing) {
		plugin.getDPlayers().add(this);
		
		this.setPlayer(player);
		this.world = world;
		
		double health = ((Damageable) player).getHealth();
		
		savePlayer = new DSavePlayer(player.getName(), player.getUniqueId(), player.getLocation(), player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getLevel(),
		        player.getTotalExperience(), (int) health, player.getFoodLevel(), player.getFireTicks(), player.getGameMode(), player.getActivePotionEffects());
		
		this.isEditing = isEditing;
		
		if (this.isEditing) {
			this.getPlayer().setGameMode(GameMode.CREATIVE);
			clearPlayerData();
			
		} else {
			this.getPlayer().setGameMode(GameMode.SURVIVAL);
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
		
		MiscUtil.secureTeleport(this.getPlayer(), teleport);
	}
	
	public void clearPlayerData() {
		getPlayer().getInventory().clear();
		getPlayer().getInventory().setArmorContents(null);
		getPlayer().setTotalExperience(0);
		getPlayer().setLevel(0);
		getPlayer().setHealth(20);
		getPlayer().setFoodLevel(20);
		for (PotionEffect effect : getPlayer().getActivePotionEffects()) {
			getPlayer().removePotionEffect(effect.getType());
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
			GameWorld gameWorld = GameWorld.get(world);
			DGroup dGroup = DGroup.get(getPlayer());
			if (dGroup != null) {
				dGroup.removePlayer(getPlayer());
			}
			
			// Belohnung
			if ( !isInTestMode) {// Nur wenn man nicht am Testen ist
				if (isFinished) {
					addTreasure();
					if (plugin.getEconomyProvider() != null) {
						plugin.getEconomyProvider().depositPlayer(getPlayer(), treasureMoney);
					}
					
					// Set Time
					File file = new File(plugin.getDataFolder() + "/maps/" + gameWorld.getMapName(), "players.yml");
					
					if ( !file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
					
					playerConfig.set(getPlayer().getUniqueId().toString(), System.currentTimeMillis());
					
					try {
						playerConfig.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// Tutorial Permissions
					if (gameWorld.isTutorial()) {
						String endGroup = plugin.getMainConfig().getTutorialEndGroup();
						if (plugin.isGroupEnabled(endGroup)) {
							plugin.getPermissionProvider().playerAddGroup(getPlayer(), endGroup);
						}
						
						String startGroup = plugin.getMainConfig().getTutorialStartGroup();
						if (plugin.isGroupEnabled(startGroup)) {
							plugin.getPermissionProvider().playerRemoveGroup(getPlayer(), startGroup);
						}
					}
				}
			}
			
			// Give Secure Objects other Players
			if (dGroup != null) {
				if ( !dGroup.isEmpty()) {
					int i = 0;
					Player groupplayer;
					do {
						groupplayer = dGroup.getPlayers().get(i);
						if (groupplayer != null) {
							org.bukkit.Bukkit.broadcastMessage("14");
							for (ItemStack istack : getPlayer().getInventory()) {
								if (istack != null) {
									if (gameWorld.getSecureObjects().contains(istack.getType())) {
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
		
		DGroup dGroup = DGroup.get(getPlayer());
		if ( !dGroup.isPlaying()) {
			if (dGroup != null) {
				for (Player player : dGroup.getPlayers()) {
					DPlayer dplayer = get(player);
					if ( !dplayer.isReady) {
						return;
					}
				}
			}
			
			dGroup.startGame();
		} else {
			respawn();
		}
	}
	
	public void respawn() {
		DGroup dGroup = DGroup.get(getPlayer());
		if (checkpoint == null) {
			MiscUtil.secureTeleport(getPlayer(), dGroup.getGameWorld().getLocStart());
		} else {
			MiscUtil.secureTeleport(getPlayer(), checkpoint);
		}
		if (wolf != null) {
			wolf.teleport(getPlayer());
		}
		
		// Respawn Items
		if (GameWorld.get(world).getConfig().getKeepInventoryOnDeath()) {
			if (respawnInventory != null || respawnArmor != null) {
				getPlayer().getInventory().setContents(respawnInventory);
				getPlayer().getInventory().setArmorContents(respawnArmor);
				respawnInventory = null;
				respawnArmor = null;
			}
			// P.plugin.updateInventory(this.player);
		}
	}
	
	public void finishFloor(String specifiedFloor) {
		MessageUtil.sendMessage(getPlayer(), plugin.getDMessages().get("Player_FinishedDungeon"));
		isFinished = true;
		
		DGroup dGroup = DGroup.get(getPlayer());
		if (dGroup == null) {
			return;
		}
		
		if ( !dGroup.isPlaying()) {
			return;
		}
		
		for (Player player : dGroup.getPlayers()) {
			DPlayer dplayer = get(player);
			if ( !dplayer.isFinished) {
				MessageUtil.sendMessage(this.getPlayer(), plugin.getDMessages().get("Player_WaitForOtherPlayers"));
				return;
			}
		}
		
		boolean invalid = false;
		
		if (dGroup.getDungeon() == null) {
			invalid = true;
		}
		
		for (Player player : dGroup.getPlayers()) {
			DPlayer dPlayer = get(player);
			
			if (invalid) {
				dPlayer.leave();
				
			} else {
				dPlayer.isFinished = false;
			}
		}
		
		if (invalid) {
			return;
		}
		
		DungeonConfig dConfig = dGroup.getDungeon().getConfig();
		int random = IntegerUtil.generateRandomInt(0, dConfig.getFloors().size());
		String newFloor = dGroup.getUnplayedFloors().get(random);
		if (dConfig.getFloorCount() == dGroup.getFloorCount() - 1) {
			newFloor = dConfig.getEndFloor();
			
		} else if (specifiedFloor != null) {
			newFloor = specifiedFloor;
		}
		dGroup.removeUnplayedFloor(dGroup.getMapName());
		dGroup.setMapName(newFloor);
		GameWorld gameWorld = GameWorld.load(newFloor);
		dGroup.setGameWorld(gameWorld);
		for (Player player : dGroup.getPlayers()) {
			DPlayer dPlayer = get(player);
			dPlayer.setWorld(gameWorld.getWorld());
			dPlayer.setCheckpoint(dGroup.getGameWorld().getLocStart());
			if (dPlayer.getWolf() != null) {
				dPlayer.getWolf().teleport(dPlayer.getCheckpoint());
			}
		}
		dGroup.startGame();
	}
	
	public void finish() {
		MessageUtil.sendMessage(getPlayer(), plugin.getDMessages().get("Player_FinishedDungeon"));
		isFinished = true;
		
		DGroup dGroup = DGroup.get(getPlayer());
		if (dGroup == null) {
			return;
		}
		
		if ( !dGroup.isPlaying()) {
			return;
		}
		
		for (Player player : dGroup.getPlayers()) {
			DPlayer dplayer = get(player);
			if ( !dplayer.isFinished) {
				MessageUtil.sendMessage(this.getPlayer(), plugin.getDMessages().get("Player_WaitForOtherPlayers"));
				return;
			}
		}
		
		for (Player player : dGroup.getPlayers()) {
			DPlayer dPlayer = get(player);
			dPlayer.leave();
		}
	}
	
	public void msg(String msg) {
		if (isEditing) {
			EditWorld eworld = EditWorld.get(world);
			eworld.msg(msg);
			for (Player player : plugin.getChatSpyers()) {
				if ( !eworld.getWorld().getPlayers().contains(player)) {
					MessageUtil.sendMessage(player, ChatColor.GREEN + "[Chatspy] " + ChatColor.WHITE + msg);
				}
			}
		} else {
			GameWorld gameWorld = GameWorld.get(world);
			gameWorld.msg(msg);
			for (Player player : plugin.getChatSpyers()) {
				if ( !gameWorld.getWorld().getPlayers().contains(player)) {
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
					SignChangeEvent event = new SignChangeEvent(block, getPlayer(), linesCopy);
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
				MessageUtil.sendMessage(getPlayer(), plugin.getDMessages().get("Player_SignCopied"));
			}
		} else {
			String info = "" + block.getType();
			if (block.getData() != 0) {
				info = info + "," + block.getData();
			}
			MessageUtil.sendMessage(getPlayer(), plugin.getDMessages().get("Player_BlockInfo", info));
		}
	}
	
	public void addTreasure() {
		new DLootInventory(getPlayer(), treasureInv.getContents());
	}
	
	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * @param player
	 * the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/**
	 * @return the world
	 */
	public World getWorld() {
		return world;
	}
	
	/**
	 * @param world
	 * the world to set
	 */
	public void setWorld(World world) {
		this.world = world;
	}
	
	/**
	 * @return the isinTestMode
	 */
	public boolean isIsinTestMode() {
		return isInTestMode;
	}
	
	/**
	 * @param isInTestMode
	 * the isInTestMode to set
	 */
	public void setIsInTestMode(boolean isInTestMode) {
		this.isInTestMode = isInTestMode;
	}
	
	/**
	 * @return the savePlayer
	 */
	public DSavePlayer getSavePlayer() {
		return savePlayer;
	}
	
	/**
	 * @param savePlayer
	 * the savePlayer to set
	 */
	public void setSavePlayer(DSavePlayer savePlayer) {
		this.savePlayer = savePlayer;
	}
	
	/**
	 * @return the isEditing
	 */
	public boolean isEditing() {
		return isEditing;
	}
	
	/**
	 * @param isEditing
	 * the isEditing to set
	 */
	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}
	
	/**
	 * @return the isInDungeonChat
	 */
	public boolean isInDungeonChat() {
		return isInDungeonChat;
	}
	
	/**
	 * @param isInDungeonChat
	 * the isInDungeonChat to set
	 */
	public void setInDungeonChat(boolean isInDungeonChat) {
		this.isInDungeonChat = isInDungeonChat;
	}
	
	/**
	 * @return the isReady
	 */
	public boolean isReady() {
		return isReady;
	}
	
	/**
	 * @param isReady
	 * the isReady to set
	 */
	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
	
	/**
	 * @return the isFinished
	 */
	public boolean isFinished() {
		return isFinished;
	}
	
	/**
	 * @param isFinished
	 * the isFinished to set
	 */
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	
	/**
	 * @return the dClass
	 */
	public DClass getDClass() {
		return dClass;
	}
	
	/**
	 * @param dClass
	 * the dClass to set
	 */
	public void setDClass(String className) {
		GameWorld gameWorld = GameWorld.get(getPlayer().getWorld());
		if (gameWorld == null) {
			return;
		}
		
		DClass dClass = gameWorld.getConfig().getClass(className);
		if (dClass != null) {
			if (this.dClass != dClass) {
				this.dClass = dClass;
				
				/* Set Dog */
				if (wolf != null) {
					wolf.remove();
					wolf = null;
				}
				
				if (dClass.hasDog()) {
					wolf = (Wolf) world.spawnEntity(getPlayer().getLocation(), EntityType.WOLF);
					wolf.setTamed(true);
					wolf.setOwner(getPlayer());
					
					double maxHealth = ((Damageable) wolf).getMaxHealth();
					wolf.setHealth(maxHealth);
				}
				
				/* Delete Inventory */
				getPlayer().getInventory().clear();
				getPlayer().getInventory().setArmorContents(null);
				getPlayer().getInventory().setItemInHand(new ItemStack(Material.AIR));
				
				// Remove Potion Effects
				for (PotionEffect effect : getPlayer().getActivePotionEffects()) {
					getPlayer().removePotionEffect(effect.getType());
				}
				
				// Reset lvl
				getPlayer().setTotalExperience(0);
				getPlayer().setLevel(0);
				
				/* Set Inventory */
				for (ItemStack istack : dClass.getItems()) {
					
					// Leggings
					if (istack.getType() == Material.LEATHER_LEGGINGS || istack.getType() == Material.CHAINMAIL_LEGGINGS || istack.getType() == Material.IRON_LEGGINGS
					        || istack.getType() == Material.DIAMOND_LEGGINGS || istack.getType() == Material.GOLD_LEGGINGS) {
						getPlayer().getInventory().setLeggings(istack);
					}
					// Helmet
					else if (istack.getType() == Material.LEATHER_HELMET || istack.getType() == Material.CHAINMAIL_HELMET || istack.getType() == Material.IRON_HELMET
					        || istack.getType() == Material.DIAMOND_HELMET || istack.getType() == Material.GOLD_HELMET) {
						getPlayer().getInventory().setHelmet(istack);
					}
					// Chestplate
					else if (istack.getType() == Material.LEATHER_CHESTPLATE || istack.getType() == Material.CHAINMAIL_CHESTPLATE || istack.getType() == Material.IRON_CHESTPLATE
					        || istack.getType() == Material.DIAMOND_CHESTPLATE || istack.getType() == Material.GOLD_CHESTPLATE) {
						getPlayer().getInventory().setChestplate(istack);
					}
					// Boots
					else if (istack.getType() == Material.LEATHER_BOOTS || istack.getType() == Material.CHAINMAIL_BOOTS || istack.getType() == Material.IRON_BOOTS
					        || istack.getType() == Material.DIAMOND_BOOTS || istack.getType() == Material.GOLD_BOOTS) {
						getPlayer().getInventory().setBoots(istack);
					}
					
					else {
						getPlayer().getInventory().addItem(istack);
					}
				}
			}
		}
	}
	
	/**
	 * @return the checkpoint
	 */
	public Location getCheckpoint() {
		return checkpoint;
	}
	
	/**
	 * @param checkpoint
	 * the checkpoint to set
	 */
	public void setCheckpoint(Location checkpoint) {
		this.checkpoint = checkpoint;
	}
	
	/**
	 * @return the wolf
	 */
	public Wolf getWolf() {
		return wolf;
	}
	
	/**
	 * @param wolf
	 * the wolf to set
	 */
	public void setWolf(Wolf wolf) {
		this.wolf = wolf;
	}
	
	/**
	 * @return the wolfRespawnTime
	 */
	public int getWolfRespawnTime() {
		return wolfRespawnTime;
	}
	
	/**
	 * @param wolfRespawnTime
	 * the wolfRespawnTime to set
	 */
	public void setWolfRespawnTime(int wolfRespawnTime) {
		this.wolfRespawnTime = wolfRespawnTime;
	}
	
	/**
	 * @return the offlineTime
	 */
	public long getOfflineTime() {
		return offlineTime;
	}
	
	/**
	 * @param offlineTime
	 * the offlineTime to set
	 */
	public void setOfflineTime(long offlineTime) {
		this.offlineTime = offlineTime;
	}
	
	/**
	 * @return the respawnInventory
	 */
	public ItemStack[] getRespawnInventory() {
		return respawnInventory;
	}
	
	/**
	 * @param respawnInventory
	 * the respawnInventory to set
	 */
	public void setRespawnInventory(ItemStack[] respawnInventory) {
		this.respawnInventory = respawnInventory;
	}
	
	/**
	 * @return the respawnArmor
	 */
	public ItemStack[] getRespawnArmor() {
		return respawnArmor;
	}
	
	/**
	 * @param respawnArmor
	 * the respawnArmor to set
	 */
	public void setRespawnArmor(ItemStack[] respawnArmor) {
		this.respawnArmor = respawnArmor;
	}
	
	/**
	 * @return the linesCopy
	 */
	public String[] getLinesCopy() {
		return linesCopy;
	}
	
	/**
	 * @param linesCopy
	 * the linesCopy to set
	 */
	public void setLinesCopy(String[] linesCopy) {
		this.linesCopy = linesCopy;
	}
	
	/**
	 * @return the treasureInv
	 */
	public Inventory getTreasureInv() {
		return treasureInv;
	}
	
	/**
	 * @param treasureInv
	 * the treasureInv to set
	 */
	public void setTreasureInv(Inventory treasureInv) {
		this.treasureInv = treasureInv;
	}
	
	/**
	 * @return the treasureMoney
	 */
	public double getTreasureMoney() {
		return treasureMoney;
	}
	
	/**
	 * @param treasureMoney
	 * the treasureMoney to set
	 */
	public void setTreasureMoney(double treasureMoney) {
		this.treasureMoney = treasureMoney;
	}
	
	/**
	 * @return the initialLives
	 */
	public int getInitialLives() {
		return initialLives;
	}
	
	/**
	 * @param initialLives
	 * the initialLives to set
	 */
	public void setInitialLives(int initialLives) {
		this.initialLives = initialLives;
	}
	
	/**
	 * @return the lives
	 */
	public int getLives() {
		return lives;
	}
	
	/**
	 * @param lives
	 * the lives to set
	 */
	public void setLives(int lives) {
		this.lives = lives;
	}
	
	// Static
	
	public static void remove(DPlayer player) {
		plugin.getDPlayers().remove(player);
	}
	
	public static DPlayer get(Player player) {
		for (DPlayer dplayer : plugin.getDPlayers()) {
			if (dplayer.getPlayer().equals(player)) {
				return dplayer;
			}
		}
		return null;
	}
	
	public static DPlayer get(String name) {
		for (DPlayer dplayer : plugin.getDPlayers()) {
			if (dplayer.getPlayer().getName().equalsIgnoreCase(name)) {
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
				if ( !dplayer.getPlayer().getWorld().equals(dplayer.world)) {
					if (dplayer.isEditing) {
						EditWorld eworld = EditWorld.get(dplayer.world);
						if (eworld != null) {
							if (eworld.getLobby() == null) {
								MiscUtil.secureTeleport(dplayer.getPlayer(), eworld.getWorld().getSpawnLocation());
							} else {
								MiscUtil.secureTeleport(dplayer.getPlayer(), eworld.getLobby());
							}
						}
					} else {
						GameWorld gameWorld = GameWorld.get(dplayer.world);
						if (gameWorld != null) {
							DGroup dGroup = DGroup.get(dplayer.getPlayer());
							if (dplayer.checkpoint == null) {
								MiscUtil.secureTeleport(dplayer.getPlayer(), dGroup.getGameWorld().getLocStart());
								if (dplayer.wolf != null) {
									dplayer.wolf.teleport(dGroup.getGameWorld().getLocStart());
								}
							} else {
								MiscUtil.secureTeleport(dplayer.getPlayer(), dplayer.checkpoint);
								if (dplayer.wolf != null) {
									dplayer.wolf.teleport(dplayer.checkpoint);
								}
							}
							
							// Respawn Items
							if (dplayer.respawnInventory != null || dplayer.respawnArmor != null) {
								dplayer.getPlayer().getInventory().setContents(dplayer.respawnInventory);
								dplayer.getPlayer().getInventory().setArmorContents(dplayer.respawnArmor);
								dplayer.respawnInventory = null;
								dplayer.respawnArmor = null;
							}
						}
					}
				}
			} else {
				GameWorld gameWorld = GameWorld.get(dplayer.world);
				
				if (gameWorld != null) {
					// Update Wolf
					if (dplayer.wolf != null) {
						if (dplayer.wolf.isDead()) {
							if (dplayer.wolfRespawnTime <= 0) {
								dplayer.wolf = (Wolf) dplayer.world.spawnEntity(dplayer.getPlayer().getLocation(), EntityType.WOLF);
								dplayer.wolf.setTamed(true);
								dplayer.wolf.setOwner(dplayer.getPlayer());
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
					DistanceTrigger.triggerAllInDistance(dplayer.getPlayer(), gameWorld);
				}
			}
		}
	}
	
}
