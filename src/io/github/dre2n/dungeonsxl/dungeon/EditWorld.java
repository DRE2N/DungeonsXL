package io.github.dre2n.dungeonsxl.dungeon;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.FileUtil;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

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
	public World world;
	public String owner;
	public String name;
	public String dungeonname;
	public int id;
	public Location lobby;
	public CopyOnWriteArrayList<String> invitedPlayers = new CopyOnWriteArrayList<String>();
	public CopyOnWriteArrayList<Block> sign = new CopyOnWriteArrayList<Block>();
	
	public EditWorld() {
		plugin.getEditWorlds().add(this);
		
		// ID
		id = -1;
		int i = -1;
		while (id == -1) {
			i++;
			boolean exist = false;
			for (EditWorld eworld : plugin.getEditWorlds()) {
				if (eworld.id == i) {
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
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(plugin.getDataFolder(), "/maps/" + dungeonname + "/DXLData.data")));
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
			DPlayer dplayer = DPlayer.get(player);
			dplayer.leave();
		}
		
		plugin.getServer().unloadWorld(world, true);
		File dir = new File("DXL_Edit_" + id);
		FileUtil.copyDirectory(dir, new File(plugin.getDataFolder(), "/maps/" + dungeonname));
		FileUtil.deletenotusingfiles(new File(plugin.getDataFolder(), "/maps/" + dungeonname));
		FileUtil.removeDirectory(dir);
	}
	
	public void deleteNoSave() {
		plugin.getEditWorlds().remove(this);
		for (Player player : world.getPlayers()) {
			DPlayer dplayer = DPlayer.get(player);
			dplayer.leave();
		}
		
		File dir = new File("DXL_Edit_" + id);
		FileUtil.copyDirectory(dir, new File(plugin.getDataFolder(), "/maps/" + dungeonname));
		FileUtil.deletenotusingfiles(new File(plugin.getDataFolder(), "/maps/" + dungeonname));
		plugin.getServer().unloadWorld(world, true);
		FileUtil.removeDirectory(dir);
	}
	
	// Static
	public static EditWorld get(World world) {
		for (EditWorld eworld : plugin.getEditWorlds()) {
			if (eworld.world.equals(world)) {
				return eworld;
			}
		}
		
		return null;
	}
	
	public static EditWorld get(String name) {
		for (EditWorld eworld : plugin.getEditWorlds()) {
			if (eworld.dungeonname.equalsIgnoreCase(name)) {
				return eworld;
			}
		}
		
		return null;
	}
	
	public static void deleteAll() {
		for (EditWorld eworld : plugin.getEditWorlds()) {
			eworld.delete();
		}
	}
	
	public static EditWorld load(String name) {
		for (EditWorld eworld : plugin.getEditWorlds()) {
			
			if (eworld.dungeonname.equalsIgnoreCase(name)) {
				return eworld;
			}
		}
		
		File file = new File(plugin.getDataFolder(), "/maps/" + name);
		
		if (file.exists()) {
			EditWorld eworld = new EditWorld();
			eworld.dungeonname = name;
			// World
			FileUtil.copyDirectory(file, new File("DXL_Edit_" + eworld.id));
			
			// Id File
			File idFile = new File("DXL_Edit_" + eworld.id + "/.id_" + name);
			try {
				idFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			eworld.world = plugin.getServer().createWorld(WorldCreator.name("DXL_Edit_" + eworld.id));
			
			try {
				ObjectInputStream os = new ObjectInputStream(new FileInputStream(new File(plugin.getDataFolder(), "/maps/" + eworld.dungeonname + "/DXLData.data")));
				int length = os.readInt();
				for (int i = 0; i < length; i++) {
					int x = os.readInt();
					int y = os.readInt();
					int z = os.readInt();
					Block block = eworld.world.getBlockAt(x, y, z);
					eworld.checkSign(block);
					eworld.sign.add(block);
				}
				os.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return eworld;
		}
		
		return null;
	}
	
	public static boolean exist(String name) {
		// Cheack Loaded EditWorlds
		for (EditWorld eworld : plugin.getEditWorlds()) {
			if (eworld.dungeonname.equalsIgnoreCase(name)) {
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
		for (DPlayer dplayer : DPlayer.get(world)) {
			MessageUtil.sendMessage(dplayer.player, msg);
		}
	}
	
	// Invite
	public static boolean addInvitedPlayer(String eworldname, UUID uuid) {
		if (exist(eworldname)) {
			WorldConfig config = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + eworldname, "config.yml"));
			config.addInvitedPlayer(uuid.toString());
			config.save();
			return true;
		}
		
		return false;
	}
	
	public static boolean removeInvitedPlayer(String eworldname, UUID uuid, String name) {
		
		if (exist(eworldname)) {
			WorldConfig config = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + eworldname, "config.yml"));
			config.removeInvitedPlayers(uuid.toString(), name.toLowerCase());
			config.save();
			
			// Kick Player
			EditWorld eworld = EditWorld.get(eworldname);
			if (eworld != null) {
				DPlayer player = DPlayer.get(name);
				
				if (player != null) {
					if (eworld.world.getPlayers().contains(player.player)) {
						player.leave();
					}
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public static boolean isInvitedPlayer(String eworldname, UUID uuid, String name) {
		if (exist(eworldname)) {
			WorldConfig config = new WorldConfig(new File(plugin.getDataFolder() + "/maps/" + eworldname, "config.yml"));
			// get player from both a 0.9.1 and lower and 0.9.2 and higher file
			if (config.getInvitedPlayers().contains(name.toLowerCase()) || config.getInvitedPlayers().contains(uuid.toString())) {
				return true;
			}
		}
		
		return false;
	}
	
}
