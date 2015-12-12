package io.github.dre2n.dungeonsxl.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class UUIDUtil {
	
	public static UUID getUniqueIdFromName(String name) {
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(name);
		return player.getUniqueId();
	}
	
	public static String getNameFromUniqueId(String uuid) {
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(UUID.fromString(uuid));
		return player.getName();
	}
	
	public static boolean isValidUUID(String string) {
		if (string.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
			return true;
		} else {
			return false;
		}
	}
	
}
