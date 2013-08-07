package com.dre.dungeonsxl.signs;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameWorld;
import com.dre.dungeonsxl.P;

public class SIGNRedstone extends DSign {

	public static String name = "Redstone";
	public String buildPermissions = "dxl.sign.redstone";
	public boolean onDungeonInit = false;

	// Variables
	private boolean initialized;
	private boolean active;
	private int enableTaskId = -1;
	private int disableTaskId = -1;
	private Block block;
	private long delay = 0;
	private long offDelay = 0;
	private int repeat = 1;
	private int repeatsToDo = 1;

	public SIGNRedstone(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		return true;
	}

	@Override
	public void onInit() {
		int line1 = 0;
		int line11 = 0;
		if (!sign.getLine(1).equals("")) {
			String line[] = sign.getLine(1).split(",");
			line1 = P.p.parseInt(line[0]);
			if (line.length > 1) {
				line11 = P.p.parseInt(line[1]);
			}
		}

		int line2 = 1;
		if (!sign.getLine(2).equals("")) {
			line2 = P.p.parseInt(sign.getLine(2));
		}

		if (line1 > 0) {
			delay = (long) line1 * 2;
			if (line11 > 0) {
				offDelay = (long) line11 * 2;
			} else {
				offDelay = delay;
			}
			if (line2 >= 0) {
				repeat = line2;
			}
		}

		this.block = sign.getBlock();
		this.block.setTypeId(0);

		initialized = true;
	}

	@Override
	public void onTrigger() {
		if (initialized && !active) {
			if (delay > 0) {
				enableTaskId = P.p.getServer().getScheduler().scheduleSyncRepeatingTask(P.p, new DelayedPower(true), delay, delay + offDelay);
				if (repeat != 1) {
					repeatsToDo = repeat;
					disableTaskId = P.p.getServer().getScheduler().scheduleSyncRepeatingTask(P.p, new DelayedPower(false), delay + offDelay, delay + offDelay);
				}
			} else {
				power();
			}
			active = true;
		}
	}

	@Override
	public void onDisable() {
		if (initialized && active) {
			unpower();

			disableTask(enableTaskId);
			disableTask(disableTaskId);
			enableTaskId = -1;
			disableTaskId = -1;

			active = false;
		}
	}

	public void power() {
		block.setTypeId(152);
	}

	public void unpower() {
		block.setTypeId(0);
	}

	public void disableTask(int taskId) {
		if (taskId != -1) {
			if (P.p.getServer().getScheduler().isCurrentlyRunning(taskId) || P.p.getServer().getScheduler().isQueued(taskId)) {
				P.p.getServer().getScheduler().cancelTask(taskId);
			}
		}
	}

	@Override
	public String getPermissions() {
		return buildPermissions;
	}

	@Override
	public boolean isOnDungeonInit() {
		return onDungeonInit;
	}

	public class DelayedPower implements Runnable {
		private final boolean enable;

		public DelayedPower(boolean enable) {
			this.enable = enable;
		}

		@Override
		public void run() {
			if (GameWorld.get(block.getWorld()) == null) {
				disableTask(enableTaskId);
				disableTask(disableTaskId);
				return;
			}
			if (enable) {
				power();
				if (repeatsToDo == 1) {
					disableTask(enableTaskId);
					enableTaskId = -1;
				}
			} else {
				unpower();
				if (repeatsToDo == 1) {
					disableTask(disableTaskId);
					disableTaskId = -1;
				}
				repeatsToDo--;
			}
		}
	}
}
