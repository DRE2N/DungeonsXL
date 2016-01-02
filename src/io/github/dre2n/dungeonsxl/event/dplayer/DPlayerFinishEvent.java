package io.github.dre2n.dungeonsxl.event.dplayer;

import io.github.dre2n.dungeonsxl.player.DPlayer;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DPlayerFinishEvent extends DPlayerEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private boolean first;
	private boolean hasToWait;
	
	public DPlayerFinishEvent(DPlayer dPlayer, boolean first, boolean hasToWait) {
		super(dPlayer);
		this.first = first;
		this.hasToWait = hasToWait;
	}
	
	/**
	 * @return the first
	 */
	public boolean isFirst() {
		return first;
	}
	
	/**
	 * @param first
	 * the first to set
	 */
	public void setFirst(boolean first) {
		this.first = first;
	}
	
	/**
	 * @return the hasToWait
	 */
	public boolean getHasToWait() {
		return hasToWait;
	}
	
	/**
	 * @param hasToWait
	 * the hasToWait to set
	 */
	public void setHasToWait(boolean hasToWait) {
		this.hasToWait = hasToWait;
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
