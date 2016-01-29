package io.github.dre2n.dungeonsxl.reward;

import java.util.ArrayList;
import java.util.List;

public class RewardTypes {
	
	private List<RewardType> types = new ArrayList<RewardType>();
	
	public RewardTypes() {
		for (RewardType type : RewardTypeDefault.values()) {
			types.add(type);
		}
	}
	
	/**
	 * @return the reward type which has the identifier
	 */
	public RewardType getByIdentifier(String identifier) {
		for (RewardType type : types) {
			if (type.getIdentifier().equals(identifier)) {
				return type;
			}
		}
		
		return null;
	}
	
	/**
	 * @return the reward types
	 */
	public List<RewardType> getRewards() {
		return types;
	}
	
	/**
	 * @param type
	 * the reward type to add
	 */
	public void addReward(RewardType type) {
		types.add(type);
	}
	
	/**
	 * @param type
	 * the reward type to remove
	 */
	public void removeReward(RewardType type) {
		types.remove(type);
	}
	
}
