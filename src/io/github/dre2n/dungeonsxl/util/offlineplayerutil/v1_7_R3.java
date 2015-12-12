package io.github.dre2n.dungeonsxl.util.offlineplayerutil;

import java.io.File;
import java.util.UUID;

import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.server.v1_7_R3.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Player;

public class v1_7_R3 {
	
	public static Player getOfflinePlayer(String player, UUID uuid) {
		Player pplayer = null;
		try {
			File playerfolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");
			
			for (File playerfile : playerfolder.listFiles()) {
				String filename = playerfile.getName();
				String playername = filename.substring(0, filename.length() - 4);
				
				GameProfile profile = new GameProfile(uuid, playername);
				
				if (playername.trim().equalsIgnoreCase(player)) {
					MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
					EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), profile, new PlayerInteractManager(server.getWorldServer(0)));
					Player target = entity == null ? null : (Player) entity.getBukkitEntity();
					if (target != null) {
						target.loadData();
						return target;
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
		return pplayer;
	}
	
	public static Player getOfflinePlayer(String player, UUID uuid, Location location) {
		Player pplayer = null;
		try {
			File playerfolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");
			
			for (File playerfile : playerfolder.listFiles()) {
				String filename = playerfile.getName();
				String playername = filename.substring(0, filename.length() - 4);
				
				GameProfile profile = new GameProfile(uuid, playername);
				
				if (playername.trim().equalsIgnoreCase(player)) {
					MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
					EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), profile, new PlayerInteractManager(server.getWorldServer(0)));
					entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
					entity.world = ((CraftWorld) location.getWorld()).getHandle();
					Player target = entity == null ? null : (Player) entity.getBukkitEntity();
					if (target != null) {
						// target.loadData();
						return target;
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
		return pplayer;
	}
	
}
