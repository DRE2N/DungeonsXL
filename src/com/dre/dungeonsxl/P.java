package com.dre.dungeonsxl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.CopyOnWriteArrayList;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.MinecraftServer;
import net.minecraft.server.v1_6_R2.PlayerInteractManager;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.dre.dungeonsxl.commands.DCommandRoot;
import com.dre.dungeonsxl.game.GameWorld;
import com.dre.dungeonsxl.listener.BlockListener;
import com.dre.dungeonsxl.listener.CommandListener;
import com.dre.dungeonsxl.listener.EntityListener;
import com.dre.dungeonsxl.listener.HangingListener;
import com.dre.dungeonsxl.listener.PlayerListener;
import com.dre.dungeonsxl.listener.WorldListener;

public class P extends JavaPlugin {
	public static P p;

	// Listener
	private static Listener entityListener;
	private static Listener playerListener;
	private static Listener blockListener;
	private static Listener worldListener;
	private static Listener hangingListener;

	// Main Config Reader
	public MainConfig mainConfig;

	// Language Reader
	public LanguageReader language;

	// Chatspyer
	public CopyOnWriteArrayList<Player> chatSpyer = new CopyOnWriteArrayList<Player>();

	// Spout
	public boolean isSpoutEnabled = false;

	@Override
	public void onEnable() {
		p = this;

		// Commands
		getCommand("dungeonsxl").setExecutor(new CommandListener());

		// Load Language
		language = new LanguageReader(new File(p.getDataFolder(), "languages/en.yml"));

		// Load Config
		mainConfig = new MainConfig(new File(p.getDataFolder(), "config.yml"));

		// Load Language 2
		language = new LanguageReader(new File(p.getDataFolder(), "languages/" + mainConfig.language + ".yml"));

		// Init Commands
		new DCommandRoot();

		// InitFolders
		this.initFolders();

		// Setup Permissions
		this.setupPermissions();

		// Listener
		entityListener = new EntityListener();
		playerListener = new PlayerListener();
		blockListener = new BlockListener();
		worldListener = new WorldListener();
		hangingListener = new HangingListener();

		Bukkit.getServer().getPluginManager().registerEvents(entityListener, this);
		Bukkit.getServer().getPluginManager().registerEvents(playerListener, this);
		Bukkit.getServer().getPluginManager().registerEvents(blockListener, this);
		Bukkit.getServer().getPluginManager().registerEvents(worldListener, this);
		Bukkit.getServer().getPluginManager().registerEvents(hangingListener, this);

		// Load All
		this.loadAll();

		// Spout
		if (mainConfig.enableSpout) {
			if (P.p.getServer().getPluginManager().getPlugin("Spout") != null) {
				isSpoutEnabled = true;
			} else {
				isSpoutEnabled = false;
				mainConfig.enableSpout = false;
				P.p.log(P.p.language.get("Log_Error_Spout"));
			}
		}

		// Scheduler
		this.initSchedulers();

		// MSG
		this.log(this.getDescription().getName() + " enabled!");
	}

	@Override
	public void onDisable() {
		// Save
		this.saveData();
		language.save();

		// DPlayer leaves World
		for (DPlayer dplayer : DPlayer.players) {
			dplayer.leave();
		}

		// Delete all Data
		DGroup.dgroups.clear();
		DGSign.dgsigns.clear();
		DLootInventory.LootInventorys.clear();
		DPlayer.players.clear();
		DPortal.portals.clear();
		LeaveSign.lsigns.clear();
		DCommandRoot.root.commands.clear();

		// Delete Worlds
		GameWorld.deleteAll();
		EditWorld.deleteAll();

		// Disable listeners
		HandlerList.unregisterAll(p);

		// Stop shedulers
		p.getServer().getScheduler().cancelTasks(this);

		// MSG
		this.log(this.getDescription().getName() + " disabled!");
	}

