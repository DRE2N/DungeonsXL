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

import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Daniel Saukel
 */
public class UnloadedProtection<T extends GlobalProtection> {

    private DungeonsXL plugin;
    private GlobalProtectionCache cache;

    private Constructor constructor;
    private String worldName;
    private int id;
    private ConfigurationSection config;

    public UnloadedProtection(DungeonsXL plugin, Class<T> type, String worldName, int id, ConfigurationSection config) {
        this.plugin = plugin;
        try {
            constructor = type.getConstructor(DungeonsXL.class, World.class, int.class, ConfigurationSection.class);
        } catch (NoSuchMethodException | SecurityException exception) {
            // Don't register
            return;
        }
        this.worldName = worldName;
        this.id = id;
        this.config = config;

        cache = plugin.getGlobalProtectionCache();
        cache.getUnloadedProtections().put(this, worldName);
    }

    public T load(World world) {
        if (!world.getName().equals(worldName)) {
            throw new IllegalArgumentException("World mismatch: Expected " + worldName + ", but received " + world);
        }

        T protection = null;
        try {
            protection = (T) constructor.newInstance(plugin, world, id, config);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            MessageUtil.log(plugin, "Could not find or invoke " + constructor);
            exception.printStackTrace();
        }
        if (protection != null) {
            cache.getUnloadedProtections().remove(this);
        }
        return protection;
    }

    @Override
    public String toString() {
        return "UnloadedProtection{type=" + constructor.getDeclaringClass().getName() + "; " + "world=" + worldName + "}";
    }

}
