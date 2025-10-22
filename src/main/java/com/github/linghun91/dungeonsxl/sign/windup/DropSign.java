package com.github.linghun91.dungeonsxl.sign.windup;

import com.github.linghun91.dungeonsxl.api.world.GameWorld;
import com.github.linghun91.dungeonsxl.sign.AbstractDungeonSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * DROP sign - drop items when triggered
 * @author linghun91
 */
public class DropSign extends AbstractDungeonSign {
    
    private final Material material;
    private final int amount;
    
    public DropSign(GameWorld gameWorld, Block block) {
        super(gameWorld, block);
        
        String materialName = getLine(1);
        this.material = parseMaterial(materialName);
        
        String amountStr = getLine(2);
        this.amount = parseAmount(amountStr);
    }
    
    @Override
    public String getType() {
        return "DROP";
    }
    
    @Override
    public boolean setup() {
        return true;
    }
    
    @Override
    public void trigger() {
        var loc = block.getLocation().add(0.5, 1, 0.5);
        ItemStack item = new ItemStack(material, amount);
        gameWorld.getWorld().dropItem(loc, item);
    }
    
    private Material parseMaterial(String name) {
        try {
            return Material.valueOf(name.toUpperCase());
        } catch (Exception e) {
            return Material.DIAMOND;
        }
    }
    
    private int parseAmount(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 1;
        }
    }
}
