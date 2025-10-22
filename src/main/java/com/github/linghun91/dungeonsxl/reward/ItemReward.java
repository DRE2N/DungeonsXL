package com.github.linghun91.dungeonsxl.reward;

import com.github.linghun91.dungeonsxl.api.Reward;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Item reward
 * @author linghun91
 */
public class ItemReward implements Reward {
    
    private final ItemStack item;
    
    public ItemReward(ItemStack item) {
        this.item = item;
    }
    
    @Override
    public String getType() {
        return "ITEM";
    }
    
    @Override
    public void give(Player player) {
        player.getInventory().addItem(item);
        player.sendMessage("You received " + item.getAmount() + "x " + item.getType().name());
    }
}
