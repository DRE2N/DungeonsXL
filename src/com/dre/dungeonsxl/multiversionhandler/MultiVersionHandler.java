package com.dre.dungeonsxl.multiversionhandler;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.P;

public class MultiVersionHandler {
	
	private static String version = P.p.getServer().getVersion();
	
	public static String supported = "v1_8_R1,v1_8_R2,v1_8_R3";
	
	public static String getInternals() {
		String internals = "v1_8_R3";
		if (version.contains("1.8.4") || version.contains("1.8.5") || version.contains("1.8.6") || version.contains("1.8.7") || version.contains("1.8.8")) {
			internals = "v1_8_R3";
		} else if (version.contains("1.8.3")) {
			internals = "v1_8_R2";
		} else if (version.contains("1.8") && !version.contains("1.8.")) {
			internals = "v1_8_R1";
		} else if (version.contains("1.7.10")) {
			internals = "v1_7_R4";
		} else if (version.contains("1.7.8") || version.contains("1.7.9")) {
			internals = "v1_7_R3";
		}
		return internals;
	}
	
	public static Player getOfflinePlayer(String player, UUID uuid) {
		Player pplayer = null;
		if (getInternals().equals("v1_8_R3")) {
			pplayer = v1_8_R3.getOfflinePlayer(player, uuid);
		} else if (getInternals().equals("v1_8_R2")) {
			pplayer = v1_8_R2.getOfflinePlayer(player, uuid);
		} else if (getInternals().equals("v1_8_R1")) {
			pplayer = v1_8_R1.getOfflinePlayer(player, uuid);
		} else if (getInternals().equals("v1_8_R2")) {
			pplayer = v1_8_R1.getOfflinePlayer(player, uuid);
		} else if (getInternals().equals("v1_7_R4")) {
			pplayer = v1_7_R4.getOfflinePlayer(player, uuid);
		} else if (getInternals().equals("v1_7_R3")) {
			pplayer = v1_7_R3.getOfflinePlayer(player, uuid);
		}
		return pplayer;
	}
	
	public static Player getOfflinePlayer(String player, UUID uuid, Location location) {
		Player pplayer = null;
		if (getInternals().equals("v1_8_R3")) {
			pplayer = v1_8_R3.getOfflinePlayer(player, uuid, location);
		} else if (getInternals().equals("v1_8_R2")) {
			pplayer = v1_8_R2.getOfflinePlayer(player, uuid, location);
		} else if (getInternals().equals("v1_8_R1")) {
			pplayer = v1_8_R1.getOfflinePlayer(player, uuid, location);
		} else if (getInternals().equals("v1_8_R2")) {
			pplayer = v1_8_R1.getOfflinePlayer(player, uuid, location);
		} else if (getInternals().equals("v1_7_R4")) {
			pplayer = v1_7_R4.getOfflinePlayer(player, uuid, location);
		} else if (getInternals().equals("v1_7_R3")) {
			pplayer = v1_7_R3.getOfflinePlayer(player, uuid, location);
		}
		return pplayer;
	}
	
}
