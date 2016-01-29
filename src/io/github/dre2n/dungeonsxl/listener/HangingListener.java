package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.dungeonsxl.game.GameWorld;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class HangingListener implements Listener {
	
	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		GameWorld gameWorld = GameWorld.getByWorld(event.getEntity().getWorld());
		if (gameWorld != null) {
			event.setCancelled(true);
		}
	}
	
}
