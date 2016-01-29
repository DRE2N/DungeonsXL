package io.github.dre2n.dungeonsxl.event.gameworld;

import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.game.GameWorld;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GameWorldStartGameEvent extends GameWorldEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private Game game;
	
	public GameWorldStartGameEvent(GameWorld gameWorld, Game game) {
		super(gameWorld);
		this.game = game;
	}
	
	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}
	
	/**
	 * @param game
	 * the game to set
	 */
	public void setGame(Game game) {
		this.game = game;
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