	// Init.
	public void initFolders() {
		// Check Folder
		File folder = new File(this.getDataFolder() + "");
		if (!folder.exists()) {
			folder.mkdir();
		}

		folder = new File(this.getDataFolder() + File.separator + "dungeons");
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	public void initSchedulers() {
		p.getServer().getScheduler().scheduleSyncRepeatingTask(p, new Runnable() {
			public void run() {
				for (GameWorld gworld : GameWorld.gworlds) {
					if (gworld.world.getPlayers().isEmpty()) {
						if (DPlayer.get(gworld.world).isEmpty()) {
							gworld.delete();
						}
					}
				}
				for (EditWorld eworld : EditWorld.eworlds) {
					if (eworld.world.getPlayers().isEmpty()) {
						eworld.delete();
					}
				}
			}
		}, 0L, 1200L);

		p.getServer().getScheduler().scheduleSyncRepeatingTask(p, new Runnable() {
			public void run() {
				GameWorld.update();
				DPlayer.update(true);
			}
		}, 0L, 20L);

		p.getServer().getScheduler().scheduleSyncRepeatingTask(p, new Runnable() {
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
		return (permission != null);
	}

	public Boolean GroupEnabled(String group) {

		for (String agroup : permission.getGroups()) {
			if (agroup.equalsIgnoreCase(group)) {
				return true;
			}
		}

		return false;
	}

	// Save and Load
	public void saveData() {
		File file = new File(this.getDataFolder(), "data.yml");
		FileConfiguration configFile = new YamlConfiguration();

		DPortal.save(configFile);
		DGSign.save(configFile);
		LeaveSign.save(configFile);

		try {
			configFile.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadAll() {
		// Load world data
		File file = new File(this.getDataFolder(), "data.yml");
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);

		DPortal.load(configFile);
		DGSign.load(configFile);
		LeaveSign.load(configFile);

		// Load saved players
		DSavePlayer.load();

		// Check Worlds
		this.checkWorlds();
	}

	public void checkWorlds() {
		File serverDir = new File(".");

		for (File file : serverDir.listFiles()) {
			if (file.getName().contains("DXL_Edit_") && file.isDirectory()) {
				for (File dungeonFile : file.listFiles()) {
					if (dungeonFile.getName().contains(".id_")) {
						String dungeonName = dungeonFile.getName().substring(4);
						this.copyDirectory(file, new File(p.getDataFolder(), "/dungeons/" + dungeonName));
						this.deletenotusingfiles(new File(p.getDataFolder(), "/dungeons/" + dungeonName));
					}
				}

				this.removeDirectory(file);
			} else if (file.getName().contains("DXL_Game_") && file.isDirectory()) {
				this.removeDirectory(file);
			}
		}
	}

	// File Control
	public boolean removeDirectory(File directory) {
		if (directory.isDirectory()) {
			for (File f : directory.listFiles()) {
				if (!removeDirectory(f))
					return false;
			}
		}
		return directory.delete();
	}

	public void copyFile(File in, File out) throws IOException {
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			inChannel = new FileInputStream(in).getChannel();
			outChannel = new FileOutputStream(out).getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	public String[] excludedFiles = { "config.yml", "uid.dat", "DXLData.data" };

	public void copyDirectory(File sourceLocation, File targetLocation) {
		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				boolean isOk = true;
				for (String excluded : excludedFiles) {
					if (children[i].contains(excluded)) {
						isOk = false;
						break;
					}
				}
				if (isOk) {
					copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
				}
			}
		} else {
			try {
				if (!targetLocation.getParentFile().exists()) {

					createDirectory(targetLocation.getParentFile().getAbsolutePath());
					targetLocation.createNewFile();

				} else if (!targetLocation.exists()) {

					targetLocation.createNewFile();
				}

				InputStream in = new FileInputStream(sourceLocation);
				OutputStream out = new FileOutputStream(targetLocation);

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				in.close();
				out.close();
			} catch (Exception e) {
				if (e.getMessage().contains("Zugriff") || e.getMessage().contains("Access"))
					P.p.log("Error: " + e.getMessage() + " // Access denied");
				else
					P.p.log("Error: " + e.getMessage());
			}
		}
	}

	public void createDirectory(String s) {

		if (!new File(s).getParentFile().exists()) {

			createDirectory(new File(s).getParent());
		}

		new File(s).mkdir();
	}

	public void deletenotusingfiles(File directory) {
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.getName().equalsIgnoreCase("uid.dat") || file.getName().contains(".id_")) {
				file.delete();
			}
		}
	}

	// Msg
	public void msg(CommandSender sender, String msg) {
		msg = replaceColors(msg);
		sender.sendMessage(ChatColor.DARK_GREEN + "[DXL] " + ChatColor.WHITE + msg);
	}

	public void msg(CommandSender sender, String msg, boolean zusatz) {
		msg = replaceColors(msg);
		if (zusatz) {
			sender.sendMessage(ChatColor.DARK_GREEN + "[DXL]" + ChatColor.WHITE + msg);
		} else {
			sender.sendMessage(ChatColor.WHITE + msg);
		}
	}

	private String replaceColors(String msg) {
		if (msg != null) {
			msg = msg.replace("&0", ChatColor.getByChar("0").toString());
			msg = msg.replace("&1", ChatColor.getByChar("1").toString());
			msg = msg.replace("&2", ChatColor.getByChar("2").toString());
			msg = msg.replace("&3", ChatColor.getByChar("3").toString());
			msg = msg.replace("&4", ChatColor.getByChar("4").toString());
			msg = msg.replace("&5", ChatColor.getByChar("5").toString());
			msg = msg.replace("&6", ChatColor.getByChar("6").toString());
			msg = msg.replace("&7", ChatColor.getByChar("7").toString());
			msg = msg.replace("&8", ChatColor.getByChar("8").toString());
			msg = msg.replace("&9", ChatColor.getByChar("9").toString());
			msg = msg.replace("&a", ChatColor.getByChar("a").toString());
			msg = msg.replace("&b", ChatColor.getByChar("b").toString());
			msg = msg.replace("&c", ChatColor.getByChar("c").toString());
			msg = msg.replace("&d", ChatColor.getByChar("d").toString());
			msg = msg.replace("&e", ChatColor.getByChar("e").toString());
			msg = msg.replace("&f", ChatColor.getByChar("f").toString());
			msg = msg.replace("&k", ChatColor.getByChar("k").toString());
			msg = msg.replace("&l", ChatColor.getByChar("l").toString());
			msg = msg.replace("&m", ChatColor.getByChar("m").toString());
			msg = msg.replace("&n", ChatColor.getByChar("n").toString());
			msg = msg.replace("&o", ChatColor.getByChar("o").toString());
			msg = msg.replace("&r", ChatColor.getByChar("r").toString());
		}

		return msg;
	}

	// Misc.
	public EntityType getEntitiyType(String name) {
		for (EntityType type : EntityType.values()) {
			if (name.equalsIgnoreCase(type.getName())) {
				return type;
			}
		}

		return null;
	}

	public int parseInt(String string) {
		return NumberUtils.toInt(string, 0);
	}

	public Player getOfflinePlayer(String player) {
		Player pplayer = null;
		try {
			// See if the player has data files

			// Find the player folder
			File playerfolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");

			// Find player name
			for (File playerfile : playerfolder.listFiles()) {
				String filename = playerfile.getName();
				String playername = filename.substring(0, filename.length() - 4);

				if (playername.trim().equalsIgnoreCase(player)) {
					// This player plays on the server!
					MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
					EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), playername, new PlayerInteractManager(server.getWorldServer(0)));
					Player target = (entity == null) ? null : (Player) entity.getBukkitEntity();
					if (target != null) {
						target.loadData();
						return target;
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
		return pplayer;
	}

	public Player getOfflinePlayer(String player, Location location) {
		Player pplayer = null;
		try {
			// See if the player has data files

			// Find the player folder
			File playerfolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");

			// Find player name
			for (File playerfile : playerfolder.listFiles()) {
				String filename = playerfile.getName();
				String playername = filename.substring(0, filename.length() - 4);

				if (playername.trim().equalsIgnoreCase(player)) {
					// This player plays on the server!
					MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
					EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), playername, new PlayerInteractManager(server.getWorldServer(0)));
					entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
					entity.world = ((CraftWorld) location.getWorld()).getHandle();
					Player target = (entity == null) ? null : (Player) entity.getBukkitEntity();
					if (target != null) {
						// target.loadData();
						return target;
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
		return pplayer;
	}

	// -------------------------------------------- //
	// LOGGING
	// -------------------------------------------- //
	public void log(String msg) {
		this.msg(Bukkit.getConsoleSender(), msg);
	}
}
