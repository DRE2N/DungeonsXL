package io.github.dre2n.dungeonsxl.event.requirement;

import io.github.dre2n.dungeonsxl.requirement.Requirement;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class RequirementRegistrationEvent extends RequirementEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	public RequirementRegistrationEvent(Requirement requirement) {
		super(requirement);
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
