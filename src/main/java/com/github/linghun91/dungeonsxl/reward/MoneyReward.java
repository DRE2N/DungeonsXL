package com.github.linghun91.dungeonsxl.reward;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.Reward;
import org.bukkit.entity.Player;

/**
 * Money reward
 * @author linghun91
 */
public class MoneyReward implements Reward {
    
    private final double amount;
    
    public MoneyReward(double amount) {
        this.amount = amount;
    }
    
    @Override
    public String getType() {
        return "MONEY";
    }
    
    @Override
    public void give(Player player) {
        var vault = DungeonsXL.getInstance().getVaultIntegration();
        if (vault != null && vault.isEnabled()) {
            vault.getEconomy().depositPlayer(player, amount);
            player.sendMessage("You received $" + amount);
        }
    }
}
