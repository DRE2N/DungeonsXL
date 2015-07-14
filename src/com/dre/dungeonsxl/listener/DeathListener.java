package com.dre.dungeonsxl.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.dre.dungeonsxl.P;

public class DeathListener implements Listener {

	P p = P.p;
	int lives = -1;

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (P.lives.containsKey(player)) {
			lives = P.lives.get(player) - 1;
			P.lives.put(player, lives);
		}
		if (lives == 0) {
			Bukkit.broadcastMessage(p.language.get("Player_DeathKick").replaceAll("v1", player.getName()).replaceAll("&", "\u00a76"));
			player.performCommand("dxl leave");
		} else if (!(lives == -1)) {
			p.msg(player, p.language.get("Player_Death").replaceAll("v1", String.valueOf(lives)));
		}
	}

}
