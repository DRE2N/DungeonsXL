package io.github.dre2n.dungeonsxl.requirement;

public interface RequirementType {
	
	/**
	 * @return the identifier
	 */
	public String getIdentifier();
	
	/**
	 * @return the handler
	 */
	public Class<? extends Requirement> getHandler();
	
}
