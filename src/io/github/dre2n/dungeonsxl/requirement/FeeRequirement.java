package io.github.dre2n.dungeonsxl.requirement;

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
		if (plugin.getEconomyProvider() != null) {
			plugin.getEconomyProvider().withdrawPlayer(player, fee);
		}
	}
	
	@Override
	public RequirementType getType() {
		return type;
	}
	
}
