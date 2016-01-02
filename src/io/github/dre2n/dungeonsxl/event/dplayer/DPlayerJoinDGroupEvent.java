package io.github.dre2n.dungeonsxl.event.dplayer;

import io.github.dre2n.dungeonsxl.player.DPlayer;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DPlayerJoinDGroupEvent extends DPlayerEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private boolean creator;
	
	public DPlayerJoinDGroupEvent(DPlayer dPlayer, boolean creator) {
		super(dPlayer);
		this.creator = creator;
	}
	
	/**
	 * @return if the player is the creator of the group
	 */
	public boolean isCreator() {
		return creator;
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
