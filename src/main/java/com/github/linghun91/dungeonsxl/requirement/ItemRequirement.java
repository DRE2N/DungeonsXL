package com.github.linghun91.dungeonsxl.requirement;

import com.github.linghun91.dungeonsxl.api.Requirement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Requirement for having specific items
 */
public class ItemRequirement implements Requirement {
    
    private final Material material;
    private final int amount;
    private final boolean consume;
    
    public ItemRequirement(Material material, int amount, boolean consume) {
        this.material = material;
        this.amount = amount;
        this.consume = consume;
    }
    
    @Override
    public boolean check(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        
        if (count >= amount) {
            if (consume) {
                // Remove items
                int remaining = amount;
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType() == material) {
                        if (item.getAmount() >= remaining) {
                            item.setAmount(item.getAmount() - remaining);
                            break;
                        } else {
                            remaining -= item.getAmount();
                            item.setAmount(0);
                        }
                    }
                }
            }
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getType() {
        return "item";
    }
    
    @Override
    public String getErrorMessage() {
        return "You need " + amount + "x " + material.name() + " to enter!";
    }
}
