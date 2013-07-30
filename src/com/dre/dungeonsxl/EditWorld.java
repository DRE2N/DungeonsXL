package com.dre.dungeonsxl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class EditWorld {
	private static P p = P.p;
	public static CopyOnWriteArrayList<EditWorld> eworlds = new CopyOnWriteArrayList<EditWorld>();

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
		eworlds.add(this);

		// ID
		this.id = -1;
		int i = -1;
		while (this.id == -1) {
			i++;
			boolean exist = false;
			for (EditWorld eworld : eworlds) {
				if (eworld.id == i) {
					exist = true;
					break;
				}
			}
			if (!exist)
				this.id = i;
		}

		name = "DXL_Edit_" + this.id;
	}

	public void generate() {
		WorldCreator creator = WorldCreator.name(name);
		creator.type(WorldType.FLAT);
		creator.generateStructures(false);

		this.world = p.getServer().createWorld(creator);
	}

	public void save() {
		this.world.save();
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(p.getDataFolder(), "/dungeons/" + this.dungeonname + "/DXLData.data")));
			out.writeInt(this.sign.size());
			for (Block sign : this.sign) {
				out.writeInt(sign.getX());
				out.writeInt(sign.getY());
				out.writeInt(sign.getZ());
			}
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void checkSign(Block block) {
		if ((block.getState() instanceof Sign)) {
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();

			if (lines[0].equalsIgnoreCase("[lobby]")) {
				this.lobby = block.getLocation();
			}
		}
	}

	public void delete() {
		eworlds.remove(this);
		for (Player player : this.world.getPlayers()) {
			DPlayer dplayer = DPlayer.get(player);
			dplayer.leave();
		}

		p.getServer().unloadWorld(this.world, true);
		File dir = new File("DXL_Edit_" + this.id);
		p.copyDirectory(dir, new File(p.getDataFolder(), "/dungeons/" + this.dungeonname));
		p.deletenotusingfiles(new File(p.getDataFolder(), "/dungeons/" + this.dungeonname));
		p.removeDirectory(dir);
	}

	public void deleteNoSave() {
		eworlds.remove(this);
		for (Player player : this.world.getPlayers()) {
			DPlayer dplayer = DPlayer.get(player);
			dplayer.leave();
		}

		File dir = new File("DXL_Edit_" + this.id);
		p.copyDirectory(dir, new File(p.getDataFolder(), "/dungeons/" + this.dungeonname));
		p.deletenotusingfiles(new File(p.getDataFolder(), "/dungeons/" + this.dungeonname));
		p.getServer().unloadWorld(this.world, true);
		p.removeDirectory(dir);
	}

	// Static
	public static EditWorld get(World world) {
		for (EditWorld eworld : eworlds) {
			if (eworld.world.equals(world)) {
				return eworld;
			}
		}

		return null;
	}

	public static EditWorld get(String name) {
		for (EditWorld eworld : eworlds) {
			if (eworld.dungeonname.equalsIgnoreCase(name)) {
				return eworld;
			}
		}

		return null;
	}

	public static void deleteAll() {
		for (EditWorld eworld : eworlds) {
			eworld.delete();
		}
	}

	public static EditWorld load(String name) {
		for (EditWorld eworld : eworlds) {

			if (eworld.dungeonname.equalsIgnoreCase(name)) {
				return eworld;
			}
		}

		File file = new File(p.getDataFolder(), "/dungeons/" + name);

		if (file.exists()) {
			EditWorld eworld = new EditWorld();
			eworld.dungeonname = name;
			// World
			p.copyDirectory(file, new File("DXL_Edit_" + eworld.id));

			// Id File
			File idFile = new File("DXL_Edit_" + eworld.id + "/.id_" + name);
			try {
				idFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			eworld.world = p.getServer().createWorld(WorldCreator.name("DXL_Edit_" + eworld.id));

			try {
				ObjectInputStream os = new ObjectInputStream(new FileInputStream(new File(p.getDataFolder(), "/dungeons/" + eworld.dungeonname + "/DXLData.data")));
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
		for (EditWorld eworld : eworlds) {
			if (eworld.dungeonname.equalsIgnoreCase(name)) {
				return true;
			}
		}

		// Cheack Unloaded Worlds
		File file = new File(p.getDataFolder(), "/dungeons/" + name);

		if (file.exists()) {
			return true;
		}

		return false;
	}

	public void msg(String msg) {
		for (DPlayer dplayer : DPlayer.get(this.world)) {
			p.msg(dplayer.player, msg);
		}
	}

	// Invite
	public static boolean addInvitedPlayer(String eworldname, String player) {
		if (exist(eworldname)) {
			DConfig config = new DConfig(new File(p.getDataFolder() + "/dungeons/" + eworldname, "config.yml"));
			config.addInvitedPlayer(player.toLowerCase());
			config.save();
			return true;
		}

		return false;
	}

	public static boolean removeInvitedPlayer(String eworldname, String name) {

		if (exist(eworldname)) {
			DConfig config = new DConfig(new File(p.getDataFolder() + "/dungeons/" + eworldname, "config.yml"));
			config.removeInvitedPlayers(name.toLowerCase());
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

	public static boolean isInvitedPlayer(String eworldname, String player) {
		if (exist(eworldname)) {
			DConfig config = new DConfig(new File(p.getDataFolder() + "/dungeons/" + eworldname, "config.yml"));
			return config.getInvitedPlayers().contains(player.toLowerCase());
		}

		return false;

	}
}
