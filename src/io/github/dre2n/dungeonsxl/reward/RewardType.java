package io.github.dre2n.dungeonsxl.reward;

public interface RewardType {
	
	/**
	 * @return the identifier
	 */
	public String getIdentifier();
	
	/**
	 * @return the handler
	 */
	public Class<? extends Reward> getHandler();
	
}
