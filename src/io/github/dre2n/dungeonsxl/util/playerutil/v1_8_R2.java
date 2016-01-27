package io.github.dre2n.dungeonsxl.util.playerutil;

import java.io.File;
import java.util.UUID;

import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.MinecraftServer;
import net.minecraft.server.v1_8_R2.PlayerInteractManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.CraftServer;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

class v1_8_R2 {
	
	static Player getOfflinePlayer(String name, UUID uuid) {
		Player pplayer = null;
		try {
			File playerFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");
			
			for (File playerFile : playerFolder.listFiles()) {
				String fileName = playerFile.getName();
				String playerName = fileName.substring(0, fileName.length() - 4);
				
				GameProfile profile = new GameProfile(uuid, playerName);
				
				if (playerName.trim().equalsIgnoreCase(name)) {
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
	
	static Player getOfflinePlayer(String name, UUID uuid, Location location) {
		Player pplayer = null;
		try {
			File playerFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");
			
			for (File playerFile : playerFolder.listFiles()) {
				String fileName = playerFile.getName();
				String playerName = fileName.substring(0, fileName.length() - 4);
				
				GameProfile profile = new GameProfile(uuid, playerName);
				
				if (playerName.trim().equalsIgnoreCase(name)) {
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
