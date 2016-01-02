package io.github.dre2n.dungeonsxl.reward;

import java.util.ArrayList;
import java.util.List;

public class Rewards {
	
	private List<RewardType> rewards = new ArrayList<RewardType>();
	
	public Rewards() {
		for (RewardType type : RewardTypeDefault.values()) {
			rewards.add(type);
		}
	}
	
	/**
	 * @return the reward which has the identifier
	 */
	public RewardType getByIdentifier(String identifier) {
		for (RewardType reward : rewards) {
			if (reward.getIdentifier().equals(identifier)) {
				return reward;
			}
		}
		
		return null;
	}
	
	/**
	 * @return the rewards
	 */
	public List<RewardType> getRewards() {
		return rewards;
	}
	
	/**
	 * @param reward
	 * the reward to add
	 */
	public void addReward(RewardType reward) {
		rewards.add(reward);
	}
	
	/**
	 * @param reward
	 * the reward to remove
	 */
	public void removeReward(RewardType reward) {
		rewards.remove(reward);
	}
	
}
