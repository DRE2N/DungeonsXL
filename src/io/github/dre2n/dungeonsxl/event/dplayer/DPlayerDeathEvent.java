package io.github.dre2n.dungeonsxl.event.dplayer;

import io.github.dre2n.dungeonsxl.player.DPlayer;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DPlayerDeathEvent extends DPlayerEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private PlayerDeathEvent bukkitEvent;
	private int lostLives;
	
	public DPlayerDeathEvent(DPlayer dPlayer, PlayerDeathEvent bukkitEvent, int lostLives) {
		super(dPlayer);
		this.bukkitEvent = bukkitEvent;
		this.lostLives = lostLives;
	}
	
	/**
	 * @return the bukkitEvent
	 */
	public PlayerDeathEvent getBukkitEvent() {
		return bukkitEvent;
	}
	
	/**
	 * @param bukkitEvent
	 * the bukkitEvent to set
	 */
	public void setBukkitEvent(PlayerDeathEvent bukkitEvent) {
		this.bukkitEvent = bukkitEvent;
	}
	
	/**
	 * @return the lostLives
	 */
	public int getLostLives() {
		return lostLives;
	}
	
	/**
	 * @param lostLives
	 * the lostLives to set
	 */
	public void setLostLives(int lostLives) {
		this.lostLives = lostLives;
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
