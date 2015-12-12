package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class HangingListener implements Listener {
	
	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		GameWorld gworld = GameWorld.get(event.getEntity().getWorld());
		if (gworld != null) {
			event.setCancelled(true);
		}
	}
	
}
