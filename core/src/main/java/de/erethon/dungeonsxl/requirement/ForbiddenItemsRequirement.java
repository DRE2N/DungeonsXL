/*
 * Copyright (C) 2012-2020 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.requirement;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.item.ExItem;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.config.DMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class ForbiddenItemsRequirement implements Requirement {

    private CaliburnAPI caliburn;

    private List<ExItem> forbiddenItems;

    public ForbiddenItemsRequirement(DungeonsAPI api) {
        caliburn = api.getCaliburn();
    }

    /* Getters and setters */
    /**
     * @return the forbidden items
     */
    public List<ExItem> getForbiddenItems() {
        return forbiddenItems;
    }

    /* Actions */
    @Override
    public void setup(ConfigurationSection config) {
        forbiddenItems = caliburn.deserializeExItemList(config, "forbiddenItems");
    }

    @Override
    public boolean check(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                continue;
            }
            ExItem exItem = caliburn.getExItem(item);
            if (forbiddenItems.contains(exItem)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public BaseComponent[] getCheckMessage(Player player) {
        ComponentBuilder builder = new ComponentBuilder(DMessage.REQUIREMENT_FORBIDDEN_ITEMS.getMessage() + ": ").color(ChatColor.GOLD);

        Set<ExItem> exInventory = new HashSet<>();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                exInventory.add(caliburn.getExItem(item));
            }
        }

        boolean first = true;
        for (ExItem forbiddenItem : forbiddenItems) {
            ChatColor color = exInventory.contains(forbiddenItem) ? ChatColor.DARK_RED : ChatColor.GREEN;
            if (!first) {
                builder.append(", ").color(ChatColor.WHITE);
            } else {
                first = false;
            }
            builder.append(forbiddenItem.getName()).color(color);
        }

        return builder.create();
    }

    @Override
    public void demand(Player player) {
    }

    @Override
    public String toString() {
        return "ForbiddenItemsRequirement{items=" + forbiddenItems + "}";
    }

}
