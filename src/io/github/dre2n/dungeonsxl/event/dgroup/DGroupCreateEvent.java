package io.github.dre2n.dungeonsxl.event.dgroup;

import io.github.dre2n.dungeonsxl.player.DGroup;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DGroupCreateEvent extends DGroupEvent implements Cancellable {
	
	public enum Cause {
		
		COMMAND,
		GROUP_SIGN,
		CUSTOM;
		
	}
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private Player creator;
	
	private Cause cause;
	
	public DGroupCreateEvent(DGroup dGroup, Player creator, Cause cause) {
		super(dGroup);
		this.creator = creator;
		this.cause = cause;
	}
	
	/**
	 * @return the creator
	 */
	public Player getCreator() {
		return creator;
	}
	
	/**
	 * @param creator
	 * the creator to set
	 */
	public void setCreator(Player creator) {
		this.creator = creator;
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
