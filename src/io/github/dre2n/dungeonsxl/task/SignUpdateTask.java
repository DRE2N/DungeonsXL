package io.github.dre2n.dungeonsxl.task;

import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

public class SignUpdateTask extends BukkitRunnable {
	
	private Sign sign;
	
	public SignUpdateTask(Sign sign) {
		this.sign = sign;
	}
	
	@Override
	public void run() {
		sign.update();
	}
	
}
