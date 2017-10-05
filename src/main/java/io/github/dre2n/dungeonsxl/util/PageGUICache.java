/*
 * Copyright (C) 2017 Daniel Saukel
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
package io.github.dre2n.dungeonsxl.util;

import static io.github.dre2n.dungeonsxl.util.GUIButton.*;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class PageGUICache implements Listener {

    Set<PageGUI> guis = new HashSet<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ItemStack button = event.getCurrentItem();
        if (button == null || button.getType() == Material.AIR) {
            return;
        }
        Inventory inventory = event.getInventory();
        PageGUI gui = PageGUI.getByInventory(inventory);
        if (gui == null) {
            return;
        }

        int index = gui.getPages().indexOf(inventory);
        HumanEntity player = event.getWhoClicked();
        if (button.equals(PLACEHOLDER)) {
            event.setCancelled(true);
        } else if (button.equals(NEXT_PAGE)) {
            event.setCancelled(true);
            PageGUI.playSound(event);
            gui.open(player, index + 1);
        } else if (button.equals(PREVIOUS_PAGE)) {
            event.setCancelled(true);
            PageGUI.playSound(event);
            gui.open(player, index - 1);
        } else if (!gui.isStealingAllowed()) {
            event.setCancelled(true);
            PageGUI.playSound(event);
        }
    }

}
