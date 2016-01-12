package io.github.dre2n.dungeonsxl.event.dplayer;

import io.github.dre2n.dungeonsxl.player.DPlayer;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DPlayerUpdateEvent extends DPlayerEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private boolean locationValid;
	private boolean teleportWolf;
	private boolean respawnInventory;
	private boolean offline;
	private boolean kick;
	private boolean triggerAllInDistance;
	
	public DPlayerUpdateEvent(DPlayer dPlayer, boolean locationValid, boolean teleportWolf, boolean respawnInventory, boolean offline, boolean kick, boolean triggerAllInDistance) {
		super(dPlayer);
		this.locationValid = locationValid;
		this.teleportWolf = teleportWolf;
		this.respawnInventory = respawnInventory;
		this.offline = offline;
		this.kick = kick;
		this.triggerAllInDistance = triggerAllInDistance;
	}
	
	/**
	 * @return if the location is inside the correct game world
	 */
	public boolean isLocationValid() {
		return locationValid;
	}
	
	/**
	 * @param locationValid
	 * set if the location is valid
	 */
	public void setLocationValid(boolean locationValid) {
		this.locationValid = locationValid;
	}
	
	/**
	 * @return if the player's wolf gets teleported
	 */
	public boolean isTeleportWolf() {
		return teleportWolf;
	}
	
	/**
	 * @param teleportWolf
	 * set if the wolf gets teleported
	 */
	public void setTeleportWolf(boolean teleportWolf) {
		this.teleportWolf = teleportWolf;
	}
	
	/**
	 * @return if the player's inventory gets respawned
	 */
	public boolean isRespawnInventory() {
		return respawnInventory;
	}
	
	/**
	 * @param respawnInventory
	 * respawn the player's old inventory on this update?
	 */
	public void setRespawnInventory(boolean respawnInventory) {
		this.respawnInventory = respawnInventory;
	}
	
	/**
	 * @return if the player is offline
	 */
	public boolean isOffline() {
		return offline;
	}
	
	/**
	 * @return if the player gets kicked from the dungeon
	 */
	public boolean getKick() {
		return kick;
	}
	
	/**
	 * @param kick
	 * if the player gets kicked from the dungeon
	 */
	public void setKick(boolean kick) {
		this.kick = kick;
	}
	
	/**
	 * @return the triggerAllInDistance
	 */
	public boolean getTriggerAllInDistance() {
		return triggerAllInDistance;
	}
	
	/**
	 * @param triggerAllInDistance
	 * the triggerAllInDistance to set
	 */
	public void setTriggerAllInDistance(boolean triggerAllInDistance) {
		this.triggerAllInDistance = triggerAllInDistance;
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
