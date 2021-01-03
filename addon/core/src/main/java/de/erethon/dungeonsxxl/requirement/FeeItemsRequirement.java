/*
 * Copyright (C) 2020-2021 Daniel Saukel
 *
 * All rights reserved.
 */
package de.erethon.dungeonsxxl.requirement;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.config.DMessage;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class FeeItemsRequirement implements Requirement {

    private DungeonsAPI api;

    private List<ItemStack> fee;

    public FeeItemsRequirement(DungeonsAPI api) {
        this.api = api;
    }

    public List<ItemStack> getFee() {
        return fee;
    }

    @Override
    public void setup(ConfigurationSection config) {
        fee = api.getCaliburn().deserializeStackList(config, "feeItems");
    }

    @Override
    public boolean check(Player player) {
        for (ItemStack stack : fee) {
            if (!player.getInventory().containsAtLeast(stack, stack.getAmount())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public BaseComponent[] getCheckMessage(Player player) {
        ComponentBuilder builder = new ComponentBuilder(DMessage.REQUIREMENT_FEE_ITEMS + ": ").color(ChatColor.GOLD);
        boolean first = true;
        for (ItemStack stack : fee) {
            String name = stack.getAmount() > 1 ? stack.getAmount() + " " : "" + api.getCaliburn().getExItem(stack).getName();
            ChatColor color = player.getInventory().containsAtLeast(stack, stack.getAmount()) ? ChatColor.GREEN : ChatColor.DARK_RED;
            if (!first) {
                builder.append(", ").color(ChatColor.WHITE);
            } else {
                first = false;
            }
            builder.append(name).color(color);
        }
        return builder.create();
    }

    @Override
    public void demand(Player player) {
        player.getInventory().removeItem(fee.toArray(new ItemStack[]{}));
    }

    @Override
    public String toString() {
        return "FeeItemsRequirement{items=" + fee + "}";
    }

}
