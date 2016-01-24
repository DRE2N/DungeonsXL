package io.github.dre2n.dungeonsxl.requirement;

import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import org.bukkit.entity.Player;

public class FeeRequirement extends Requirement {
	
	private RequirementType type = RequirementTypeDefault.FEE;
	
	private double fee;
	
	/**
	 * @return the fee
	 */
	public double getFee() {
		return fee;
	}
	
	/**
	 * @param fee
	 * the fee to set
	 */
	public void setFee(double fee) {
		this.fee = fee;
	}
	
	@Override
	public boolean check(Player player) {
		if (plugin.getEconomyProvider() == null) {
			return true;
		}
		
		if (plugin.getEconomyProvider().getBalance(player) >= fee) {
			return true;
			
		} else {
			return false;
		}
	}
	
	@Override
	public void demand(Player player) {
		if (plugin.getEconomyProvider() == null) {
			return;
		}
		
		plugin.getEconomyProvider().withdrawPlayer(player, fee);
		MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.REQUIREMENT_FEE, plugin.getEconomyProvider().format(fee)));
	}
	
	@Override
	public RequirementType getType() {
		return type;
	}
	
}
