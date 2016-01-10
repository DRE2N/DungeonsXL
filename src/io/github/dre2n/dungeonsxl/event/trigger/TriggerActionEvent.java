package io.github.dre2n.dungeonsxl.event.trigger;

import io.github.dre2n.dungeonsxl.trigger.Trigger;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class TriggerActionEvent extends TriggerEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	public TriggerActionEvent(Trigger trigger) {
		super(trigger);
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
