package io.github.dre2n.dungeonsxl.requirement;

public enum RequirementTypeDefault implements RequirementType {
	
	FEE("fee", FeeRequirement.class);
	
	private String identifier;
	private Class<? extends Requirement> handler;
	
	RequirementTypeDefault(String identifier, Class<? extends Requirement> handler) {
		this.identifier = identifier;
		this.handler = handler;
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public Class<? extends Requirement> getHandler() {
		return handler;
	}
	
}
