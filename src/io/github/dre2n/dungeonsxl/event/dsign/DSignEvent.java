package io.github.dre2n.dungeonsxl.event.dsign;

import org.bukkit.event.Event;

import io.github.dre2n.dungeonsxl.sign.DSign;

public abstract class DSignEvent extends Event {
	
	protected DSign dSign;
	
	public DSignEvent(DSign dSign) {
		this.dSign = dSign;
	}
	
	/**
	 * @return the dSign
	 */
	public DSign getDSign() {
		return dSign;
	}
	
	/**
	 * @param dSign
	 * the dSign to set
	 */
	public void setDSign(DSign dSign) {
		this.dSign = dSign;
	}
	
}
