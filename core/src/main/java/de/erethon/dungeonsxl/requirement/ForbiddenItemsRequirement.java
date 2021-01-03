/*
 * Copyright (C) 2012-2021 Frank Baumann
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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

    private Map<ExItem, Boolean> forbiddenItems = new HashMap<>();

    public ForbiddenItemsRequirement(DungeonsAPI api) {
        caliburn = api.getCaliburn();
    }

    /* Getters and setters */
    /**
     * @return the forbidden items (key) and if the check is deep (value)
     */
    public Map<ExItem, Boolean> getForbiddenItems() {
        return forbiddenItems;
    }

    /* Actions */
    @Override
    public void setup(ConfigurationSection config) {
        for (String entry : config.getStringList("forbiddenItems")) {
            if (entry == null) {
                continue;
            }
            boolean star = !entry.contains("*");
            entry = entry.replace("*", "");
            ExItem item = caliburn.getExItem(entry);
            if (item != null) {
                forbiddenItems.put(item, star);
            }
        }
    }

    @Override
    public boolean check(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                continue;
            }
            ExItem exItem = caliburn.getExItem(item);
            for (Entry<ExItem, Boolean> entry : forbiddenItems.entrySet()) {
                if (entry.getValue()) {
                    if (exItem.isSubsumableUnder(entry.getKey())) {
                        return false;
                    }
                } else {
                    if (exItem.equals(entry.getKey())) {
                        return false;
                    }
                }
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
        for (Entry<ExItem, Boolean> entry : forbiddenItems.entrySet()) {
            boolean contains = containsItem(exInventory, entry.getKey(), entry.getValue());
            ChatColor color = contains ? ChatColor.DARK_RED : ChatColor.GREEN;
            if (!first) {
                builder.append(", ").color(ChatColor.WHITE);
            } else {
                first = false;
            }
            builder.append(entry.getKey().getName()).color(color);
        }

        return builder.create();
    }

    private boolean containsItem(Set<ExItem> exInventory, ExItem forbiddenItem, boolean deepCheck) {
        for (ExItem item : exInventory) {
            if ((deepCheck && item.isSubsumableUnder(forbiddenItem)) || (!deepCheck && item.equals(forbiddenItem))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void demand(Player player) {
    }

    @Override
    public String toString() {
        return "ForbiddenItemsRequirement{items=" + forbiddenItems + "}";
    }

}
