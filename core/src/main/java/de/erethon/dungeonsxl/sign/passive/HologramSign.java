/*
 * Copyright (C) 2012-2022 Frank Baumann
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
package de.erethon.dungeonsxl.sign.passive;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import de.erethon.caliburn.item.ExItem;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.sign.Passive;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class HologramSign extends Passive {

    private Hologram hologram;

    public HologramSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "Hologram";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".hologram";
    }

    @Override
    public boolean isOnDungeonInit() {
        return true;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isSetToAir() {
        return true;
    }

    @Override
    public boolean validate() {
        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") == null) {
            markAsErroneous("HolographicDisplays not enabled");
            return false;
        }
        return true;
    }

    @Override
    public void initialize() {
        String text = getGameWorld().getDungeon().getRules().getState(GameRule.MESSAGES).get(NumberUtil.parseInt(getLine(1)));
        if (text == null) {
            markAsErroneous("Unknown message, ID: " + getLine(1));
            return;
        }
        String[] holoLines = text.split("(?i)<br>");
        Location location = getSign().getLocation();
        location = location.add(0.5, NumberUtil.parseDouble(getLine(2), 2.0), 0.5);

        hologram = HologramsAPI.createHologram(api, location);
        for (String line : holoLines) {
            if (line.startsWith("Item:")) {
                String id = line.replace("Item:", "");
                ItemStack item = null;

                ExItem exItem = api.getCaliburn().getExItem(id);
                if (exItem != null) {
                    item = exItem.toItemStack();
                }

                hologram.appendItemLine(item);

            } else {
                hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
    }

}
