package io.github.dre2n.dungeonsxl.event.dgroup;

import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DGroupFinishFloorEvent extends DGroupEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	
	private GameWorld finished;
	private String next;
	
	public DGroupFinishFloorEvent(DGroup dGroup, GameWorld finished, String next) {
		super(dGroup);
		this.finished = finished;
		this.next = next;
	}
	
	/**
	 * @return the finished
	 */
	public GameWorld getFinished() {
		return finished;
	}
	
	/**
	 * @param finished
	 * the name of the GameWorld to set
	 */
	public void setFinished(GameWorld finished) {
		this.finished = finished;
	}
	
	/**
	 * @return the next
	 */
	public String getNext() {
		return next;
	}
	
	/**
	 * @param next
	 * the name of the GameWorld to set
	 */
	public void setNext(String next) {
		this.next = next;
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
