package com.dre.dungeonsxl.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import com.dre.dungeonsxl.game.GameWorld;

public class HangingListener implements Listener{
	
	@EventHandler
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event){
		GameWorld gworld = GameWorld.get(event.getEntity().getWorld());
		if(gworld!=null){
			event.setCancelled(true);
		}
	}
}
