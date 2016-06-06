/*
 * Copyright (C) 2012-2016 Frank Baumann
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
package io.github.dre2n.dungeonsxl.settings;

import io.github.dre2n.commons.util.guiutil.GUIUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class Settings {

    DungeonsXL plugin = DungeonsXL.getInstance();

    private String title = DMessages.SETTINGS_TITLE.getMessage();
    private List<Button> buttons = new ArrayList<>();

    public Settings(String titleSuffix, Button... buttons) {
        this.title += titleSuffix;
        this.buttons = Arrays.asList(buttons);
    }

    /* Getters and setters */
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the buttons
     */
    public List<Button> getButtons() {
        return buttons;
    }

    /**
     * @param button
     * the Button to add
     */
    public void addButton(Button button) {
        buttons.add(button);
    }

    /**
     * @param button
     * the Button to remove
     */
    public void removeButton(Button button) {
        buttons.remove(button);
    }

    /* Actions */
    /**
     * @param player
     * the Player whose data fills the details of the button text
     * @return a List of ItemStacks to be used in a inventory GUI
     */
    public List<ItemStack> toItemStacks(Player player) {
        List<ItemStack> buttons = new ArrayList<>();
        for (Button button : this.buttons) {
            if (player.hasPermission(button.getRequiredPermission())) {
                buttons.add(button.toItemStack(player));
            }
        }
        return buttons;
    }

    /**
     * @param player
     * show the GUI to this Player
     */
    public void showGUI(Player player) {
        Inventory gui = GUIUtil.createGUI(plugin, title, toItemStacks(player));
        plugin.addGUI(gui);
        player.closeInventory();
        player.openInventory(gui);
    }

    /**
     * @param player
     * the player who clicked
     * @param button
     * the clicked button
     */
    public void clickButton(Player player, ItemStack button) {
        for (Button type : buttons) {
            if (type.toItemStack(player).equals(button)) {
                type.onClick(player);
            }
        }

        showGUI(player);
    }

}
