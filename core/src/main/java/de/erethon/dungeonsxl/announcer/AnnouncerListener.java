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
package de.erethon.dungeonsxl.announcer;

import de.erethon.caliburn.category.Category;
import de.erethon.dungeonsxl.DungeonsXL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class AnnouncerListener implements Listener {

    private AnnouncerCache announcers;

    public AnnouncerListener(DungeonsXL plugin) {
        announcers = plugin.getAnnouncerCache();
    }

    @EventHandler
    public void onButtonClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        ItemStack button = event.getCurrentItem();
        Announcer announcer = announcers.getByGUI(event.getView());
        if (announcer != null && button != null && Category.WOOL.containsMaterial(button.getType())) {
            announcer.clickGroupButton(player, button);
        }
    }

}
