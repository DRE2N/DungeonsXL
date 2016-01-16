package io.github.dre2n.dungeonsxl.task;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DPlayer;

import org.bukkit.scheduler.BukkitRunnable;

public class LazyUpdateTask extends BukkitRunnable {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	@Override
	public void run() {
		for (GameWorld gameWorld : plugin.getGameWorlds()) {
			gameWorld.update();
		}
		
		for (DPlayer dPlayer : plugin.getDPlayers()) {
			dPlayer.update(true);
		}
	}
	
}
