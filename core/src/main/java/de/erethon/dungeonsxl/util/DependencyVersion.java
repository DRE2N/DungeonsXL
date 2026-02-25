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

import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Lists compatible plugin versions.
 *
 * @author Daniel Saukel
 */
public enum DependencyVersion {

    XLIB("XLib-Runtime", "7.0-SNAPSHOT"),
    BOSSSHOP("BossShop", "${dependencyVersion.bossshop}"),
    CITIZENS("Citizens", "${dependencyVersion.citizens}"),
    HOLOGRAPHIC_DISPLAYS("HolographicDisplays", "${dependencyVersion.holographicdisplays}"),
    MODERN_LWC("LWC", "2.1.5-09ad392"),
    PARTIES("Parties", "${dependencyVersion.parties}"),
    PLACEHOLDER_API("PlaceholderAPI", "${dependencyVersion.placeholderapi}"),
    VAULT("Vault", "1.7.3"),
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
        return version;
    }

    public boolean isEnabled() {
        return plugin != null;
    }

    public boolean check() {
        return isEnabled() && getSupportedVersion().equals(getEnabledVersion());
    }

}
