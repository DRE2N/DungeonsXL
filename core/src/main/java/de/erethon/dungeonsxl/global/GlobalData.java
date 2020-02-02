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
package de.erethon.dungeonsxl.global;

import de.erethon.commons.config.DREConfig;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import java.io.File;
import java.util.Map.Entry;
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
        ConfigurationSection gameSigns = config.getConfigurationSection("protections.gameSigns");
        ConfigurationSection groupSigns = config.getConfigurationSection("protections.groupSigns");
        ConfigurationSection leaveSigns = config.getConfigurationSection("protections.leaveSigns");
        ConfigurationSection portals = config.getConfigurationSection("protections.portals");
        if (gameSigns != null) {
            for (String worldName : gameSigns.getValues(false).keySet()) {
                ConfigurationSection ws = gameSigns.getConfigurationSection(worldName);
                for (Entry<String, Object> entry : ws.getValues(false).entrySet()) {
                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        new GameSign(plugin, world, NumberUtil.parseInt(entry.getKey()), ws.getConfigurationSection(entry.getKey()));
                    } else {
                        new UnloadedProtection<>(plugin, GameSign.class, worldName, NumberUtil.parseInt(entry.getKey()), ws.getConfigurationSection(entry.getKey()));
                    }
                }
            }
        }

        if (groupSigns != null) {
            for (String worldName : groupSigns.getValues(false).keySet()) {
                ConfigurationSection ws = groupSigns.getConfigurationSection(worldName);
                for (Entry<String, Object> entry : ws.getValues(false).entrySet()) {
                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        new GroupSign(plugin, world, NumberUtil.parseInt(entry.getKey()), ws.getConfigurationSection(entry.getKey()));
                    } else {
                        new UnloadedProtection<>(plugin, GroupSign.class, worldName, NumberUtil.parseInt(entry.getKey()), ws.getConfigurationSection(entry.getKey()));
                    }
                }
            }
        }

        if (leaveSigns != null) {
            for (String worldName : leaveSigns.getValues(false).keySet()) {
                ConfigurationSection ws = leaveSigns.getConfigurationSection(worldName);
                for (Entry<String, Object> entry : ws.getValues(false).entrySet()) {
                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        new LeaveSign(plugin, world, NumberUtil.parseInt(entry.getKey()), ws.getConfigurationSection(entry.getKey()));
                    } else {
                        new UnloadedProtection<>(plugin, LeaveSign.class, worldName, NumberUtil.parseInt(entry.getKey()), ws.getConfigurationSection(entry.getKey()));
                    }
                }
            }
        }

        if (portals != null) {
            for (String worldName : portals.getValues(false).keySet()) {
                ConfigurationSection ws = portals.getConfigurationSection(worldName);
                for (Entry<String, Object> entry : ws.getValues(false).entrySet()) {
                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        new DPortal(plugin, world, NumberUtil.parseInt(entry.getKey()), ws.getConfigurationSection(entry.getKey()));
                    } else {
                        new UnloadedProtection<>(plugin, DPortal.class, worldName, NumberUtil.parseInt(entry.getKey()), ws.getConfigurationSection(entry.getKey()));
                    }
                }
            }
        }
    }

}
