package io.github.dre2n.dungeonsxl.requirement;

import java.util.ArrayList;
import java.util.List;

public class Requirements {
	
	private List<RequirementType> requirements = new ArrayList<RequirementType>();
	
	public Requirements() {
		for (RequirementType type : RequirementTypeDefault.values()) {
			requirements.add(type);
		}
	}
	
	/**
	 * @return the requirement which has the identifier
	 */
	public RequirementType getByIdentifier(String identifier) {
		for (RequirementType requirement : requirements) {
			if (requirement.getIdentifier().equals(identifier)) {
				return requirement;
			}
		}
		
		return null;
	}
	
	/**
	 * @return the requirements
	 */
	public List<RequirementType> getRequirements() {
		return requirements;
	}
	
	/**
	 * @param requirement
	 * the requirement to add
	 */
	public void addRequirement(RequirementType requirement) {
		requirements.add(requirement);
	}
	
	/**
	 * @param requirement
	 * the requirement to remove
	 */
	public void removeRequirement(RequirementType requirement) {
		requirements.remove(requirement);
	}
	
}
