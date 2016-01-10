package io.github.dre2n.dungeonsxl.event.requirement;

import io.github.dre2n.dungeonsxl.requirement.Requirement;

import org.bukkit.event.Event;

public abstract class RequirementEvent extends Event {
	
	protected Requirement requirement;
	
	public RequirementEvent(Requirement requirement) {
		this.requirement = requirement;
	}
	
	/**
	 * @return the requirement
	 */
	public Requirement getRequirement() {
		return requirement;
	}
	
	/**
	 * @param requirement
	 * the requirement to set
	 */
	public void setRequirement(Requirement requirement) {
		this.requirement = requirement;
	}
	
}
