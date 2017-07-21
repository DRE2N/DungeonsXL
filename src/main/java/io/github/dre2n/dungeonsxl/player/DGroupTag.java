/*
 * Copyright (C) 2012-2017 Frank Baumann
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
package io.github.dre2n.dungeonsxl.player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class DGroupTag {

    private DGamePlayer player;
    private Hologram hologram;

    public DGroupTag(DGamePlayer player) {
        this.player = player;
        DGroup group = player.getDGroup();
        if (group != null) {
            hologram = HologramsAPI.createHologram(DungeonsXL.getInstance(), player.getPlayer().getLocation().clone().add(0, 3.5, 0));
            hologram.appendItemLine(new ItemStack(Material.WOOL, 1, group.getDColor().getWoolData()));
            hologram.appendTextLine(group.getName());
        }
    }

    public void update() {
        hologram.teleport(player.getPlayer().getLocation().clone().add(0, 3.5, 0));
    }

}
