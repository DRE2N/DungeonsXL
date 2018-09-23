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
package de.erethon.dungeonsxl.global;

import de.erethon.commons.config.DREConfig;
import de.erethon.dungeonsxl.DungeonsXL;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents the global data.yml.
 *
 * @author Daniel Saukel
 */
public class GlobalData extends DREConfig {

    private DungeonsXL plugin;

    public static final int CONFIG_VERSION = 2;

    public GlobalData(DungeonsXL plugin, File file) {
        super(file, CONFIG_VERSION);

        this.plugin = plugin;

        if (initialize) {
            initialize();
        }
        load();
    }

    @Override
    public void initialize() {
        save();
    }

    @Override
    public void load() {
        for (World world : Bukkit.getWorlds()) {
            ConfigurationSection gameSigns = config.getConfigurationSection("protections.gameSigns." + world.getName());
            ConfigurationSection groupSigns = config.getConfigurationSection("protections.groupSigns." + world.getName());
            if (gameSigns == null && groupSigns == null) {
                continue;
            }

            int i = 0;
            while (true) {
                String key = String.valueOf("i");
                if (gameSigns != null && gameSigns.contains(key)) {
                    new GameSign(plugin, world, i, gameSigns.getConfigurationSection(key));
                }
                if (groupSigns != null && groupSigns.contains(key)) {
                    new GroupSign(plugin, world, i, groupSigns.getConfigurationSection(key));

                } else if ((gameSigns == null || !gameSigns.contains(key)) && (groupSigns == null || !groupSigns.contains(key))) {
                    break;
                }
            }
        }
    }

}
