package io.github.dre2n.dungeonsxl.event.dplayer;

import io.github.dre2n.dungeonsxl.player.DPlayer;

import org.bukkit.event.Event;

public abstract class DPlayerEvent extends Event {
	
	private DPlayer dPlayer;
	
	public DPlayerEvent(DPlayer dPlayer) {
		this.dPlayer = dPlayer;
	}
	
	/**
	 * @return the dPlayer
	 */
	public DPlayer getDPlayer() {
		return dPlayer;
	}
	
	/**
	 * @param dPlayer
	 * the dPlayer to set
	 */
	public void setDPlayer(DPlayer dPlayer) {
		this.dPlayer = dPlayer;
	}
	
}
