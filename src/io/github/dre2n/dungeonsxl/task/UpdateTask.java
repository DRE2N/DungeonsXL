package io.github.dre2n.dungeonsxl.task;

import org.bukkit.scheduler.BukkitRunnable;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.player.DPlayer;

public class UpdateTask extends BukkitRunnable {
	
	@Override
	public void run() {
		for (DPlayer dPlayer : DungeonsXL.getPlugin().getDPlayers()) {
			dPlayer.update(false);
		}
	}
	
}
