package io.github.dre2n.dungeonsxl.event.dmob;

import io.github.dre2n.dungeonsxl.mob.DMob;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;

public class DMobDeathEvent extends DMobEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private EntityDeathEvent bukkitEvent;
	
	public DMobDeathEvent(DMob dMob, EntityDeathEvent bukkitEvent) {
		super(dMob);
		this.bukkitEvent = bukkitEvent;
	}
	
	/**
	 * @return the bukkitEvent
	 */
	public EntityDeathEvent getBukkitEvent() {
		return bukkitEvent;
	}
	
	/**
	 * @param bukkitEvent
	 * the bukkitEvent to set
	 */
	public void setBukkitEvent(EntityDeathEvent bukkitEvent) {
		this.bukkitEvent = bukkitEvent;
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
