package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class WorldListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onChunkUnload(ChunkUnloadEvent event) {
		GameWorld gameWorld = GameWorld.getByWorld(event.getWorld());
		if (gameWorld != null) {
			if (gameWorld.getLoadedChunks().contains(event.getChunk())) {
				event.setCancelled(true);
			}
		}
	}
	
}
