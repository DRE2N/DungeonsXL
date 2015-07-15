package com.dre.dungeonsxl.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerUtil {
	
	@SuppressWarnings("deprecation")
	public static UUID getUniqueIdFromName(String name) {
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(name);
		UUID uuid = player.getUniqueId();
		return uuid;
	}
	
}
