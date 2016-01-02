package io.github.dre2n.dungeonsxl.trigger;

public enum TriggerTypeDefault implements TriggerType {
	
	DISTANCE("D", DistanceTrigger.class),
	INTERACT("I", InteractTrigger.class),
	MOB("M", MobTrigger.class),
	REDSTONE("R", RedstoneTrigger.class),
	SIGN("T", SignTrigger.class),
	USE_ITEM("U", UseItemTrigger.class);
	
	private String identifier;
	private Class<? extends Trigger> handler;
	
	TriggerTypeDefault(String identifier, Class<? extends Trigger> handler) {
		this.identifier = identifier;
		this.handler = handler;
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public Class<? extends Trigger> getHandler() {
		return handler;
	}
	
}
