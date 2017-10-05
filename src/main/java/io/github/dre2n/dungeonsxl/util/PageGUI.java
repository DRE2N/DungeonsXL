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

import io.github.dre2n.dungeonsxl.DungeonsXL;
import static io.github.dre2n.dungeonsxl.util.GUIButton.*;
import java.util.Stack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class PageGUI {

    private String title;
    private boolean allowStealing;
    private Stack<Inventory> pages = new Stack<>();

    public PageGUI(String title) {
        this(title, false);
    }

    public PageGUI(String title, boolean allowStealing) {
        this.title = title;
        this.allowStealing = allowStealing;
        newPage();
        DungeonsXL.getInstance().getPageGUICache().guis.add(this);
    }

    public Inventory newPage() {
        Inventory gui = Bukkit.createInventory(null, 54, title);
        gui.setItem(45, PREVIOUS_PAGE);
        gui.setItem(46, PLACEHOLDER);
        gui.setItem(47, PLACEHOLDER);
        gui.setItem(48, PLACEHOLDER);
        gui.setItem(49, PLACEHOLDER);
        gui.setItem(50, PLACEHOLDER);
        gui.setItem(51, PLACEHOLDER);
        gui.setItem(52, PLACEHOLDER);
        gui.setItem(53, NEXT_PAGE);
        pages.add(gui);
        return gui;
    }

    public void addButton(ItemStack button) {
        if (hasSpace(pages.peek())) {
            pages.peek().addItem(button);
        } else {
            newPage().addItem(button);
        }
    }

    public String getTitle() {
        return title;
    }

    public boolean isStealingAllowed() {
        return allowStealing;
    }

    public void setStealingAllowed(boolean allowed) {
        allowStealing = allowed;
    }

    public Stack<Inventory> getPages() {
        return pages;
    }

    public void open(HumanEntity player) {
        player.openInventory(pages.get(0));
    }

    public void open(HumanEntity player, int page) {
        if (pages.size() - 1 >= page && page >= 0) {
            player.openInventory(pages.get(page));
        }
    }

    public void clear() {
        pages.clear();
        newPage();
    }

    /* Statics */
    public static boolean hasSpace(Inventory inventory) {
        for (ItemStack stack : inventory.getContents()) {
            if (stack == null || stack.getType() == Material.AIR) {
                return true;
            }
        }
        return false;
    }

    public static void playSound(InventoryClickEvent event) {
        if (event.getSlot() == -999) {
            return;
        }

        HumanEntity human = event.getWhoClicked();
        if (!(human instanceof Player)) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        if (clicked.getType() == Material.BARRIER) {
            ((Player) human).playSound(human.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
        } else if (clicked != null && !clicked.equals(PLACEHOLDER)) {
            ((Player) human).playSound(human.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        }
    }

    public static boolean isPageGUI(Inventory inventory) {
        return inventory != null && inventory.getSize() == 54 && PREVIOUS_PAGE.equals(inventory.getItem(45)) && PLACEHOLDER.equals(inventory.getItem(49));
    }

    public static PageGUI getByInventory(Inventory inventory) {
        for (PageGUI gui : DungeonsXL.getInstance().getPageGUICache().guis) {
            for (Inventory page : gui.pages) {
                if (page.getTitle().equals(inventory.getTitle())) {
                    return gui;
                }
            }
        }
        return null;
    }

}
