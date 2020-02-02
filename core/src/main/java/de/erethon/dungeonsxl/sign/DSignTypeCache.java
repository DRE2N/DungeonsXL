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
package de.erethon.dungeonsxl.sign;

import de.erethon.dungeonsxl.DungeonsXL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;

/**
 * DSignType instance manager.
 *
 * @author Daniel Saukel
 */
public class DSignTypeCache {

    private DungeonsXL plugin;

    private List<DSignType> types = new ArrayList<>();

    public DSignTypeCache(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    public void init() {
        types.addAll(Arrays.asList(DSignTypeDefault.values()));
        Bukkit.getPluginManager().registerEvents(new DSignListener(plugin), plugin);
    }

    /**
     * @return the DSign types
     */
    public List<DSignType> getDSigns() {
        return types;
    }

    /**
     * @param type the type to add
     */
    public void addDSign(DSignType type) {
        types.add(type);
    }

    /**
     * @param type the type to remove
     */
    public void removeDSign(DSignType type) {
        types.remove(type);
    }

}
