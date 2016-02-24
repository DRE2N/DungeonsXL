package io.github.dre2n.dungeonsxl.task;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DPlayer;

import org.bukkit.scheduler.BukkitRunnable;

public class WorldUnloadTask extends BukkitRunnable {
	
	protected static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	@Override
	public void run() {
		for (GameWorld gameWorld : plugin.getGameWorlds()) {
			if (gameWorld.getWorld().getPlayers().isEmpty()) {
				if (DPlayer.getByWorld(gameWorld.getWorld()).isEmpty()) {
					gameWorld.delete();
				}
			}
		}
		
		for (EditWorld editWorld : plugin.getEditWorlds()) {
			if (editWorld.getWorld().getPlayers().isEmpty()) {
				editWorld.delete();
			}
		}
	}
	
}
