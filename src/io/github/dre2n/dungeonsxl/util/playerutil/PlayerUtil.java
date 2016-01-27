package io.github.dre2n.dungeonsxl.util.playerutil;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.util.VersionUtil;
import io.github.dre2n.dungeonsxl.util.VersionUtil.Internals;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerUtil {
	
	public static Player getOfflinePlayer(String name) {
		@SuppressWarnings("deprecation")
		OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(name);
		return getOfflinePlayer(name, offlinePlayer.getUniqueId());
	}
	
	public static Player getOfflinePlayer(UUID uuid) {
		OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(uuid);
		return getOfflinePlayer(offlinePlayer.getName(), uuid);
	}
	
	public static Player getOfflinePlayer(String name, UUID uuid) {
		VersionUtil versions = DungeonsXL.getPlugin().getVersion();
		
		if (versions.getInternals() == Internals.v1_9_R1) {
			return v1_9_R1.getOfflinePlayer(name, uuid);
			
		} else if (versions.getInternals() == Internals.v1_8_R3) {
			return v1_8_R3.getOfflinePlayer(name, uuid);
			
		} else if (versions.getInternals() == Internals.v1_8_R2) {
			return v1_8_R2.getOfflinePlayer(name, uuid);
			
		} else if (versions.getInternals() == Internals.v1_8_R1) {
			return v1_8_R1.getOfflinePlayer(name, uuid);
			
		} else if (versions.getInternals() == Internals.v1_7_R4) {
			return v1_7_R4.getOfflinePlayer(name, uuid);
			
		} else if (versions.getInternals() == Internals.v1_7_R3) {
			return v1_7_R3.getOfflinePlayer(name, uuid);
			
		} else {
			return null;
		}
	}
	
	public static Player getOfflinePlayer(String name, UUID uuid, Location location) {
		VersionUtil versions = DungeonsXL.getPlugin().getVersion();
		
		if (versions.getInternals() == Internals.v1_9_R1) {
			return v1_9_R1.getOfflinePlayer(name, uuid, location);
			
		} else if (versions.getInternals() == Internals.v1_8_R3) {
			return v1_8_R3.getOfflinePlayer(name, uuid, location);
			
		} else if (versions.getInternals() == Internals.v1_8_R2) {
			return v1_8_R2.getOfflinePlayer(name, uuid, location);
			
		} else if (versions.getInternals() == Internals.v1_8_R1) {
			return v1_8_R1.getOfflinePlayer(name, uuid, location);
			
		} else if (versions.getInternals() == Internals.v1_7_R4) {
			return v1_7_R4.getOfflinePlayer(name, uuid, location);
			
		} else if (versions.getInternals() == Internals.v1_7_R3) {
			return v1_7_R3.getOfflinePlayer(name, uuid, location);
			
		} else {
			return null;
		}
	}
	
	public static void secureTeleport(Player player, Location location) {
		if (player.isInsideVehicle()) {
			player.leaveVehicle();
		}
		
		player.teleport(location);
	}
	
}
