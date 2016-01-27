package io.github.dre2n.dungeonsxl.event.dgroup;

import io.github.dre2n.dungeonsxl.player.DGroup;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DGroupDisbandEvent extends DGroupEvent implements Cancellable {
	
	public enum Cause {
		
		COMMAND,
		DUNGEON_FINISHED,
		CUSTOM
		
	}
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private Player disbander;
	
	private Cause cause;
	
	public DGroupDisbandEvent(DGroup dGroup, Cause cause) {
		super(dGroup);
		this.cause = cause;
	}
	
	public DGroupDisbandEvent(DGroup dGroup, Player disbander, Cause cause) {
		super(dGroup);
		this.disbander = disbander;
		this.cause = cause;
	}
	
	/**
	 * @return the disbander
	 */
	public Player getDisbander() {
		return disbander;
	}
	
	/**
	 * @param disbander
	 * the disbander to set
	 */
	public void setDisbander(Player disbander) {
		this.disbander = disbander;
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
