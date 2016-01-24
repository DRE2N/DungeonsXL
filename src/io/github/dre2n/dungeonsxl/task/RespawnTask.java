package io.github.dre2n.dungeonsxl.task;

import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.playerutil.PlayerUtil;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnTask extends BukkitRunnable {
	
	private Player player;
	private Location location;
	
	public RespawnTask(Player player, Location location) {
		this.location = location;
		this.player = player;
	}
	
	@Override
	public void run() {
		if (player.getLocation().distance(location) > 2) {
			PlayerUtil.secureTeleport(player, location);
		}
		
		DPlayer dPlayer = DPlayer.getByPlayer(player);
		
		if (dPlayer == null) {
			return;
		}
		
		// Respawn Items
		if (dPlayer.getRespawnInventory() != null || dPlayer.getRespawnArmor() != null) {
			player.getInventory().setContents(dPlayer.getRespawnInventory());
			player.getInventory().setArmorContents(dPlayer.getRespawnArmor());
			dPlayer.setRespawnInventory(null);
			dPlayer.setRespawnArmor(null);
		}
	}
	
}
