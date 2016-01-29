package io.github.dre2n.dungeonsxl.event.gameworld;

import io.github.dre2n.dungeonsxl.game.GameWorld;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GameWorldUnloadEvent extends GameWorldEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	
	private boolean cancelled;
	
	public GameWorldUnloadEvent(GameWorld gameWorld) {
		super(gameWorld);
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
