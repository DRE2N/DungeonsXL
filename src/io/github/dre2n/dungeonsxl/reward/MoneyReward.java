package io.github.dre2n.dungeonsxl.reward;

import org.bukkit.entity.Player;

public class MoneyReward extends Reward {
	
	private RewardType type = RewardTypeDefault.MONEY;
	
	private double money;
	
	/**
	 * @return the money
	 */
	public double getMoney() {
		return money;
	}
	
	/**
	 * @param money
	 * the money to set
	 */
	public void setMoney(double money) {
		this.money = money;
	}
	
	@Override
	public void giveTo(Player player) {
		if (plugin.getEconomyProvider() != null) {
			plugin.getEconomyProvider().depositPlayer(player, money);
		}
	}
	
	@Override
	public RewardType getType() {
		return type;
	}
	
}
