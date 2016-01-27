package io.github.dre2n.dungeonsxl.event.dplayer;

import io.github.dre2n.dungeonsxl.player.DPlayer;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DPlayerKickEvent extends DPlayerEvent implements Cancellable {
	
	public enum Cause {
		
		COMMAND,
		DEATH,
		OFFLINE,
		CUSTOM
		
	}
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private Cause cause;
	
	public DPlayerKickEvent(DPlayer dPlayer, Cause cause) {
		super(dPlayer);
		this.cause = cause;
	}
	
	/**
	 * @return the cause
	 */
	public Cause getCause() {
		return cause;
	}
	
	/**
	 * @param cause
	 * the cause to set
	 */
	public void setCause(Cause cause) {
		this.cause = cause;
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
