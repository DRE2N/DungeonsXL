package com.dre.dungeonsxl;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LeaveSign {
	public static CopyOnWriteArrayList<LeaveSign> lsigns = new CopyOnWriteArrayList<LeaveSign>();

	public Sign sign;

	public LeaveSign(Sign sign) {
		lsigns.add(this);

		this.sign = sign;
		this.setText();
	}

	public void setText() {
		this.sign.setLine(0, ChatColor.BLUE + "############");
		this.sign.setLine(1, ChatColor.DARK_GREEN + "Leave");
		this.sign.setLine(2, "");
		this.sign.setLine(3, ChatColor.BLUE + "############");
		this.sign.update();
	}

	public static boolean playerInteract(Block block, Player player) {

		LeaveSign lsign = getSign(block);

		if (lsign != null) {
			DPlayer dplayer = DPlayer.get(player);
			if (dplayer != null) {
				dplayer.leave();
				return true;
			} else {
				DGroup dgroup = DGroup.get(player);
				if (dgroup != null) {
					dgroup.removePlayer(player);
					P.p.msg(player, P.p.language.get("Player_LeaveGroup"));// ChatColor.YELLOW+"Du hast deine Gruppe erfolgreich verlassen!");
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isRelativeSign(Block block, int x, int z) {
		LeaveSign lsign = getSign(block.getRelative(x, 0, z));
		if (lsign != null) {
			if (x == -1 && lsign.sign.getData().getData() == 4)
				return true;
			if (x == 1 && lsign.sign.getData().getData() == 5)
				return true;
			if (z == -1 && lsign.sign.getData().getData() == 2)
				return true;
			if (z == 1 && lsign.sign.getData().getData() == 3)
				return true;
		}

		return false;
	}

	public static LeaveSign getSign(Block block) {
		if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
			for (LeaveSign leavesign : lsigns) {
				if (block.getWorld() == leavesign.sign.getWorld()) {
					if (block.getLocation().distance(leavesign.sign.getBlock().getLocation()) < 1) {
						return leavesign;
					}
				}
			}
		}
		return null;
	}

	// Save and Load
	public static void save(FileConfiguration configFile) {
		int id = 0;
		for (LeaveSign lsign : lsigns) {
			id++;
			String preString = "leavesign." + lsign.sign.getWorld().getName() + "." + id;
			configFile.set(preString + ".x", lsign.sign.getX());
			configFile.set(preString + ".y", lsign.sign.getY());
			configFile.set(preString + ".z", lsign.sign.getZ());
		}
	}

	public static void load(FileConfiguration configFile) {
		for (World world : P.p.getServer().getWorlds()) {
			if (configFile.contains("leavesign." + world.getName())) {
				int id = 0;
				String preString;
				do {
					id++;
					preString = "leavesign." + world.getName() + "." + id + ".";
					if (configFile.contains(preString)) {
						Block block = world.getBlockAt(configFile.getInt(preString + ".x"), configFile.getInt(preString + ".y"), configFile.getInt(preString + ".z"));
						if (block.getState() instanceof Sign) {
							Sign sign = (Sign) block.getState();
							new LeaveSign(sign);
						}
					}
				} while (configFile.contains(preString));
			}
		}
	}

}
