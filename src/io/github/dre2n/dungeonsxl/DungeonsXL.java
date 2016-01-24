package io.github.dre2n.dungeonsxl;

import io.github.dre2n.dungeonsxl.command.DCommands;
import io.github.dre2n.dungeonsxl.config.MainConfig;
import io.github.dre2n.dungeonsxl.config.MessageConfig;
import io.github.dre2n.dungeonsxl.config.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.DLootInventory;
import io.github.dre2n.dungeonsxl.dungeon.Dungeons;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
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
import io.github.dre2n.dungeonsxl.requirement.Requirements;
import io.github.dre2n.dungeonsxl.reward.Rewards;
import io.github.dre2n.dungeonsxl.sign.DSigns;
import io.github.dre2n.dungeonsxl.task.LazyUpdateTask;
import io.github.dre2n.dungeonsxl.task.UpdateTask;
import io.github.dre2n.dungeonsxl.task.WorldUnloadTask;
import io.github.dre2n.dungeonsxl.trigger.Triggers;
import io.github.dre2n.dungeonsxl.util.FileUtil;
import io.github.dre2n.dungeonsxl.util.VersionUtil;
import io.github.dre2n.dungeonsxl.util.VersionUtil.Internals;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class DungeonsXL extends JavaPlugin {
	
	private static DungeonsXL plugin;
	private Economy economyProvider;
	private Permission permissionProvider;
	
	private MainConfig mainConfig;
	private MessageConfig messageConfig;
	
	private VersionUtil versionUtil;
	
	private DCommands dCommands;
	private DSigns dSigns;
	private Requirements requirements;
	private Rewards rewards;
	private Triggers triggers;
	private Dungeons dungeons;
	
	private BukkitTask worldUnloadTask;
	private BukkitTask lazyUpdateTask;
	private BukkitTask updateTask;
	
	private CopyOnWriteArrayList<Player> inBreakMode = new CopyOnWriteArrayList<Player>();
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
		
		// InitFolders
		initFolders();
		
		// Load Language
		loadMessageConfig(new File(plugin.getDataFolder(), "languages/en.yml"));
		// Load Config
		loadMainConfig(new File(plugin.getDataFolder(), "config.yml"));
		// Load Language 2
		loadMessageConfig(new File(plugin.getDataFolder(), "languages/" + mainConfig.getLanguage() + ".yml"));
		loadVersionUtil();
		loadDCommands();
		loadRequirements();
		loadRewards();
		loadTriggers();
		loadDSigns();
		loadDungeons();
		
		// Setup Permissions
		loadPermissionProvider();
		
		// Setup Economy
		loadEconomyProvider();
		
		getCommand("dungeonsxl").setExecutor(new CommandListener());
		getServer().getPluginManager().registerEvents(new EntityListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new BlockListener(), this);
		getServer().getPluginManager().registerEvents(new WorldListener(), this);
		getServer().getPluginManager().registerEvents(new HangingListener(), this);
		
		// Load All
		loadAll();
		
		// Tasks
		startWorldUnloadTask(1200L);
		startLazyUpdateTask(20L);
		startUpdateTask(20L);
		
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
		messageConfig.save();
		
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
						FileUtil.deleteUnusedFiles(new File(plugin.getDataFolder(), "/maps/" + dungeonName));
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
	 * @return the loaded instance of Economy
	 */
	public Economy getEconomyProvider() {
		return economyProvider;
	}
	
	/**
	 * load / reload a new instance of Permission
	 */
	public void loadEconomyProvider() {
		try {
			if (mainConfig.enableEconomy()) {
				RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
				if (economyProvider != null) {
					this.economyProvider = economyProvider.getProvider();
				}
			}
			
		} catch (NoClassDefFoundError error) {
			getLogger().info("Could not hook into Vault to register an economy provider!");
		}
	}
	
	/**
	 * @return the loaded instance of Permission
	 */
	public Permission getPermissionProvider() {
		return permissionProvider;
	}
	
	/**
	 * load / reload a new instance of Permission
	 */
	public void loadPermissionProvider() {
		try {
			RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
			if (permissionProvider != null) {
				this.permissionProvider = permissionProvider.getProvider();
			}
			
		} catch (NoClassDefFoundError error) {
			getLogger().info("Could not hook into Vault to register a permission provider!");
		}
	}
	
	/**
	 * @param group
	 * the group to be checked
	 */
	public boolean isGroupEnabled(String group) {
		for (String agroup : permissionProvider.getGroups()) {
			if (agroup.equalsIgnoreCase(group)) {
				return true;
			}
		}
		
		return false;
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
	 * @return the loaded instance of MessageConfig
	 */
	public MessageConfig getMessageConfig() {
		return messageConfig;
	}
	
	/**
	 * load / reload a new instance of MessageConfig
	 */
	public void loadMessageConfig(File file) {
		messageConfig = new MessageConfig(file);
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
	 * @return the dSigns
	 */
	public DSigns getDSigns() {
		return dSigns;
	}
	
	/**
	 * load / reload a new instance of DSigns
	 */
	public void loadDSigns() {
		dSigns = new DSigns();
	}
	
	/**
	 * @return the requirements
	 */
	public Requirements getRequirements() {
		return requirements;
	}
	
	/**
	 * load / reload a new instance of Requirements
	 */
	public void loadRequirements() {
		requirements = new Requirements();
	}
	
	/**
	 * @return the rewards
	 */
	public Rewards getRewards() {
		return rewards;
	}
	
	/**
	 * load / reload a new instance of Rewards
	 */
	public void loadRewards() {
		rewards = new Rewards();
	}
	
	/**
	 * @return the triggers
	 */
	public Triggers getTriggers() {
		return triggers;
	}
	
	/**
	 * load / reload a new instance of Triggers
	 */
	public void loadTriggers() {
		triggers = new Triggers();
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
	 * @return the worldUnloadTask
	 */
	public BukkitTask getWorldUnloadTask() {
		return worldUnloadTask;
	}
	
	/**
	 * start a new WorldUnloadTask
	 */
	public void startWorldUnloadTask(long period) {
		worldUnloadTask = new WorldUnloadTask().runTaskTimer(this, 0L, period);
	}
	
	/**
	 * @return the lazyUpdateTask
	 */
	public BukkitTask getLazyUpdateTask() {
		return lazyUpdateTask;
	}
	
	/**
	 * start a new LazyUpdateTask
	 */
	public void startLazyUpdateTask(long period) {
		lazyUpdateTask = new LazyUpdateTask().runTaskTimer(this, 0L, period);
	}
	
	/**
	 * @return the updateTask
	 */
	public BukkitTask getUpdateTask() {
		return updateTask;
	}
	
	/**
	 * start a new LazyUpdateTask
	 */
	public void startUpdateTask(long period) {
		updateTask = new UpdateTask().runTaskTimer(this, 0L, period);
	}
	
	/**
	 * @return the inBreakMode
	 */
	public CopyOnWriteArrayList<Player> getInBreakMode() {
		return inBreakMode;
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
