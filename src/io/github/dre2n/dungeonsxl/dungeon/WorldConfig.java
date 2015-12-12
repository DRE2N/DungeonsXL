package io.github.dre2n.dungeonsxl.dungeon;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.mob.DMobType;
import io.github.dre2n.dungeonsxl.player.DClass;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class WorldConfig {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	public static WorldConfig defaultConfig = new WorldConfig();
	
	private File file;
	
	private boolean keepInventory = false;
	private boolean keepInventoryOnEnter = false;
	private boolean keepInventoryOnEscape = false;
	private boolean keepInventoryOnFinish = false;
	private boolean keepInventoryOnDeath = true;
	
	private CopyOnWriteArrayList<DClass> dClasses = new CopyOnWriteArrayList<DClass>();
	private Map<Integer, String> msgs = new HashMap<Integer, String>();
	
	private CopyOnWriteArrayList<String> invitedPlayers = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<Material> secureObjects = new CopyOnWriteArrayList<Material>();
	
	private int initialLives = 3;
	
	private boolean isLobbyDisabled = false;
	private int timeToNextPlay = 0;
	private int timeToNextLoot = 0;
	
	private int timeUntilKickOfflinePlayer = -1;
	
	private double fee = 0;
	
	private List<String> finishedOne;
	private List<String> finishedAll;
	private int timeLastPlayed = 0;
	
	// MobTypes
	private Set<DMobType> mobTypes = new HashSet<DMobType>();
	
	public WorldConfig() {
		
	}
	
	public WorldConfig(File file) {
		this.file = file;
		
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
		
		load(configFile);
	}
	
	public WorldConfig(ConfigurationSection configFile) {
		load(configFile);
	}
	
	// Load & Save
	@SuppressWarnings("deprecation")
	public void load(ConfigurationSection configFile) {
		/* Classes */
		ConfigurationSection configSetionClasses = configFile.getConfigurationSection("classes");
		if (configSetionClasses != null) {
			Set<String> list = configSetionClasses.getKeys(false);
			for (String className : list) {
				String name = className;
				boolean hasDog = configSetionClasses.getBoolean(className + ".dog");
				/* Items */
				List<String> items = configSetionClasses.getStringList(className + ".items");
				CopyOnWriteArrayList<ItemStack> istacks = new CopyOnWriteArrayList<ItemStack>();
				
				for (String item : items) {
					String[] itemsplit = item.split(",");
					if (itemsplit.length > 0) {
						int itemId = 0, itemData = 0, itemSize = 1, itemLvlEnchantment = 1;
						Enchantment itemEnchantment = null;
						// Check Id & Data
						String[] idAndData = itemsplit[0].split("/");
						itemId = IntegerUtil.parseInt(idAndData[0]);
						
						if (idAndData.length > 1) {
							itemData = IntegerUtil.parseInt(idAndData[1]);
						}
						
						// Size
						if (itemsplit.length > 1) {
							itemSize = IntegerUtil.parseInt(itemsplit[1]);
						}
						// Enchantment
						if (itemsplit.length > 2) {
							String[] enchantmentSplit = itemsplit[2].split("/");
							
							itemEnchantment = Enchantment.getByName(enchantmentSplit[0]);
							
							if (enchantmentSplit.length > 1) {
								itemLvlEnchantment = IntegerUtil.parseInt(enchantmentSplit[1]);
							}
						}
						
						// Add Item to Stacks
						ItemStack istack = new ItemStack(itemId, itemSize, (short) itemData);
						if (itemEnchantment != null) {
							istack.addEnchantment(itemEnchantment, itemLvlEnchantment);
						}
						istacks.add(istack);
					}
				}
				
				/* Create Class */
				dClasses.add(new DClass(name, istacks, hasDog));
			}
		}
		
		/* Messages */
		ConfigurationSection configSetionMessages = configFile.getConfigurationSection("message");
		if (configSetionMessages != null) {
			Set<String> list = configSetionMessages.getKeys(false);
			for (String messagePath : list) {
				int messageId = IntegerUtil.parseInt(messagePath);
				msgs.put(messageId, configSetionMessages.getString(messagePath));
			}
		}
		
		/* Secure Objects */
		if (configFile.contains("secureObjects")) {
			List<Integer> secureobjectlist = configFile.getIntegerList("secureObjects");
			for (int i : secureobjectlist) {
				secureObjects.add(Material.getMaterial(i));
			}
		}
		
		/* Invited Players */
		if (configFile.contains("invitedPlayers")) {
			List<String> invitedplayers = configFile.getStringList("invitedPlayers");
			for (String i : invitedplayers) {
				invitedPlayers.add(i);
			}
		}
		
		/* Keep Inventory */
		if (configFile.contains("keepInventory")) {
			if ( !configFile.contains("keepInventoryOnEnter")) {
				keepInventoryOnEnter = configFile.getBoolean("keepInventory");
			}
			if ( !configFile.contains("keepInventoryOnEscape")) {
				keepInventoryOnEscape = configFile.getBoolean("keepInventory");
			}
			if ( !configFile.contains("keepInventoryOnFinish")) {
				keepInventoryOnFinish = configFile.getBoolean("keepInventory");
			}
		} else {
			if (plugin.getDefaultConfig().keepInventory) {
				keepInventoryOnEnter = plugin.getDefaultConfig().keepInventory;
				keepInventoryOnEscape = plugin.getDefaultConfig().keepInventory;
				keepInventoryOnFinish = plugin.getDefaultConfig().keepInventory;
			}
		}
		
		if (configFile.contains("keepInventoryOnEnter")) {
			keepInventoryOnEnter = configFile.getBoolean("keepInventoryOnEnter");
		} else {
			keepInventoryOnEnter = plugin.getDefaultConfig().keepInventoryOnEnter;
		}
		
		if (configFile.contains("keepInventoryOnEscape")) {
			keepInventoryOnEscape = configFile.getBoolean("keepInventoryOnEscape");
		} else {
			keepInventoryOnEscape = plugin.getDefaultConfig().keepInventoryOnEscape;
		}
		
		if (configFile.contains("keepInventoryOnFinish")) {
			keepInventoryOnFinish = configFile.getBoolean("keepInventoryOnFinish");
		} else {
			keepInventoryOnFinish = plugin.getDefaultConfig().keepInventoryOnFinish;
		}
		
		if (configFile.contains("keepInventoryOnDeath")) {
			keepInventoryOnDeath = configFile.getBoolean("keepInventoryOnDeath");
		} else {
			keepInventoryOnDeath = plugin.getDefaultConfig().keepInventoryOnDeath;
		}
		
		/* Lives */
		if (configFile.contains("initialLives")) {
			initialLives = configFile.getInt("initialLives");
		} else {
			initialLives = plugin.getDefaultConfig().getInitialLives();
		}
		
		/* Lobby */
		if (configFile.contains("isLobbyDisabled")) {
			isLobbyDisabled = configFile.getBoolean("isLobbyDisabled");
		} else {
			isLobbyDisabled = plugin.getDefaultConfig().isLobbyDisabled;
		}
		
		/* Times */
		if (configFile.contains("timeToNextPlay")) {
			timeToNextPlay = configFile.getInt("timeToNextPlay");
		} else {
			timeToNextPlay = plugin.getDefaultConfig().timeToNextPlay;
		}
		
		if (configFile.contains("timeToNextLoot")) {
			timeToNextLoot = configFile.getInt("timeToNextLoot");
		} else {
			timeToNextLoot = plugin.getDefaultConfig().timeToNextLoot;
		}
		
		if (configFile.contains("timeUntilKickOfflinePlayer")) {
			timeUntilKickOfflinePlayer = configFile.getInt("timeUntilKickOfflinePlayer");
		} else {
			timeUntilKickOfflinePlayer = plugin.getDefaultConfig().timeUntilKickOfflinePlayer;
		}
		
		/* Dungeon Requirements */
		if (configFile.contains("fee")) {
			fee = configFile.getDouble("fee");
		} else {
			fee = plugin.getDefaultConfig().fee;
		}
		
		if (configFile.contains("mustFinishOne")) {
			finishedOne = configFile.getStringList("mustFinishOne");
		} else {
			finishedOne = new ArrayList<String>();
		}
		
		if (configFile.contains("mustFinishAll")) {
			finishedAll = configFile.getStringList("mustFinishAll");
		} else {
			finishedAll = new ArrayList<String>();
		}
		
		if (configFile.contains("timeLastPlayed")) {
			timeLastPlayed = configFile.getInt("timeLastPlayed");
		}
		
		/* Mobtypes */
		configSetionMessages = configFile.getConfigurationSection("mobTypes");
		mobTypes = DMobType.load(configSetionMessages);
	}
	
	@SuppressWarnings("deprecation")
	public void save() {
		if (file != null) {
			FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
			
			// Messages
			for (Integer msgs : this.msgs.keySet()) {
				configFile.set("message." + msgs, this.msgs.get(msgs));
			}
			
			// Secure Objects
			CopyOnWriteArrayList<Integer> secureObjectsids = new CopyOnWriteArrayList<Integer>();
			
			for (Material mat : secureObjects) {
				secureObjectsids.add(mat.getId());
			}
			
			configFile.set("secureObjects", secureObjectsids);
			
			// Invited Players
			configFile.set("invitedPlayers", invitedPlayers);
			
			try {
				configFile.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Getters and Setters
	public CopyOnWriteArrayList<DClass> getClasses() {
		if (dClasses != null) {
			if ( !dClasses.isEmpty()) {
				return dClasses;
			}
		}
		
		return plugin.getDefaultConfig().dClasses;
	}
	
	public DClass getClass(String name) {
		for (DClass dClass : dClasses) {
			if (dClass.getName().equals(name)) {
				return dClass;
			}
		}
		
		for (DClass dClass : plugin.getDefaultConfig().dClasses) {
			if (dClass.getName().equals(name)) {
				return dClass;
			}
		}
		return null;
	}
	
	public String getMsg(int id, boolean returnMainConfig) {
		String msg = msgs.get(id);
		if (msg != null) {
			return msgs.get(id);
		}
		if (returnMainConfig) {
			return plugin.getDefaultConfig().msgs.get(id);
		}
		
		return null;
	}
	
	public void setMsg(String msg, int id) {
		msgs.put(id, msg);
	}
	
	public CopyOnWriteArrayList<String> getInvitedPlayers() {
		CopyOnWriteArrayList<String> tmpInvitedPlayers = new CopyOnWriteArrayList<String>();
		tmpInvitedPlayers.addAll(invitedPlayers);
		tmpInvitedPlayers.addAll(plugin.getDefaultConfig().invitedPlayers);
		return tmpInvitedPlayers;
	}
	
	public void addInvitedPlayer(String uuid) {
		invitedPlayers.add(uuid);
	}
	
	public void removeInvitedPlayers(String uuid, String name) {
		invitedPlayers.remove(uuid);
		// remove player from a 0.9.1 and lower file
		invitedPlayers.remove(name);
	}
	
	public CopyOnWriteArrayList<Material> getSecureObjects() {
		CopyOnWriteArrayList<Material> tmpSecureObjects = new CopyOnWriteArrayList<Material>();
		tmpSecureObjects.addAll(secureObjects);
		tmpSecureObjects.addAll(plugin.getDefaultConfig().secureObjects);
		return tmpSecureObjects;
	}
	
	public boolean getKeepInventoryOnEnter() {
		return keepInventoryOnEnter;
	}
	
	public boolean getKeepInventoryOnEscape() {
		return keepInventoryOnEscape;
	}
	
	public boolean getKeepInventoryOnFinish() {
		return keepInventoryOnFinish;
	}
	
	public boolean getKeepInventoryOnDeath() {
		return keepInventoryOnDeath;
	}
	
	public int getInitialLives() {
		return initialLives;
	}
	
	public boolean isLobbyDisabled() {
		return isLobbyDisabled;
	}
	
	public int getTimeToNextPlay() {
		return timeToNextPlay;
	}
	
	public int getTimeToNextLoot() {
		return timeToNextLoot;
	}
	
	public int getTimeUntilKickOfflinePlayer() {
		return timeUntilKickOfflinePlayer;
	}
	
	public int getTimeLastPlayed() {
		return timeLastPlayed;
	}
	
	public double getFee() {
		return fee;
	}
	
	public List<String> getFinishedAll() {
		return finishedAll;
	}
	
	public List<String> getFinished() {
		List<String> merge = new ArrayList<String>();
		merge.addAll(finishedAll);
		merge.addAll(finishedOne);
		return merge;
	}
	
	public Set<DMobType> getMobTypes() {
		return mobTypes;
	}
	
}
