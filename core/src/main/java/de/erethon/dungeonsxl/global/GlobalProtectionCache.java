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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.player.DGroup;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Daniel Saukel
 */
public class GlobalProtectionCache {

    private DungeonsXL plugin;

    private Set<GlobalProtection> protections = new HashSet<>();
    private Map<UnloadedProtection, String> unloaded = new HashMap<>();

    public GlobalProtectionCache(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    /**
     * @param location the location to check
     * @return the protection which covers this location
     */
    public GlobalProtection getByLocation(Location location) {
        return getByBlock(location.getBlock());
    }

    /**
     * @param block the block to check
     * @return the protection which covers this block
     */
    public GlobalProtection getByBlock(Block block) {
        for (GlobalProtection protection : protections) {
            if (protection.getBlocks().contains(block)) {
                return protection;
            }
        }

        return null;
    }

    /**
     * @return the protections
     */
    public Set<GlobalProtection> getProtections() {
        return protections;
    }

    /**
     * @return the protections that are known but not loaded yet
     */
    public Map<UnloadedProtection, String> getUnloadedProtections() {
        return unloaded;
    }

    /**
     * @param type All protections which are an instance of it will be returned.
     * @return the protections of the type
     */
    public Set<GlobalProtection> getProtections(Class<? extends GlobalProtection> type) {
        Set<GlobalProtection> protectionsOfType = new HashSet<>();
        for (GlobalProtection protection : protections) {
            if (protection.getClass() == type) {
                protectionsOfType.add(protection);
            }
        }
        return protectionsOfType;
    }

    /**
     * @param protection the protection type to add
     */
    public void addProtection(GlobalProtection protection) {
        protections.add(protection);
    }

    /**
     * @param protection the protection to remove
     */
    public void removeProtection(GlobalProtection protection) {
        protections.remove(protection);
    }

    /**
     * Save all protections to the default file
     */
    public void saveAll() {
        saveAll(plugin.getGlobalData().getConfig());
    }

    /**
     * @param file the file to save all protections to
     */
    public void saveAll(File file) {
        saveAll(YamlConfiguration.loadConfiguration(file));
    }

    /**
     * @param config the config to save all protections to
     */
    public void saveAll(FileConfiguration config) {
        config.set("protections", null);
        for (GlobalProtection protection : protections) {
            protection.save(config);
        }

        plugin.getGlobalData().save();
    }

    /**
     * @param type  Each type is stored seperately.
     * @param world Each world has its own IDs.
     * @return an unused ID number for a new protection
     */
    public int generateId(Class<? extends GlobalProtection> type, World world) {
        int id = 1;
        for (GlobalProtection protection : protections) {
            if (protection.getClass() == type && id <= protection.getId()) {
                id = protection.getId() + 1;
            }
        }
        return id;
    }

    /**
     * @param block the block to check
     * @return if the block is protected by a GlobalProtection
     */
    public boolean isProtectedBlock(Block block) {
        for (GlobalProtection protection : protections) {
            if (protection.getBlocks().contains(block)) {
                return true;
            }
        }

        return false;
    }

    public void updateGroupSigns(DGroup dGroupSearch) {
        for (GlobalProtection protection : getProtections(GroupSign.class)) {
            GroupSign groupSign = (GroupSign) protection;
            if (dGroupSearch != null && groupSign.getGroup() == dGroupSearch) {
                if (dGroupSearch.isEmpty()) {
                    groupSign.setGroup(null);
                }
                groupSign.update();
            }
        }
    }

}
