package io.github.dre2n.dungeonsxl.task;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.trigger.RedstoneTrigger;

import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class RedstoneEventTask extends BukkitRunnable {
	
	private final Block block;
	
	public RedstoneEventTask(final Block block) {
		this.block = block;
	}
	
	@Override
	public void run() {
		for (GameWorld gameWorld : DungeonsXL.getPlugin().getGameWorlds()) {
			if (block.getWorld() == gameWorld.getWorld()) {
				RedstoneTrigger.updateAll(gameWorld);
			}
		}
	}
	
}
