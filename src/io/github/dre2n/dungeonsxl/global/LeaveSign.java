package io.github.dre2n.dungeonsxl.global;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LeaveSign {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	private Sign sign;
	
	public LeaveSign(Sign sign) {
		plugin.getLeaveSigns().add(this);
		
		this.sign = sign;
		setText();
	}
	
	public void setText() {
		sign.setLine(0, ChatColor.BLUE + "############");
		sign.setLine(1, ChatColor.DARK_GREEN + "Leave");
		sign.setLine(2, "");
		sign.setLine(3, ChatColor.BLUE + "############");
		sign.update();
	}
	
	public void delete() {
		plugin.getLeaveSigns().remove(this);
	}
	
	public static boolean playerInteract(Block block, Player player) {
		
		LeaveSign leaveSign = getSign(block);
		
		if (leaveSign == null) {
			return false;
		}
		
		DPlayer dplayer = DPlayer.getByPlayer(player);
		
		if (dplayer != null) {
			dplayer.leave();
			return true;
			
		} else {
			DGroup dgroup = DGroup.getByPlayer(player);
			if (dgroup != null) {
				dgroup.removePlayer(player);
				MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.PLAYER_LEAVE_GROUP));// ChatColor.YELLOW+"Du hast deine Gruppe erfolgreich verlassen!");
				return true;
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isRelativeSign(Block block, int x, int z) {
		LeaveSign leaveSign = getSign(block.getRelative(x, 0, z));
		if (leaveSign == null) {
			return false;
		}
		
		if (x == -1 && leaveSign.sign.getData().getData() == 4) {
			return true;
		}
		
		if (x == 1 && leaveSign.sign.getData().getData() == 5) {
			return true;
		}
		
		if (z == -1 && leaveSign.sign.getData().getData() == 2) {
			return true;
		}
		
		if (z == 1 && leaveSign.sign.getData().getData() == 3) {
			return true;
		}
		
		return false;
	}
	
	public static LeaveSign getSign(Block block) {
		if ( !(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)) {
			return null;
		}
		
		for (LeaveSign leavesign : plugin.getLeaveSigns()) {
			if (block.getWorld() != leavesign.sign.getWorld()) {
				continue;
			}
			
			if (block.getLocation().distance(leavesign.sign.getBlock().getLocation()) < 1) {
				return leavesign;
			}
		}
		
		return null;
	}
	
	// Save and Load
	public static void save(FileConfiguration configFile) {
		int id = 0;
		for (LeaveSign leaveSign : plugin.getLeaveSigns()) {
			id++;
			String preString = "leavesign." + leaveSign.sign.getWorld().getName() + "." + id;
			configFile.set(preString + ".x", leaveSign.sign.getX());
			configFile.set(preString + ".y", leaveSign.sign.getY());
			configFile.set(preString + ".z", leaveSign.sign.getZ());
		}
	}
	
	public static void load(FileConfiguration configFile) {
		for (World world : plugin.getServer().getWorlds()) {
			if ( !configFile.contains("leavesign." + world.getName())) {
				continue;
			}
			
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
