package io.github.dre2n.dungeonsxl.player;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.util.MiscUtil;
import io.github.dre2n.dungeonsxl.util.offlineplayerutil.OfflinePlayerUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class DSavePlayer {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	private static CopyOnWriteArrayList<DSavePlayer> savePlayers = new CopyOnWriteArrayList<DSavePlayer>();
	
	// Variables
	private String playerName;
	private String uuid;
	
	private Location oldLocation;
	private ItemStack[] oldInventory;
	private ItemStack[] oldArmor;
	private int oldLvl;
	private int oldExp;
	private int oldHealth;
	private int oldFoodLevel;
	private int oldFireTicks;
	private GameMode oldGamemode;
	private Collection<PotionEffect> oldPotionEffects;
	
	public DSavePlayer(String playerName, UUID uuid, Location oldLocation, ItemStack[] oldInventory, ItemStack[] oldArmor, int oldLvl, int oldExp, int oldHealth, int oldFoodLevel, int oldFireTicks,
	        GameMode oldGamemode, Collection<PotionEffect> oldPotionEffects) {
		savePlayers.add(this);
		
		this.playerName = playerName;
		this.uuid = uuid.toString();
		
		this.oldLocation = oldLocation;
		this.oldInventory = oldInventory;
		this.oldArmor = oldArmor;
		this.oldExp = oldExp;
		this.oldHealth = oldHealth;
		this.oldFoodLevel = oldFoodLevel;
		this.oldGamemode = oldGamemode;
		this.oldLvl = oldLvl;
		this.oldFireTicks = oldFireTicks;
		this.oldPotionEffects = oldPotionEffects;
		
		save();
	}
	
	public void reset(boolean keepInventory) {
		Player player = plugin.getServer().getPlayer(playerName);
		boolean offline = false;
		if (player == null) {
			player = OfflinePlayerUtil.getOfflinePlayer(playerName, UUID.fromString(uuid), oldLocation);
			offline = true;
		}
		if (player == null) {
			return;
		}
		
		try {
			if ( !keepInventory) {
				player.getInventory().setContents(oldInventory);
				player.getInventory().setArmorContents(oldArmor);
				player.setTotalExperience(oldExp);
				player.setLevel(oldLvl);
				player.setHealth(oldHealth);
				player.setFoodLevel(oldFoodLevel);
				player.setGameMode(oldGamemode);
				player.setFireTicks(oldFireTicks);
				for (PotionEffect effect : player.getActivePotionEffects()) {
					player.removePotionEffect(effect.getType());
				}
				// Causes NPE if offline
				if ( !offline) {
					player.addPotionEffects(oldPotionEffects);
					
				} else {
					player.saveData();
				}
			}
			
			if ( !offline && oldLocation.getWorld() != null) {
				MiscUtil.secureTeleport(player, oldLocation);
			}
			
		} catch (NullPointerException exception) {
			plugin.getLogger().info("Corrupted playerdata detected and removed!");
		}
		
		savePlayers.remove(this);
		save();
	}
	
	// Static
	@SuppressWarnings("deprecation")
	public static void save() {
		FileConfiguration configFile = new YamlConfiguration();
		
		for (DSavePlayer savePlayer : savePlayers) {
			configFile.set(savePlayer.playerName + ".uuid", savePlayer.uuid);
			configFile.set(savePlayer.playerName + ".oldGamemode", savePlayer.oldGamemode.getValue());
			configFile.set(savePlayer.playerName + ".oldFireTicks", savePlayer.oldFireTicks);
			configFile.set(savePlayer.playerName + ".oldFoodLevel", savePlayer.oldFoodLevel);
			configFile.set(savePlayer.playerName + ".oldHealth", savePlayer.oldHealth);
			configFile.set(savePlayer.playerName + ".oldExp", savePlayer.oldExp);
			configFile.set(savePlayer.playerName + ".oldLvl", savePlayer.oldLvl);
			configFile.set(savePlayer.playerName + ".oldArmor", savePlayer.oldArmor);
			configFile.set(savePlayer.playerName + ".oldInventory", savePlayer.oldInventory);
			configFile.set(savePlayer.playerName + ".oldLocation.x", savePlayer.oldLocation.getX());
			configFile.set(savePlayer.playerName + ".oldLocation.y", savePlayer.oldLocation.getY());
			configFile.set(savePlayer.playerName + ".oldLocation.z", savePlayer.oldLocation.getZ());
			configFile.set(savePlayer.playerName + ".oldLocation.yaw", savePlayer.oldLocation.getYaw());
			configFile.set(savePlayer.playerName + ".oldLocation.pitch", savePlayer.oldLocation.getPitch());
			configFile.set(savePlayer.playerName + ".oldLocation.world", savePlayer.oldLocation.getWorld().getName());
			configFile.set(savePlayer.playerName + ".oldPotionEffects", savePlayer.oldPotionEffects);
		}
		
		try {
			configFile.save(new File(plugin.getDataFolder(), "savePlayers.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({"unchecked", "deprecation"})
	public static void load() {
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "savePlayers.yml"));
		
		for (String playerName : configFile.getKeys(false)) {
			// Load uuid
			UUID uuid = UUID.fromString(configFile.getString(playerName + ".uuid"));
			
			// Load inventory data
			ArrayList<ItemStack> oldInventoryList = (ArrayList<ItemStack>) configFile.get(playerName + ".oldInventory");
			ArrayList<ItemStack> oldArmorList = (ArrayList<ItemStack>) configFile.get(playerName + ".oldArmor");
			
			ItemStack[] oldInventory = oldInventoryList.toArray(new ItemStack[oldInventoryList.size()]);
			ItemStack[] oldArmor = oldArmorList.toArray(new ItemStack[oldArmorList.size()]);
			
			// Load other data
			int oldLvl = configFile.getInt(playerName + ".oldLvl");
			int oldExp = configFile.getInt(playerName + ".oldExp");
			int oldHealth = configFile.getInt(playerName + ".oldHealth");
			int oldFoodLevel = configFile.getInt(playerName + ".oldFoodLevel");
			int oldFireTicks = configFile.getInt(playerName + ".oldFireTicks");
			GameMode oldGamemode = GameMode.getByValue(configFile.getInt(playerName + ".oldGamemode"));
			Collection<PotionEffect> oldPotionEffects = (Collection<PotionEffect>) configFile.get(playerName + ".oldPotionEffects");
			
			// Location
			World world = plugin.getServer().getWorld(configFile.getString(playerName + ".oldLocation.world"));
			if (world == null) {
				world = plugin.getServer().getWorlds().get(0);
			}
			
			Location oldLocation = new Location(world, configFile.getDouble(playerName + ".oldLocation.x"), configFile.getDouble(playerName + ".oldLocation.y"), configFile.getDouble(playerName
			        + ".oldLocation.z"), configFile.getInt(playerName + ".oldLocation.yaw"), configFile.getInt(playerName + ".oldLocation.pitch"));
			
			// Create Player
			DSavePlayer savePlayer = new DSavePlayer(playerName, uuid, oldLocation, oldInventory, oldArmor, oldLvl, oldExp, oldHealth, oldFoodLevel, oldFireTicks, oldGamemode, oldPotionEffects);
			savePlayer.reset(false);
		}
	}
	
}
