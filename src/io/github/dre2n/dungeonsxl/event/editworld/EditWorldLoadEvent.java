package io.github.dre2n.dungeonsxl.event.editworld;

import io.github.dre2n.dungeonsxl.dungeon.EditWorld;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class EditWorldLoadEvent extends EditWorldEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	public EditWorldLoadEvent(EditWorld editWorld) {
		super(editWorld);
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
