package com.dre.dungeonsxl.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

public class OfflinePlayerUtil {
	
	static Server server = Bukkit.getServer();
	
	@SuppressWarnings("deprecation")
	public static UUID getUniqueIdFromName(String name) {
		OfflinePlayer player = server.getOfflinePlayer(name);
		UUID uuid = player.getUniqueId();
		return uuid;
	}
	
}
