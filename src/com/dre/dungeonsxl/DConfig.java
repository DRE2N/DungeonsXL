package com.dre.dungeonsxl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class DConfig {
	public static DConfig mainConfig = new DConfig();

	private File file;

	private boolean keepInventory = false;

	private CopyOnWriteArrayList<DClass> dClasses = new CopyOnWriteArrayList<DClass>();
	private Map<Integer, String> msgs = new HashMap<Integer, String>();

	private CopyOnWriteArrayList<String> invitedPlayers = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<Material> secureObjects = new CopyOnWriteArrayList<Material>();

	private int initialLives = 3;
	
	private boolean isLobbyDisabled = false;
	private int timeToNextPlay = 0;
	private int timeToNextLoot = 0;

	private int timeUntilKickOfflinePlayer = -1;

	private List<String> finishedOne;
	private List<String> finishedAll;
	private int timeLastPlayed = 0;

	// MobTypes
	private Set<DMobType> mobTypes = new HashSet<DMobType>();

	public DConfig() {

	}

	public DConfig(File file) {
		this.file = file;

		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);

		load(configFile);
	}

	public DConfig(ConfigurationSection configFile) {
		load(configFile);
	}

	// Load & Save
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
						itemId = P.p.parseInt(idAndData[0]);

						if (idAndData.length > 1) {
							itemData = P.p.parseInt(idAndData[1]);
						}

						// Size
						if (itemsplit.length > 1) {
							itemSize = P.p.parseInt(itemsplit[1]);
						}

						// Enchantment
						if (itemsplit.length > 2) {
							String[] enchantmentSplit = itemsplit[2].split("/");

							itemEnchantment = Enchantment.getByName(enchantmentSplit[0]);

							if (enchantmentSplit.length > 1) {
								itemLvlEnchantment = P.p.parseInt(enchantmentSplit[1]);
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
				this.dClasses.add(new DClass(name, istacks, hasDog));
			}
		}

		/* Messages */
		ConfigurationSection configSetionMessages = configFile.getConfigurationSection("message");
		if (configSetionMessages != null) {
			Set<String> list = configSetionMessages.getKeys(false);
			for (String messagePath : list) {
				int messageId = P.p.parseInt(messagePath);
				this.msgs.put(messageId, configSetionMessages.getString(messagePath));
			}
		}

		/* Secure Objects */
		if (configFile.contains("secureObjects")) {
			List<Integer> secureobjectlist = configFile.getIntegerList("secureObjects");
			for (int i : secureobjectlist) {
				this.secureObjects.add(Material.getMaterial(i));
			}
		}

		/* Invited Players */
		if (configFile.contains("invitedPlayers")) {
			List<String> invitedplayers = configFile.getStringList("invitedPlayers");
			for (String i : invitedplayers) {
				this.invitedPlayers.add(i);
			}
		}

		/* keep Inventory */
		if (configFile.contains("keepInventory")) {
			keepInventory = configFile.getBoolean("keepInventory");
		} else {
			keepInventory = mainConfig.keepInventory;
		}

		/* keep Inventory */
		if (configFile.contains("initialLives")) {
			initialLives = configFile.getInt("initialLives");
		} else {
			initialLives = mainConfig.getInitialLives();
		}

		/* Lobby */
		if (configFile.contains("isLobbyDisabled")) {
			isLobbyDisabled = configFile.getBoolean("isLobbyDisabled");
		} else {
			isLobbyDisabled = mainConfig.isLobbyDisabled;
		}

		/* Times */
		if (configFile.contains("timeToNextPlay")) {
			timeToNextPlay = configFile.getInt("timeToNextPlay");
		} else {
			timeToNextPlay = mainConfig.timeToNextPlay;
		}

		if (configFile.contains("timeToNextLoot")) {
			timeToNextLoot = configFile.getInt("timeToNextLoot");
		} else {
			timeToNextLoot = mainConfig.timeToNextLoot;
		}

		if (configFile.contains("timeUntilKickOfflinePlayer")) {
			timeUntilKickOfflinePlayer = configFile.getInt("timeUntilKickOfflinePlayer");
		} else {
			timeUntilKickOfflinePlayer = mainConfig.timeUntilKickOfflinePlayer;
		}

		/* Dungeon Requirements */
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
		this.mobTypes = DMobType.load(configSetionMessages);
	}

	public void save() {
		if (this.file != null) {
			FileConfiguration configFile = YamlConfiguration.loadConfiguration(this.file);

			// Messages
			for (Integer msgs : this.msgs.keySet()) {
				configFile.set("message." + msgs, this.msgs.get(msgs));
			}

			// Secure Objects
			CopyOnWriteArrayList<Integer> secureObjectsids = new CopyOnWriteArrayList<Integer>();

			for (Material mat : this.secureObjects) {
				secureObjectsids.add(mat.getId());
			}

			configFile.set("secureObjects", secureObjectsids);

			// Invited Players
			configFile.set("invitedPlayers", this.invitedPlayers);

			try {
				configFile.save(this.file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Getters and Setters
	public CopyOnWriteArrayList<DClass> getClasses() {
		if (this.dClasses != null) {
			if (!this.dClasses.isEmpty()) {
				return this.dClasses;
			}
		}

		return mainConfig.dClasses;
	}

	public DClass getClass(String name) {
		for (DClass dClass : this.dClasses) {
			if (dClass.name.equals(name)) {
				return dClass;
			}
		}

		for (DClass dClass : mainConfig.dClasses) {
			if (dClass.name.equals(name)) {
				return dClass;
			}
		}
		return null;
	}

	public String getMsg(int id, boolean returnMainConfig) {
		String msg = this.msgs.get(id);
		if (msg != null) {
			return this.msgs.get(id);
		}
		if (returnMainConfig) {
			return mainConfig.msgs.get(id);
		}

		return null;
	}

	public void setMsg(String msg, int id) {
		this.msgs.put(id, msg);
	}

	public CopyOnWriteArrayList<String> getInvitedPlayers() {
		CopyOnWriteArrayList<String> tmpInvitedPlayers = new CopyOnWriteArrayList<String>();
		tmpInvitedPlayers.addAll(this.invitedPlayers);
		tmpInvitedPlayers.addAll(mainConfig.invitedPlayers);
		return tmpInvitedPlayers;
	}

	public void addInvitedPlayer(String player) {
		this.invitedPlayers.add(player);
	}

	public void removeInvitedPlayers(String player) {
		this.invitedPlayers.remove(player);
	}

	public CopyOnWriteArrayList<Material> getSecureObjects() {
		CopyOnWriteArrayList<Material> tmpSecureObjects = new CopyOnWriteArrayList<Material>();
		tmpSecureObjects.addAll(this.secureObjects);
		tmpSecureObjects.addAll(mainConfig.secureObjects);
		return tmpSecureObjects;
	}

	public boolean getKeepInventory() {
		return keepInventory;
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
