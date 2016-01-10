package io.github.dre2n.dungeonsxl.reward;

public enum RewardTypeDefault implements RewardType {
	
	MONEY("money", MoneyReward.class),
	LOOT_INVENTORY("loot", Reward.class);
	
	private String identifier;
	private Class<? extends Reward> handler;
	
	RewardTypeDefault(String identifier, Class<? extends Reward> handler) {
		this.identifier = identifier;
		this.handler = handler;
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public Class<? extends Reward> getHandler() {
		return handler;
	}
	
}
