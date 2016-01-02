package io.github.dre2n.dungeonsxl.trigger;

public interface TriggerType {
	
	/**
	 * @return the identifier
	 */
	public String getIdentifier();
	
	/**
	 * @return the handler
	 */
	public Class<? extends Trigger> getHandler();
	
}
