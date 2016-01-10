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
	 * @return the trigger which has the identifier
	 */
	public TriggerType getByIdentifier(String identifier) {
		for (TriggerType trigger : triggers) {
			if (trigger.getIdentifier().equalsIgnoreCase(identifier)) {
				return trigger;
			}
		}
		
		return null;
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
