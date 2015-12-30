package io.github.dre2n.dungeonsxl.util.messageutil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class UNKNOWN {
	
	static void sendScreenMessage(Player player, String title, String subtitle, long fadeIn, long show, long fadeOut) {
		subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
		title = ChatColor.translateAlternateColorCodes('&', title);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " time " + fadeIn + " " + show + " " + fadeOut);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " subtitle " + subtitle);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " title " + title);
	}
	
	static void sendScreenMessage(Player player, String title, String subtitle) {
		subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
		title = ChatColor.translateAlternateColorCodes('&', title);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " subtitle " + subtitle);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " title " + title);
	}
	
	public static void sendScreenMessage(Player player, String title) {
		title = ChatColor.translateAlternateColorCodes('&', title);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " title \"" + title + "\"");
	}
	
}
