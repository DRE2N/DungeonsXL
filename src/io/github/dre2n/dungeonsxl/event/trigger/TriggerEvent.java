package io.github.dre2n.dungeonsxl.event.trigger;

import io.github.dre2n.dungeonsxl.trigger.Trigger;

import org.bukkit.event.Event;

public abstract class TriggerEvent extends Event {
	
	protected Trigger trigger;
	
	public TriggerEvent(Trigger trigger) {
		this.trigger = trigger;
	}
	
	/**
	 * @return the trigger
	 */
	public Trigger getTrigger() {
		return trigger;
	}
	
}
