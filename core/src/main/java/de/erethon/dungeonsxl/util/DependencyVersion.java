/*
 * Copyright (C) 2012-2013 Frank Baumann; 2015-2026 Daniel Saukel
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
package de.erethon.dungeonsxl.util;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.xlib.compatibility.Version;
import de.erethon.xlib.plugin.PluginMeta;
import de.erethon.xlib.spiget.comparator.VersionComparator;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Lists compatible plugin versions.
 *
 * @author Daniel Saukel
 */
public enum DependencyVersion {

    XLIB("XLib-Runtime", getProperties().getProperty("dependencyVersion.xlib")),
    BOSSSHOP("BossShop", getProperties().getProperty("dependencyVersion.bossshop")),
    CITIZENS("Citizens", getProperties().getProperty("dependencyVersion.citizens")),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays", getProperties().getProperty("dependencyVersion.holographicdisplays")),
    MODERN_LWC("LWC", "2.1.5-09ad392"),
    PARTIES("Parties", getProperties().getProperty("dependencyVersion.parties")),
    PLACEHOLDER_API("PlaceholderAPI", getProperties().getProperty("dependencyVersion.placeholderapi")),
    VAULT("Vault", "1.7.3-b131"),
    // Two public plugins share this name
    CUSTOM_MOBS("CustomMobs", "4.17", s -> {
        try {
            Class.forName("de.hellfirepvp.CustomMobs");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }),
    INSANE_MOBS("InsaneMobs2", "3.0.1"),
    MYTHIC_MOBS("MythicMobs", "5.11.2-6a371d59");

    /**
     * Meta information about this project.
     */
    public static final PluginMeta META = new PluginMeta.Builder("DungeonsXL")
            .minVersion(Version.MC1_8)
            .paperState(PluginMeta.State.NOT_SUPPORTED)
            .spigotState(PluginMeta.State.SUPPORTED)
            .economyState(PluginMeta.State.SUPPORTED)
            .permissionsState(PluginMeta.State.SUPPORTED)
            .spigotMCResourceId(9488)
            .bStatsResourceId(1039)
            .versionComparator(VersionComparator.SEM_VER_SNAPSHOT)
            .build();

    private static Properties properties;

    private String name;
    private String version;
    private Plugin plugin;

    DependencyVersion(String name, String version) {
        this(name, version, null);
    }

    DependencyVersion(String name, String version, Predicate<String> enabled) {
        this.name = name;
        this.version = version;
        if (enabled == null || enabled.test(name)) {
            plugin = Bukkit.getPluginManager().getPlugin(name);
        }
    }

    public String getName() {
        return name;
    }

    public String getSupportedVersion() {
        return version;
    }

    public String getEnabledVersion() {
        return plugin.getDescription().getVersion();
    }

    public boolean isEnabled() {
        return plugin != null;
    }

    public boolean check() {
        return isEnabled() && getSupportedVersion().equals(getEnabledVersion());
    }

    public static Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(DungeonsXL.class.getClassLoader().getResourceAsStream("dxl.properties"));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return properties;
    }

}
