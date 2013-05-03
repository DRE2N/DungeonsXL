package com.dre.dungeonsxl.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.dre.dungeonsxl.game.GameWorld;

public class WorldListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onChunkUnload(ChunkUnloadEvent event) {
		GameWorld gWorld = GameWorld.get(event.getWorld());
		if (gWorld != null) {
			if (gWorld.loadedChunks.contains(event.getChunk())) {
				event.setCancelled(true);
			}
		}
	}
}
