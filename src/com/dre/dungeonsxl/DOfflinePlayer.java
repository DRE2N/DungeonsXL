package com.dre.dungeonsxl;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DOfflinePlayer {
	public static CopyOnWriteArrayList<DOfflinePlayer> players=new CopyOnWriteArrayList<DOfflinePlayer>();
	
	//Variables
	public String name;
	public Location oldLocation;
	public ItemStack[] oldInventory;
	public ItemStack[] oldArmor;
	public int oldLvl;
	public int oldExp;
	public int oldHealth;
	public int oldFoodLevel;
	public GameMode oldGamemode;
	
	public DOfflinePlayer(){
		players.add(this);
	}
	
	//Static
	public static void check(Player player){
		for(DOfflinePlayer offplayer:players){
			if(offplayer.name.equalsIgnoreCase(player.getName())){
				players.remove(offplayer);
				
				player.teleport(offplayer.oldLocation);
				player.getInventory().setContents(offplayer.oldInventory);
				player.getInventory().setArmorContents(offplayer.oldArmor);
				player.setTotalExperience(offplayer.oldExp);
				player.setLevel(offplayer.oldLvl);
				player.setHealth(offplayer.oldHealth);
				player.setFoodLevel(offplayer.oldFoodLevel);
				player.setGameMode(offplayer.oldGamemode);
			}
		}
	}
}
