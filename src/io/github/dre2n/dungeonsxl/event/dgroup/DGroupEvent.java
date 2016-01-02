package io.github.dre2n.dungeonsxl.event.dgroup;

import io.github.dre2n.dungeonsxl.player.DGroup;

import org.bukkit.event.Event;

public abstract class DGroupEvent extends Event {
	
	private DGroup dGroup;
	
	public DGroupEvent(DGroup dGroup) {
		this.dGroup = dGroup;
	}
	
	/**
	 * @return the dGroup
	 */
	public DGroup getDGroup() {
		return dGroup;
	}
	
	/**
	 * @param dGroup
	 * the dGroup to set
	 */
	public void setDGroup(DGroup dGroup) {
		this.dGroup = dGroup;
	}
	
}
