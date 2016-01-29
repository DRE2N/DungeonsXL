package io.github.dre2n.dungeonsxl.event.gameworld;

import io.github.dre2n.dungeonsxl.game.GameWorld;

import org.bukkit.event.Event;

public abstract class GameWorldEvent extends Event {
	
	protected GameWorld gameWorld;
	
	public GameWorldEvent(GameWorld gameWorld) {
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
	
}
