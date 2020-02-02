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
package de.erethon.dungeonsxl.player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import de.erethon.dungeonsxl.DungeonsXL;

/**
 * @author Daniel Saukel
 */
public class DGroupTag {

    private DGamePlayer player;
    private Hologram hologram;

    public DGroupTag(DungeonsXL plugin, DGamePlayer player) {
        this.player = player;
        DGroup group = player.getDGroup();
        if (group != null) {
            hologram = HologramsAPI.createHologram(plugin, player.getPlayer().getLocation().clone().add(0, 3.5, 0));
            hologram.appendItemLine(group.getDColor().getWoolMaterial().toItemStack());
            hologram.appendTextLine(group.getName());
        }
    }

    public void update() {
        hologram.teleport(player.getPlayer().getLocation().clone().add(0, 3.5, 0));
    }

}
