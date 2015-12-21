package io.github.dre2n.dungeonsxl;

import io.github.dre2n.dungeonsxl.command.DCommands;
import io.github.dre2n.dungeonsxl.dungeon.DLootInventory;
import io.github.dre2n.dungeonsxl.dungeon.Dungeons;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.file.DMessages;
import io.github.dre2n.dungeonsxl.file.MainConfig;
import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.global.LeaveSign;
import io.github.dre2n.dungeonsxl.listener.BlockListener;
import io.github.dre2n.dungeonsxl.listener.CommandListener;
import io.github.dre2n.dungeonsxl.listener.EntityListener;
import io.github.dre2n.dungeonsxl.listener.HangingListener;
import io.github.dre2n.dungeonsxl.listener.PlayerListener;
import io.github.dre2n.dungeonsxl.listener.WorldListener;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.player.DSavePlayer;
import io.github.dre2n.dungeonsxl.util.FileUtil;
import io.github.dre2n.dungeonsxl.util.VersionUtil;
import io.github.dre2n.dungeonsxl.util.VersionUtil.Internals;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonsXL extends JavaPlugin {
	
	private static DungeonsXL plugin;
	
	private MainConfig mainConfig;
	private DMessages dMessages;
	private VersionUtil versionUtil;
	private DCommands dCommands;
	private Dungeons dungeons;
	
	private CopyOnWriteArrayList<Player> chatSpyers = new CopyOnWriteArrayList<Player>();
	private CopyOnWriteArrayList<DLootInventory> dLootInventories = new CopyOnWriteArrayList<DLootInventory>();
	private CopyOnWriteArrayList<EditWorld> editWorlds = new CopyOnWriteArrayList<EditWorld>();
	private CopyOnWriteArrayList<GameWorld> gameWorlds = new CopyOnWriteArrayList<GameWorld>();
	private CopyOnWriteArrayList<GroupSign> groupSigns = new CopyOnWriteArrayList<GroupSign>();
	private CopyOnWriteArrayList<LeaveSign> leaveSigns = new CopyOnWriteArrayList<LeaveSign>();
	private CopyOnWriteArrayList<DPortal> dPortals = new CopyOnWriteArrayList<DPortal>();
	private CopyOnWriteArrayList<DGroup> dGroups = new CopyOnWriteArrayList<DGroup>();
	private CopyOnWriteArrayList<DPlayer> dPlayers = new CopyOnWriteArrayList<DPlayer>();
	
	@Override
	public void onEnable() {
		plugin = this;
		getDataFolder().mkdir();
		
		// Load Language
		dMessages = new DMessages(new File(plugin.getDataFolder(), "languages/en.yml"));
		
		// Load Config
		mainConfig = new MainConfig(new File(plugin.getDataFolder(), "config.yml"));
		
		// Load Language 2
		loadDMessages(new File(plugin.getDataFolder(), "languages/" + mainConfig.getLanguage() + ".yml"));
		loadVersionUtil();
		loadDCommands();
		loadDungeons();
		
		// InitFolders
		initFolders();
		
		// Setup Permissions
		setupPermissions();
		
		// Setup Economy
		setupEconomy();
		
		getCommand("dungeonsxl").setExecutor(new CommandListener());
		Bukkit.getServer().getPluginManager().registerEvents(new EntityListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new BlockListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WorldListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new HangingListener(), this);
		
		// Load All
		loadAll();
		
		// Scheduler
		initSchedulers();
		
		// MSG
		getLogger().info("DungeonsXL " + getDescription().getVersion() + " for Spigot 1.8.8 loaded succesfully!");
		if (versionUtil.getInternals() == Internals.OUTDATED) {
			getLogger().info("Warning: Your CraftBukkit version is deprecated. DungeonsXL does not support it.");
		}
	}
	
	@Override
	public void onDisable() {
		// Save
		saveData();
		dMessages.save();
		
		// DPlayer leaves World
		for (DPlayer dplayer : dPlayers) {
			dplayer.leave();
		}
		
		// Delete all Data
		chatSpyers.clear();
		dLootInventories.clear();
		groupSigns.clear();
		leaveSigns.clear();
		dPortals.clear();
		dGroups.clear();
		dPlayers.clear();
		
		// Delete Worlds
		GameWorld.deleteAll();
		gameWorlds.clear();
		EditWorld.deleteAll();
		editWorlds.clear();
		
		// Disable listeners
		HandlerList.unregisterAll(plugin);
		
		// Stop shedulers
		plugin.getServer().getScheduler().cancelTasks(this);
	}
	
	// Init.
	public void initFolders() {
		if ( !getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		
		File dungeons = new File(getDataFolder() + "/dungeons");
		if ( !dungeons.exists()) {
			dungeons.mkdir();
		}
		
		File languages = new File(getDataFolder() + "/languages");
		if ( !languages.exists()) {
			languages.mkdir();
		}
		
		File maps = new File(getDataFolder() + "/maps");
		if ( !maps.exists()) {
			maps.mkdir();
		}
	}
	
	public void initSchedulers() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (GameWorld gworld : gameWorlds) {
					if (gworld.world.getPlayers().isEmpty()) {
						if (DPlayer.get(gworld.world).isEmpty()) {
							gworld.delete();
						}
					}
				}
				for (EditWorld eworld : editWorlds) {
					if (eworld.world.getPlayers().isEmpty()) {
						eworld.delete();
					}
				}
			}
		}, 0L, 1200L);
		
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				GameWorld.update();
				DPlayer.update(true);
			}
		}, 0L, 20L);
		
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				DPlayer.update(false);
			}
		}, 0L, 2L);
	}
	
	// Permissions
	public Permission permission = null;
	
	private Boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return permission != null;
	}
	
	public Boolean GroupEnabled(String group) {
		
		for (String agroup : permission.getGroups()) {
			if (agroup.equalsIgnoreCase(group)) {
				return true;
			}
		}
		
		return false;
	}
	
	// Economy
	public Economy economy = null;
	
	private Boolean setupEconomy() {
		if (mainConfig.enableEconomy()) {
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
			return economy != null;
		} else {
			return false;
		}
	}
	
	// Save and Load
	public void saveData() {
		File file = new File(getDataFolder(), "data.yml");
		FileConfiguration configFile = new YamlConfiguration();
		
		DPortal.save(configFile);
		GroupSign.save(configFile);
		LeaveSign.save(configFile);
		
		try {
			configFile.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadAll() {
		// Load world data
		File file = new File(getDataFolder(), "data.yml");
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
		
		DPortal.load(configFile);
		GroupSign.load(configFile);
		LeaveSign.load(configFile);
		
		// Load saved players
		DSavePlayer.load();
		
		// Check Worlds
		checkWorlds();
	}
	
	public void checkWorlds() {
		File serverDir = new File(".");
		
		for (File file : serverDir.listFiles()) {
			if (file.getName().contains("DXL_Edit_") && file.isDirectory()) {
				for (File dungeonFile : file.listFiles()) {
					if (dungeonFile.getName().contains(".id_")) {
						String dungeonName = dungeonFile.getName().substring(4);
						FileUtil.copyDirectory(file, new File(plugin.getDataFolder(), "/maps/" + dungeonName));
						FileUtil.deletenotusingfiles(new File(plugin.getDataFolder(), "/maps/" + dungeonName));
					}
				}
				
				FileUtil.removeDirectory(file);
			} else if (file.getName().contains("DXL_Game_") && file.isDirectory()) {
				FileUtil.removeDirectory(file);
			}
		}
	}
	
	// Getters & loaders
	
	/**
	 * @return the plugin instance
	 */
	public static DungeonsXL getPlugin() {
		return plugin;
	}
	
	/**
	 * @return the loaded instance of MainConfig
	 */
	public MainConfig getMainConfig() {
		return mainConfig;
	}
	
	/**
	 * load / reload a new instance of MainConfig
	 */
	public void loadMainConfig(File file) {
		mainConfig = new MainConfig(file);
	}
	
	/**
	 * @return the loaded instance of VersionUtil
	 */
	public VersionUtil getVersion() {
		return versionUtil;
	}
	
	/**
	 * load / reload a new instance of VersionUtil
	 */
	public void loadVersionUtil() {
		versionUtil = new VersionUtil();
	}
	
	/**
	 * @return the loaded instance of DMessages
	 */
	public DMessages getDMessages() {
		return dMessages;
	}
	
	/**
	 * load / reload a new instance of DMessages
	 */
	public void loadDMessages(File file) {
		dMessages = new DMessages(file);
	}
	
	/**
	 * @return the loaded instance of DCommands
	 */
	public DCommands getDCommands() {
		return dCommands;
	}
	
	/**
	 * load / reload a new instance of DCommands
	 */
	public void loadDCommands() {
		dCommands = new DCommands();
	}
	
	/**
	 * @return the loaded instance of Dungeons
	 */
	public Dungeons getDungeons() {
		return dungeons;
	}
	
	/**
	 * load / reload a new instance of Dungeons
	 */
	public void loadDungeons() {
		dungeons = new Dungeons();
	}
	
	/**
	 * @return the chatSpyers
	 */
	public CopyOnWriteArrayList<Player> getChatSpyers() {
		return chatSpyers;
	}
	
	/**
	 * @return the dLootInventories
	 */
	public CopyOnWriteArrayList<DLootInventory> getDLootInventories() {
		return dLootInventories;
	}
	
	/**
	 * @return the editWorlds
	 */
	public CopyOnWriteArrayList<EditWorld> getEditWorlds() {
		return editWorlds;
	}
	
	/**
	 * @return the defaultConfig
	 */
	public WorldConfig getDefaultConfig() {
		return WorldConfig.defaultConfig;// TODO
	}
	
	/**
	 * @return the gameWorlds
	 */
	public CopyOnWriteArrayList<GameWorld> getGameWorlds() {
		return gameWorlds;
	}
	
	/**
	 * @return the groupSigns
	 */
	public CopyOnWriteArrayList<GroupSign> getGroupSigns() {
		return groupSigns;
	}
	
	/**
	 * @return the dPortals
	 */
	public CopyOnWriteArrayList<DPortal> getDPortals() {
		return dPortals;
	}
	
	/**
	 * @return the leaveSigns
	 */
	public CopyOnWriteArrayList<LeaveSign> getLeaveSigns() {
		return leaveSigns;
	}
	
	/**
	 * @return the dGroups
	 */
	public CopyOnWriteArrayList<DGroup> getDGroups() {
		return dGroups;
	}
	
	/**
	 * @return the dPlayers
	 */
	public CopyOnWriteArrayList<DPlayer> getDPlayers() {
		return dPlayers;
	}
	
}
