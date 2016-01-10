package io.github.dre2n.dungeonsxl.event.reward;

import org.bukkit.event.Event;

import io.github.dre2n.dungeonsxl.reward.Reward;

public abstract class RewardEvent extends Event {
	
	protected Reward reward;
	
	public RewardEvent(Reward reward) {
		this.reward = reward;
	}
	
	/**
	 * @return the reward
	 */
	public Reward getReward() {
		return reward;
	}
	
	/**
	 * @param reward
	 * the reward to set
	 */
	public void setReward(Reward reward) {
		this.reward = reward;
	}
	
}
