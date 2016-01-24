package io.github.dre2n.dungeonsxl.task;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.sign.RedstoneSign;

import org.bukkit.scheduler.BukkitRunnable;

public class DelayedPowerTask extends BukkitRunnable {
	
	private RedstoneSign sign;
	private boolean enable;
	
	public DelayedPowerTask(RedstoneSign sign, boolean enable) {
		this.sign = sign;
		this.enable = enable;
	}
	
	@Override
	public void run() {
		if (GameWorld.getByWorld(sign.getBlock().getWorld()) == null) {
			sign.getEnableTask().cancel();
			sign.getDisableTask().cancel();
			return;
		}
		if (enable) {
			sign.power();
			if (sign.getRepeatsToDo() == 1) {
				sign.getEnableTask().cancel();
			}
		} else {
			sign.unpower();
			if (sign.getRepeatsToDo() == 1) {
				sign.getDisableTask().cancel();
			}
			sign.setRepeatsToDo(sign.getRepeatsToDo() - 1);
		}
	}
}
