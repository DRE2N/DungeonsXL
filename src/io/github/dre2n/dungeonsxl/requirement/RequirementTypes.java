package io.github.dre2n.dungeonsxl.requirement;

import java.util.ArrayList;
import java.util.List;

public class RequirementTypes {
	
	private List<RequirementType> types = new ArrayList<RequirementType>();
	
	public RequirementTypes() {
		for (RequirementType type : RequirementTypeDefault.values()) {
			types.add(type);
		}
	}
	
	/**
	 * @return the requirement type which has the identifier
	 */
	public RequirementType getByIdentifier(String identifier) {
		for (RequirementType type : types) {
			if (type.getIdentifier().equals(identifier)) {
				return type;
			}
		}
		
		return null;
	}
	
	/**
	 * @return the requirement types
	 */
	public List<RequirementType> getRequirements() {
		return types;
	}
	
	/**
	 * @param type
	 * the requirement type to add
	 */
	public void addRequirement(RequirementType type) {
		types.add(type);
	}
	
	/**
	 * @param type
	 * the requirement type to remove
	 */
	public void removeRequirement(RequirementType type) {
		types.remove(type);
	}
	
}
