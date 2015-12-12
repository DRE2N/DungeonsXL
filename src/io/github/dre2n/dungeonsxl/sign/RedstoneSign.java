package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class RedstoneSign extends DSign {
	
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
	
	public RedstoneSign(Sign sign, GameWorld gworld) {
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
		if ( !getSign().getLine(1).equals("")) {
			String line[] = getSign().getLine(1).split(",");
			line1 = IntegerUtil.parseInt(line[0]);
			if (line.length > 1) {
				line11 = IntegerUtil.parseInt(line[1]);
			}
		}
		
		int line2 = 1;
		if ( !getSign().getLine(2).equals("")) {
			line2 = IntegerUtil.parseInt(getSign().getLine(2));
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
		
		block = getSign().getBlock();
		block.setType(Material.AIR);
		
		initialized = true;
	}
	
	@Override
	public void onTrigger() {
		if (initialized && !active) {
			if (delay > 0) {
				enableTaskId = DungeonsXL.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(DungeonsXL.getPlugin(), new DelayedPower(true), delay, delay + offDelay);
				if (repeat != 1) {
					repeatsToDo = repeat;
					disableTaskId = DungeonsXL.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(DungeonsXL.getPlugin(), new DelayedPower(false), delay + offDelay, delay + offDelay);
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
		block.setType(Material.REDSTONE_BLOCK);
	}
	
	public void unpower() {
		block.setType(Material.AIR);
	}
	
	public void disableTask(int taskId) {
		if (taskId != -1) {
			if (DungeonsXL.getPlugin().getServer().getScheduler().isCurrentlyRunning(taskId) || DungeonsXL.getPlugin().getServer().getScheduler().isQueued(taskId)) {
				DungeonsXL.getPlugin().getServer().getScheduler().cancelTask(taskId);
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
