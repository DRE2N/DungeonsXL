package io.github.dre2n.dungeonsxl.dungeon;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.FileUtil;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class EditWorld {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	// Variables
	private World world;
	private String owner;
	private String name;
	private String mapName;
	private int id;
	private Location lobby;
	private CopyOnWriteArrayList<String> invitedPlayers = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<Block> sign = new CopyOnWriteArrayList<Block>();
	
	public EditWorld() {
		plugin.getEditWorlds().add(this);
		
		// ID
		id = -1;
		int i = -1;
		while (id == -1) {
			i++;
			boolean exist = false;
			for (EditWorld editWorld : plugin.getEditWorlds()) {
				if (editWorld.id == i) {
					exist = true;
					break;
				}
			}
			if ( !exist) {
				id = i;
			}
		}
		
		name = "DXL_Edit_" + id;
	}
	
	public void generate() {
		WorldCreator creator = WorldCreator.name(name);
		creator.type(WorldType.FLAT);
		creator.generateStructures(false);
		
		world = plugin.getServer().createWorld(creator);
	}
	
	public void save() {
		world.save();
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(plugin.getDataFolder(), "/maps/" + mapName + "/DXLData.data")));
			out.writeInt(sign.size());
			for (Block sign : this.sign) {
				out.writeInt(sign.getX());
				out.writeInt(sign.getY());
				out.writeInt(sign.getZ());
			}
			out.close();
			
		} catch (IOException exception) {
		}
	}
	
	public void checkSign(Block block) {
		if (block.getState() instanceof Sign) {
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();
			
			if (lines[0].equalsIgnoreCase("[lobby]")) {
				lobby = block.getLocation();
			}
		}
	}
	
	public void delete() {
		plugin.getEditWorlds().remove(this);
		for (Player player : world.getPlayers()) {
			DPlayer dPlayer = DPlayer.getByPlayer(player);
			dPlayer.leave();
		}
		
		plugin.getServer().unloadWorld(world, true);
		File dir = new File("DXL_Edit_" + id);
		FileUtil.copyDirectory(dir, new File(plugin.getDataFolder(), "/maps/" + mapName));
		FileUtil.deleteUnusedFiles(new File(plugin.getDataFolder(), "/maps/" + mapName));
		FileUtil.removeDirectory(dir);
	}
	
	public void deleteNoSave() {
		plugin.getEditWorlds().remove(this);
		for (Player player : world.getPlayers()) {
			DPlayer dPlayer = DPlayer.getByPlayer(player);
			dPlayer.leave();
		}
		
		File dir = new File("DXL_Edit_" + id);
		FileUtil.copyDirectory(dir, new File(plugin.getDataFolder(), "/maps/" + mapName));
		FileUtil.deleteUnusedFiles(new File(plugin.getDataFolder(), "/maps/" + mapName));
		plugin.getServer().unloadWorld(world, true);
		FileUtil.removeDirectory(dir);
	}
	
	// Static
	public static EditWorld getByWorld(World world) {
		for (EditWorld editWorld : plugin.getEditWorlds()) {
			if (editWorld.world.equals(world)) {
				return editWorld;
			}
		}
		
		return null;
	}
	
	public static EditWorld getByName(String name) {
		for (EditWorld editWorld : plugin.getEditWorlds()) {
			if (editWorld.mapName.equalsIgnoreCase(name)) {
				return editWorld;
			}
		}
		
		return null;
	}
	
	public static void deleteAll() {
		for (EditWorld editWorld : plugin.getEditWorlds()) {
			editWorld.delete();
		}
	}
	
	public static EditWorld load(String name) {
		for (EditWorld editWorld : plugin.getEditWorlds()) {
			
			if (editWorld.mapName.equalsIgnoreCase(name)) {
				return editWorld;
			}
		}
		
		File file = new File(plugin.getDataFolder(), "/maps/" + name);
		
		if (file.exists()) {
			EditWorld editWorld = new EditWorld();
			editWorld.mapName = name;
			// World
			FileUtil.copyDirectory(file, new File("DXL_Edit_" + editWorld.id));
			
			// Id File
			File idFile = new File("DXL_Edit_" + editWorld.id + "/.id_" + name);
			try {
				idFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			editWorld.world = plugin.getServer().createWorld(WorldCreator.name("DXL_Edit_" + editWorld.id));
			
			try {
				ObjectInputStream os = new ObjectInputStream(new FileInputStream(new File(plugin.getDataFolder(), "/maps/" + editWorld.mapName + "/DXLData.data")));
				int length = os.readInt();
				for (int i = 0; i < length; i++) {
					int x = os.readInt();
					int y = os.readInt();
					int z = os.readInt();
					Block block = editWorld.world.getBlockAt(x, y, z);
					editWorld.checkSign(block);
					editWorld.sign.add(block);
				}
				os.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return editWorld;
		}
		
		return null;
	}
	
	public static boolean exist(String name) {
		// Cheack Loaded EditWorlds
		for (EditWorld editWorld : plugin.getEditWorlds()) {
			if (editWorld.mapName.equalsIgnoreCase(name)) {
				return true;
			}
		}
		
		// Cheack Unloaded Worlds
		File file = new File(plugin.getDataFolder(), "/maps/" + name);
		
		if (file.exists()) {
			return true;
		}
		
		return false;
	}
	
	public void msg(String msg) {
		for (DPlayer dPlayer : DPlayer.getByWorld(world)) {
			MessageUtil.sendMessage(dPlayer.getPlayer(), msg);
		}
	}
	
	// Invite
	public static boolean addInvitedPlayer(String editWorldName, UUID uuid) {
		if (exist(editWorldName)) {
			WorldConfig config = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + editWorldName, "config.yml"));
			config.addInvitedPlayer(uuid.toString());
			config.save();
			return true;
		}
		
		return false;
	}
	
	public static boolean removeInvitedPlayer(String editWorldName, UUID uuid, String name) {
		if (exist(editWorldName)) {
			WorldConfig config = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + editWorldName, "config.yml"));
			config.removeInvitedPlayers(uuid.toString(), name.toLowerCase());
			config.save();
			
			// Kick Player
			EditWorld editWorld = EditWorld.getByName(editWorldName);
			if (editWorld != null) {
				DPlayer player = DPlayer.getByName(name);
				
				if (player != null) {
					if (editWorld.world.getPlayers().contains(player.getPlayer())) {
						player.leave();
					}
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public static boolean isInvitedPlayer(String editWorldName, UUID uuid, String name) {
		if ( !exist(editWorldName)) {
			return false;
		}
		
		WorldConfig config = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + editWorldName, "config.yml"));
		// get player from both a 0.9.1 and lower and 0.9.2 and higher file
		if (config.getInvitedPlayers().contains(name.toLowerCase()) || config.getInvitedPlayers().contains(uuid.toString())) {
			return true;
			
		} else {
			return false;
		}
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
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}
	
	/**
	 * @param owner
	 * the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name
	 * the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the mapName
	 */
	public String getMapName() {
		return mapName;
	}
	
	/**
	 * @param mapName
	 * the mapName to set
	 */
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param id
	 * the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the lobby
	 */
	public Location getLobby() {
		return lobby;
	}
	
	/**
	 * @param lobby
	 * the lobby to set
	 */
	public void setLobby(Location lobby) {
		this.lobby = lobby;
	}
	
	/**
	 * @return the invitedPlayers
	 */
	public CopyOnWriteArrayList<String> getInvitedPlayers() {
		return invitedPlayers;
	}
	
	/**
	 * @param invitedPlayers
	 * the invitedPlayers to set
	 */
	public void setInvitedPlayers(CopyOnWriteArrayList<String> invitedPlayers) {
		this.invitedPlayers = invitedPlayers;
	}
	
	/**
	 * @return the sign
	 */
	public CopyOnWriteArrayList<Block> getSign() {
		return sign;
	}
	
	/**
	 * @param sign
	 * the sign to set
	 */
	public void setSign(CopyOnWriteArrayList<Block> sign) {
		this.sign = sign;
	}
	
}
