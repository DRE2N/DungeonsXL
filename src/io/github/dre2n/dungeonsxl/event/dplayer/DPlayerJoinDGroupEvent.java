package io.github.dre2n.dungeonsxl.event.dplayer;

import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DPlayerJoinDGroupEvent extends DPlayerEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private boolean creator;
	private DGroup dGroup;
	
	public DPlayerJoinDGroupEvent(DPlayer dPlayer, boolean creator, DGroup dGroup) {
		super(dPlayer);
		this.creator = creator;
		this.dGroup = dGroup;
	}
	
	/**
	 * @return if the player is the creator of the group
	 */
	public boolean isCreator() {
		return creator;
	}
	
	/**
	 * @return the dGroup
	 */
	public DGroup getDGroup() {
		return dGroup;
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
