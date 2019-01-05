/*
 * Copyright (C) 2012-2019 Frank Baumann
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
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import java.io.File;
import java.util.Map.Entry;
import java.util.Random;
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
            ConfigurationSection leaveSigns = config.getConfigurationSection("protections.leaveSigns." + world.getName());
            ConfigurationSection portals = config.getConfigurationSection("protections.portals." + world.getName());
            Random random = new Random();
            if (gameSigns != null) {
                for (Entry<String, Object> entry : gameSigns.getValues(false).entrySet()) {
                    new GameSign(plugin, world, NumberUtil.parseInt(entry.getKey(), random.nextInt()), gameSigns.getConfigurationSection(entry.getKey()));
                }
            }
            if (groupSigns != null) {
                for (Entry<String, Object> entry : groupSigns.getValues(false).entrySet()) {
                    new GroupSign(plugin, world, NumberUtil.parseInt(entry.getKey(), random.nextInt()), groupSigns.getConfigurationSection(entry.getKey()));
                }
            }
            if (leaveSigns != null) {
                for (Entry<String, Object> entry : leaveSigns.getValues(false).entrySet()) {
                    new LeaveSign(plugin, world, NumberUtil.parseInt(entry.getKey(), random.nextInt()), leaveSigns.getConfigurationSection(entry.getKey()));
                }
            }
            if (portals != null) {
                for (Entry<String, Object> entry : portals.getValues(false).entrySet()) {
                    new DPortal(plugin, world, NumberUtil.parseInt(entry.getKey(), random.nextInt()), portals.getConfigurationSection(entry.getKey()));
                }
            }
        }
    }

}
