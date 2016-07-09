/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dre2n.dungeonsxl.global;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Daniel Saukel
 */
public class GlobalProtections {

    DungeonsXL plugin = DungeonsXL.getInstance();

    private Set<GlobalProtection> protections = new HashSet<>();

    /**
     * @return the protection which covers this location
     */
    public GlobalProtection getByLocation(Location location) {
        return getByBlock(location.getBlock());
    }

    /**
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
     * @param type
     * All protections which are an instance of it will be returned.
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
     * @param protection
     * the protection type to add
     */
    public void addProtection(GlobalProtection protection) {
        protections.add(protection);
    }

    /**
     * @param protection
     * the protection to remove
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
     * @param file
     * the file to save all protections to
     */
    public void saveAll(File file) {
        saveAll(YamlConfiguration.loadConfiguration(file));
    }

    /**
     * @param config
     * the config to save all protections to
     */
    public void saveAll(FileConfiguration config) {
        config.set("protections", null);
        for (GlobalProtection protection : protections) {
            protection.save(config);
        }

        plugin.getGlobalData().save();
    }

    /**
     * @param type
     * Each type is stored seperately.
     * @param world
     * Each world has its own IDs.
     * @return an unused ID number for a new protection
     */
    public int generateId(Class<? extends GlobalProtection> type, World world) {
        int id = 1;
        for (GlobalProtection protection : protections) {
            if (protection.getClass() == type) {
                id++;
            }
        }
        return id;
    }

    /**
     * @param block
     * the block to check
     */
    public boolean isProtectedBlock(Block block) {
        for (GlobalProtection protection : protections) {
            if (protection.getBlocks().contains(block)) {
                return true;
            }
        }

        return false;
    }

    /* SUBJECT TO CHANGE */
    @Deprecated
    public void loadAll() {
        FileConfiguration data = plugin.getGlobalData().getConfig();

        for (World world : plugin.getServer().getWorlds()) {
            // GameSigns
            if (data.contains("protections.gameSigns." + world.getName())) {
                int id = 0;
                String preString;

                do {
                    id++;
                    preString = "protections.gameSigns." + world.getName() + "." + id + ".";
                    if (data.contains(preString)) {
                        String mapName = data.getString(preString + ".dungeon");
                        int maxGames = data.getInt(preString + ".maxGames");
                        int maxGroupsPerGame = data.getInt(preString + ".maxGroupsPerGame");
                        boolean multiFloor = false;
                        if (data.contains(preString + ".multiFloor")) {
                            multiFloor = data.getBoolean(preString + ".multiFloor");
                        }
                        Block startSign = world.getBlockAt(data.getInt(preString + ".x"), data.getInt(preString + ".y"), data.getInt(preString + ".z"));

                        new GameSign(id, startSign, mapName, maxGames, maxGroupsPerGame, multiFloor);
                    }

                } while (data.contains(preString));
            }

            // GroupSigns
            if (data.contains("protections.groupSigns." + world.getName())) {
                int id = 0;
                String preString;

                do {
                    id++;
                    preString = "protections.groupSigns." + world.getName() + "." + id + ".";
                    if (data.contains(preString)) {
                        String mapName = data.getString(preString + ".dungeon");
                        int maxGroups = data.getInt(preString + ".maxGroups");
                        int maxPlayersPerGroup = data.getInt(preString + ".maxPlayersPerGroup");
                        boolean multiFloor = false;
                        if (data.contains(preString + ".multiFloor")) {
                            multiFloor = data.getBoolean(preString + ".multiFloor");
                        }
                        Block startSign = world.getBlockAt(data.getInt(preString + ".x"), data.getInt(preString + ".y"), data.getInt(preString + ".z"));

                        new GroupSign(id, startSign, mapName, maxGroups, maxPlayersPerGroup, multiFloor);
                    }
                } while (data.contains(preString));
            }

            if (data.contains("protections.leaveSigns." + world.getName())) {

                int id = 0;
                String preString;

                do {
                    id++;
                    preString = "protections.leaveSigns." + world.getName() + "." + id + ".";
                    if (data.contains(preString)) {
                        Block block = world.getBlockAt(data.getInt(preString + ".x"), data.getInt(preString + ".y"), data.getInt(preString + ".z"));
                        if (block.getState() instanceof Sign) {
                            Sign sign = (Sign) block.getState();
                            new LeaveSign(id, sign);
                        }
                    }

                } while (data.contains(preString));
            }

            // DPortals
            if (data.contains("protections.portals." + world.getName())) {
                int id = 0;
                String preString;

                do {
                    id++;
                    preString = "protections.portals." + world.getName() + "." + id + ".";

                    if (data.contains(preString)) {
                        Block block1 = world.getBlockAt(data.getInt(preString + "loc1.x"), data.getInt(preString + "loc1.y"), data.getInt(preString + "loc1.z"));
                        Block block2 = world.getBlockAt(data.getInt(preString + "loc2.x"), data.getInt(preString + "loc2.y"), data.getInt(preString + "loc2.z"));
                        DPortal dPortal = new DPortal(id, block1, block2, true);
                        dPortal.create();
                    }

                } while (data.contains(preString));
            }
        }
    }

}
