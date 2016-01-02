package io.github.dre2n.dungeonsxl.event.dgroup;

import io.github.dre2n.dungeonsxl.player.DGroup;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DGroupCreateEvent extends DGroupEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private Player creator;
	
	public DGroupCreateEvent(DGroup dGroup, Player creator) {
		super(dGroup);
		this.creator = creator;
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
