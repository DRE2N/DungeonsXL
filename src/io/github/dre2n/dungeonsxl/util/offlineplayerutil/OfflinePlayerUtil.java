package io.github.dre2n.dungeonsxl.util.offlineplayerutil;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.util.VersionUtil;
import io.github.dre2n.dungeonsxl.util.VersionUtil.Internals;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class OfflinePlayerUtil {
	
	public static Player getOfflinePlayer(String player, UUID uuid) {
		VersionUtil versions = DungeonsXL.getPlugin().getVersion();
		
		if (versions.getInternals() == Internals.v1_9_R1) {
			return v1_9_R1.getOfflinePlayer(player, uuid);
			
		} else if (versions.getInternals() == Internals.v1_8_R3) {
			return v1_8_R3.getOfflinePlayer(player, uuid);
			
		} else if (versions.getInternals() == Internals.v1_8_R2) {
			return v1_8_R2.getOfflinePlayer(player, uuid);
			
		} else if (versions.getInternals() == Internals.v1_8_R1) {
			return v1_8_R1.getOfflinePlayer(player, uuid);
			
		} else if (versions.getInternals() == Internals.v1_7_R4) {
			return v1_7_R4.getOfflinePlayer(player, uuid);
			
		} else if (versions.getInternals() == Internals.v1_7_R3) {
			return v1_7_R3.getOfflinePlayer(player, uuid);
			
		} else {
			return null;
		}
	}
	
	public static Player getOfflinePlayer(String player, UUID uuid, Location location) {
		VersionUtil versions = DungeonsXL.getPlugin().getVersion();
		
		if (versions.getInternals() == Internals.v1_9_R1) {
			return v1_9_R1.getOfflinePlayer(player, uuid, location);
			
		} else if (versions.getInternals() == Internals.v1_8_R3) {
			return v1_8_R3.getOfflinePlayer(player, uuid, location);
			
		} else if (versions.getInternals() == Internals.v1_8_R2) {
			return v1_8_R2.getOfflinePlayer(player, uuid, location);
			
		} else if (versions.getInternals() == Internals.v1_8_R1) {
			return v1_8_R1.getOfflinePlayer(player, uuid, location);
			
		} else if (versions.getInternals() == Internals.v1_7_R4) {
			return v1_7_R4.getOfflinePlayer(player, uuid, location);
			
		} else if (versions.getInternals() == Internals.v1_7_R3) {
			return v1_7_R3.getOfflinePlayer(player, uuid, location);
			
		} else {
			return null;
		}
	}
	
}
