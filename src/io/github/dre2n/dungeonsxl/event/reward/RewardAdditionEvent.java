package io.github.dre2n.dungeonsxl.event.reward;

import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.reward.Reward;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class RewardAdditionEvent extends RewardEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private DGroup dGroup;
	
	public RewardAdditionEvent(Reward reward, DGroup dGroup) {
		super(reward);
		this.dGroup = dGroup;
	}
	
	/**
	 * @return the dGroup
	 */
	public DGroup getDGroup() {
		return dGroup;
	}
	
	/**
	 * @param dGroup
	 * the dGroup to set
	 */
	public void setDGroup(DGroup dGroup) {
		this.dGroup = dGroup;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}
