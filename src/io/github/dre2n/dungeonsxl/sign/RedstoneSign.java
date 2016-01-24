package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.task.DelayedPowerTask;
import io.github.dre2n.dungeonsxl.util.NumberUtil;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitTask;

public class RedstoneSign extends DSign {
	
	private DSignType type = DSignTypeDefault.REDSTONE;
	
	// Variables
	private boolean initialized;
	private boolean active;
	private BukkitTask enableTask;
	private BukkitTask disableTask;
	private Block block;
	private long delay = 0;
	private long offDelay = 0;
	private int repeat = 1;
	private int repeatsToDo = 1;
	
	public RedstoneSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	/**
	 * @return the initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}
	
	/**
	 * @param initialized
	 * the initialized to set
	 */
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * @param active
	 * the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * @return the enableTask
	 */
	public BukkitTask getEnableTask() {
		return enableTask;
	}
	
	/**
	 * @param enableTask
	 * the enableTask to set
	 */
	public void setEnableTask(BukkitTask enableTask) {
		this.enableTask = enableTask;
	}
	
	/**
	 * @return the disableTask
	 */
	public BukkitTask getDisableTask() {
		return disableTask;
	}
	
	/**
	 * @param disableTask
	 * the disableTask to set
	 */
	public void setDisableTask(BukkitTask disableTask) {
		this.disableTask = disableTask;
	}
	
	/**
	 * @return the block
	 */
	public Block getBlock() {
		return block;
	}
	
	/**
	 * @param block
	 * the block to set
	 */
	public void setBlock(Block block) {
		this.block = block;
	}
	
	/**
	 * @return the delay
	 */
	public long getDelay() {
		return delay;
	}
	
	/**
	 * @param delay
	 * the delay to set
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	/**
	 * @return the offDelay
	 */
	public long getOffDelay() {
		return offDelay;
	}
	
	/**
	 * @param offDelay
	 * the offDelay to set
	 */
	public void setOffDelay(long offDelay) {
		this.offDelay = offDelay;
	}
	
	/**
	 * @return the repeat
	 */
	public int getRepeat() {
		return repeat;
	}
	
	/**
	 * @param repeat
	 * the repeat to set
	 */
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	
	/**
	 * @return the repeatsToDo
	 */
	public int getRepeatsToDo() {
		return repeatsToDo;
	}
	
	/**
	 * @param repeatsToDo
	 * the repeatsToDo to set
	 */
	public void setRepeatsToDo(int repeatsToDo) {
		this.repeatsToDo = repeatsToDo;
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
			line1 = NumberUtil.parseInt(line[0]);
			if (line.length > 1) {
				line11 = NumberUtil.parseInt(line[1]);
			}
		}
		
		int line2 = 1;
		if ( !getSign().getLine(2).equals("")) {
			line2 = NumberUtil.parseInt(getSign().getLine(2));
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
		if ( !initialized || active) {
			return;
		}
		
		if (delay > 0) {
			enableTask = new DelayedPowerTask(this, true).runTaskTimer(plugin, delay, delay + offDelay);
			
			if (repeat != 1) {
				repeatsToDo = repeat;
				disableTask = new DelayedPowerTask(this, false).runTaskTimer(plugin, delay + offDelay, delay + offDelay);
			}
			
		} else {
			power();
		}
		
		active = true;
	}
	
	@Override
	public void onDisable() {
		if ( !initialized || !active) {
			return;
		}
		
		unpower();
		
		enableTask.cancel();
		disableTask.cancel();
		
		active = false;
	}
	
	public void power() {
		block.setType(Material.REDSTONE_BLOCK);
	}
	
	public void unpower() {
		block.setType(Material.AIR);
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
