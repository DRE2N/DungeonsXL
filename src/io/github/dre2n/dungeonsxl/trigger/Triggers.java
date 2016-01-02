package io.github.dre2n.dungeonsxl.trigger;

import java.util.ArrayList;
import java.util.List;

public class Triggers {
	
	private List<TriggerType> triggers = new ArrayList<TriggerType>();
	
	public Triggers() {
		for (TriggerType type : TriggerTypeDefault.values()) {
			triggers.add(type);
		}
	}
	
	/**
	 * @return the triggers
	 */
	public List<TriggerType> getTriggers() {
		return triggers;
	}
	
	/**
	 * @param trigger
	 * the triggers to add
	 */
	public void addTrigger(TriggerType trigger) {
		triggers.add(trigger);
	}
	
	/**
	 * @param trigger
	 * the trigger to remove
	 */
	public void removeTrigger(TriggerType trigger) {
		triggers.remove(trigger);
	}
	
}
