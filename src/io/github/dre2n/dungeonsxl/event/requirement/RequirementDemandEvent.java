package io.github.dre2n.dungeonsxl.event.requirement;

import io.github.dre2n.dungeonsxl.requirement.Requirement;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class RequirementDemandEvent extends RequirementEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private Player player;
	
	public RequirementDemandEvent(Requirement requirement, Player player) {
		super(requirement);
		this.player = player;
	}
	
	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * @param player
	 * the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
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
