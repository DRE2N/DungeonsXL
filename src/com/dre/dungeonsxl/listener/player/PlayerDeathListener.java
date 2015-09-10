package com.dre.dungeonsxl.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.dre.dungeonsxl.DConfig;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.game.GameWorld;

public class PlayerDeathListener implements Listener {

	P p = P.p;
	int lives = -1;

	@EventHandler(priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		DPlayer dPlayer = DPlayer.get(player);

		DConfig dConfig = GameWorld.get(player.getLocation().getWorld()).config;

		if (dPlayer != null) {
			if (dConfig.getKeepInventoryOnDeath()) {
				dPlayer.respawnInventory = event.getEntity().getInventory().getContents();
				dPlayer.respawnArmor = event.getEntity().getInventory().getArmorContents();
				// Delete all drops
				for (ItemStack istack : event.getDrops()) {
					istack.setType(Material.AIR);
				}
			}
	
			if (p.lives.containsKey(player)) {
				lives = p.lives.get(player) - 1;
				p.lives.put(player, lives);
			}
	
			if (lives == 0 && dPlayer.isReady) {
				Bukkit.broadcastMessage(p.language.get("Player_DeathKick").replaceAll("v1", player.getName()).replaceAll("&", "\u00a7"));
				player.performCommand("dxl leave");
			} else if (!(lives == -1)) {
				p.msg(player, p.language.get("Player_Death").replaceAll("v1", String.valueOf(lives)));
			}
		}

	}

}
