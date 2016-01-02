package io.github.dre2n.dungeonsxl.event.dgroup;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DGroupJoinGameWorldEvent extends DGroupEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private GameWorld gameWorld;
	
	public DGroupJoinGameWorldEvent(DGroup dGroup, GameWorld gameWorld) {
		super(dGroup);
		this.gameWorld = gameWorld;
	}
	
	/**
	 * @return the gameWorld
	 */
	public GameWorld getGameWorld() {
		return gameWorld;
	}
	
	/**
	 * @param gameWorld
	 * the gameWorld to set
	 */
	public void setGameWorld(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
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
