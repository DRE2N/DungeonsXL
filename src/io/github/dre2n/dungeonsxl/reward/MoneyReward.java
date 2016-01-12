package io.github.dre2n.dungeonsxl.reward;

import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

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
	 * the money to add
	 */
	public void addMoney(double money) {
		this.money += money;
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
		if (plugin.getEconomyProvider() == null) {
			return;
		}
		
		plugin.getEconomyProvider().depositPlayer(player, money);
		MessageUtil.sendMessage(player, plugin.getDMessages().getMessage(Messages.REWARD_MONEY, plugin.getEconomyProvider().format(money)));
	}
	
	@Override
	public RewardType getType() {
		return type;
	}
	
}
