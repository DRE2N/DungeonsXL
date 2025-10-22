package com.github.linghun91.dungeonsxl.requirement;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.Requirement;
import org.bukkit.entity.Player;

/**
 * Money fee requirement
 * @author linghun91
 */
public class FeeMoneyRequirement implements Requirement {
    
    private final double amount;
    
    public FeeMoneyRequirement(double amount) {
        this.amount = amount;
    }
    
    @Override
    public String getType() {
        return "FEE_MONEY";
    }
    
    @Override
    public boolean check(Player player) {
        var vault = DungeonsXL.getInstance().getVaultIntegration();
        if (vault == null || !vault.isEnabled()) return true;
        
        return vault.getEconomy().has(player, amount);
    }
    
    @Override
    public String getFailureMessage() {
        return "You need $" + amount + " to enter!";
    }
    
    @Override
    public void demand(Player player) {
        var vault = DungeonsXL.getInstance().getVaultIntegration();
        if (vault != null && vault.isEnabled()) {
            vault.getEconomy().withdrawPlayer(player, amount);
        }
    }
}
