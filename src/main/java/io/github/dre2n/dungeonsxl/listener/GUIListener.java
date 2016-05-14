/*
 * Copyright (C) 2016 Daniel Saukel
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
package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.commons.util.guiutil.ButtonClickEvent;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.announcer.Announcer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class GUIListener implements Listener {

    DungeonsXL plugin = DungeonsXL.getInstance();

    @EventHandler
    public void onButtonClick(ButtonClickEvent event) {
        Inventory gui = event.getGUI();
        if (!plugin.getGUIs().contains(gui)) {
            return;
        }

        ItemStack button = event.getGUI().getItem(event.getSlot());
        Announcer announcer = plugin.getAnnouncers().getByGUI(gui);
        if (announcer != null) {
            announcer.clickGroupButton(event.getPlayer(), button);
        }
    }

}
