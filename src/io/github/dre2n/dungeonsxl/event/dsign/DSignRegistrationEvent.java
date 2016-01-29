package io.github.dre2n.dungeonsxl.event.dsign;

import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.sign.DSign;

import org.bukkit.block.Sign;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DSignRegistrationEvent extends DSignEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private Sign sign;
	private GameWorld gameWorld;
	
	public DSignRegistrationEvent(Sign sign, GameWorld gameWorld, DSign dSign) {
		super(dSign);
		this.sign = sign;
		this.gameWorld = gameWorld;
	}
	
	/**
	 * @return the sign
	 */
	public Sign getSign() {
		return sign;
	}
	
	/**
	 * @param sign
	 * the sign to set
	 */
	public void setSign(Sign sign) {
		this.sign = sign;
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
