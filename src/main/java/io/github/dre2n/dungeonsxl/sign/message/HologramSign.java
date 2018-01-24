/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package io.github.dre2n.dungeonsxl.sign.message;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.github.dre2n.caliburn.CaliburnAPI;
import io.github.dre2n.caliburn.item.UniversalItem;
import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.compatibility.Version;
import io.github.dre2n.commons.misc.EnumUtil;
import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.sign.DSignType;
import io.github.dre2n.dungeonsxl.sign.DSignTypeDefault;
import io.github.dre2n.dungeonsxl.util.LegacyUtil;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class HologramSign extends DSign {

    private DSignType type = DSignTypeDefault.HOLOGRAM;

    private Hologram hologram;

    public HologramSign(Sign sign, String[] lines, DGameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") == null) {
            markAsErroneous();
            return;
        }
        getSign().getBlock().setType(Material.AIR);

        String[] holoLines = lines[1].split("/");
        Location location = getSign().getLocation();
        location = location.add(0.5, NumberUtil.parseDouble(lines[2]), 0.5);

        hologram = HologramsAPI.createHologram(plugin, location);
        for (String line : holoLines) {
            if (line.startsWith("Item:")) {
                String id = line.replace("Item:", "");
                ItemStack item = null;

                if (Version.andHigher(Version.MC1_9).contains(CompatibilityHandler.getInstance().getVersion())) {
                    UniversalItem universalItem = CaliburnAPI.getInstance().getItems().getById(id);
                    if (universalItem != null) {
                        item = universalItem.toItemStack(1);
                    }
                }

                if (item == null) {
                    if (EnumUtil.isValidEnum(Material.class, id)) {
                        item = new ItemStack(Material.valueOf(id));

                    } else {
                        item = new ItemStack(LegacyUtil.getMaterial(NumberUtil.parseInt(id, 1)));
                    }
                }

                hologram.appendItemLine(item);

            } else {
                hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
