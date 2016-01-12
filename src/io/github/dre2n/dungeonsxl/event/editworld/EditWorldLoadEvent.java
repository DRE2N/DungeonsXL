package io.github.dre2n.dungeonsxl.event.editworld;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EditWorldLoadEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private String name;
	
	public EditWorldLoadEvent(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name
	 * the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
