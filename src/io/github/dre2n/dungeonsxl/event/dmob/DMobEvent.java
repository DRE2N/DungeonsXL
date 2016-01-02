package io.github.dre2n.dungeonsxl.event.dmob;

import io.github.dre2n.dungeonsxl.mob.DMob;

import org.bukkit.event.Event;

public abstract class DMobEvent extends Event {
	
	private DMob dMob;
	
	public DMobEvent(DMob dMob) {
		this.dMob = dMob;
	}
	
	/**
	 * @return the dMob
	 */
	public DMob getDMob() {
		return dMob;
	}
	
	/**
	 * @param dMob
	 * the dMob to set
	 */
	public void setDMob(DMob dMob) {
		this.dMob = dMob;
	}
	
}
