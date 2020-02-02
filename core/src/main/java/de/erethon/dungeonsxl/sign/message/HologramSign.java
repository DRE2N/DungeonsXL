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
package de.erethon.dungeonsxl.sign.message;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.sign.DSign;
import de.erethon.dungeonsxl.sign.DSignType;
import de.erethon.dungeonsxl.sign.DSignTypeDefault;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class HologramSign extends DSign {

    private Hologram hologram;

    public HologramSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") == null) {
            markAsErroneous("HolographicDisplays not enabled");
            return;
        }
        getSign().getBlock().setType(VanillaItem.AIR.getMaterial());

        String[] holoLines = lines[1].split("/");
        Location location = getSign().getLocation();
        location = location.add(0.5, NumberUtil.parseDouble(lines[2]), 0.5);

        hologram = HologramsAPI.createHologram(plugin, location);
        for (String line : holoLines) {
            if (line.startsWith("Item:")) {
                String id = line.replace("Item:", "");
                ItemStack item = null;

                ExItem exItem = plugin.getCaliburn().getExItem(id);
                if (exItem != null) {
                    item = exItem.toItemStack();
                }

                hologram.appendItemLine(item);

            } else {
                hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.HOLOGRAM;
    }

}
