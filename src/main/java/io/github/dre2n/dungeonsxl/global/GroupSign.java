/*
 * Copyright (C) 2012-2016 Frank Baumann
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
package io.github.dre2n.dungeonsxl.global;

import io.github.dre2n.commons.util.BlockUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.player.DGroup;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class GroupSign extends GlobalProtection {

    // Sign Labels
    public static final String IS_PLAYING = ChatColor.DARK_RED + "Is Playing";
    public static final String FULL = ChatColor.DARK_RED + "Full";
    public static final String JOIN_GROUP = ChatColor.DARK_GREEN + "Join Group";
    public static final String NEW_GROUP = ChatColor.DARK_GREEN + "New Group";

    // Variables
    private DGroup[] dGroups;
    private boolean multiFloor;
    private String dungeonName;
    private String mapName;
    private int maxPlayersPerGroup;
    private Block startSign;
    private int directionX = 0, directionZ = 0;
    private int verticalSigns;
    private Set<Block> blocks;

    public GroupSign(int id, Block startSign, String identifier, int maxGroups, int maxPlayersPerGroup, boolean multiFloor) {
        super(startSign.getWorld(), id);

        this.startSign = startSign;
        dGroups = new DGroup[maxGroups];
        this.setMultiFloor(multiFloor);
        if (multiFloor) {
            dungeonName = identifier;
            Dungeon dungeon = plugin.getDungeons().getDungeon(identifier);
            if (dungeon != null) {
                mapName = dungeon.getConfig().getStartFloor();
            } else {
                mapName = "invalid";
            }
        } else {
            mapName = identifier;
        }
        this.maxPlayersPerGroup = maxPlayersPerGroup;
        verticalSigns = (int) Math.ceil((float) (1 + maxPlayersPerGroup) / 4);

        int[] direction = getDirection(this.startSign.getData());
        directionX = direction[0];
        directionZ = direction[1];

        update();
    }

    /**
     * @return the dGroups
     */
    public DGroup[] getDGroups() {
        return dGroups;
    }

    /**
     * @param dGroups
     * the dGroups to set
     */
    public void setDGroups(DGroup[] dGroups) {
        this.dGroups = dGroups;
    }

    /**
     * @return the multiFloor
     */
    public boolean isMultiFloor() {
        return multiFloor;
    }

    /**
     * @param multiFloor
     * the multiFloor to set
     */
    public void setMultiFloor(boolean multiFloor) {
        this.multiFloor = multiFloor;
    }

    /**
     * @return the dungeonName
     */
    public String getDungeonName() {
        return dungeonName;
    }

    /**
     * @param dungeonName
     * the dungeonName to set
     */
    public void setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
    }

    /**
     * @return the mapName
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @param mapName
     * the mapName to set
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * @return the maximum player count per group
     */
    public int getMaxPlayersPerGroup() {
        return maxPlayersPerGroup;
    }

    /**
     * @param maxPlayersPerGroup
     * the maximum player count per group to set
     */
    public void setMaxPlayerPerGroup(int maxPlayersPerGroup) {
        this.maxPlayersPerGroup = maxPlayersPerGroup;
    }

    /**
     * Update this group sign to show the group(s) correctly.
     */
    public void update() {
        int i = 0;
        for (DGroup dGroup : dGroups) {
            if (!(startSign.getRelative(i * directionX, 0, i * directionZ).getState() instanceof Sign)) {
                i++;
                continue;
            }

            Sign sign = (Sign) startSign.getRelative(i * directionX, 0, i * directionZ).getState();

            // Reset Signs
            sign.setLine(0, "");
            sign.setLine(1, "");
            sign.setLine(2, "");
            sign.setLine(3, "");

            int yy = -1;
            while (sign.getBlock().getRelative(0, yy, 0).getState() instanceof Sign) {
                Sign subsign = (Sign) sign.getBlock().getRelative(0, yy, 0).getState();
                subsign.setLine(0, "");
                subsign.setLine(1, "");
                subsign.setLine(2, "");
                subsign.setLine(3, "");
                subsign.update();
                yy--;
            }

            // Set Signs
            if (dGroup != null) {
                if (dGroup.isPlaying()) {
                    sign.setLine(0, IS_PLAYING);

                } else if (dGroup.getPlayers().size() >= maxPlayersPerGroup) {
                    sign.setLine(0, FULL);

                } else {
                    sign.setLine(0, JOIN_GROUP);
                }

                int j = 1;
                Sign rowSign = sign;

                for (Player player : dGroup.getPlayers()) {
                    if (j > 3) {
                        j = 0;
                        rowSign = (Sign) sign.getBlock().getRelative(0, -1, 0).getState();
                    }

                    if (rowSign != null) {
                        rowSign.setLine(j, player.getName());
                    }

                    j++;
                    rowSign.update();
                }

            } else {
                sign.setLine(0, NEW_GROUP);
            }

            sign.update();

            i++;
        }
    }

    @Override
    public Set<Block> getBlocks() {
        if (blocks == null) {
            blocks = new HashSet<>();

            int i = dGroups.length;
            do {
                i--;

                blocks.add(startSign.getRelative(i * directionX, 0, i * directionZ));

            } while (i >= 0);

            HashSet<Block> toAdd = new HashSet<>();
            for (Block block : blocks) {
                i = verticalSigns;
                do {
                    i--;

                    Block beneath = block.getLocation().add(0, -1 * i, 0).getBlock();
                    toAdd.add(beneath);
                    toAdd.add(BlockUtil.getAttachedBlock(beneath));

                } while (i >= 0);
            }
            blocks.addAll(toAdd);
        }

        return blocks;
    }

    @Override
    public void save(FileConfiguration config) {
        String preString = "protections.groupSigns." + getWorld().getName() + "." + getId();

        config.set(preString + ".x", startSign.getX());
        config.set(preString + ".y", startSign.getY());
        config.set(preString + ".z", startSign.getZ());

        if (isMultiFloor()) {
            config.set(preString + ".dungeon", dungeonName);

        } else {
            config.set(preString + ".dungeon", mapName);
        }

        config.set(preString + ".maxGroups", dGroups.length);
        config.set(preString + ".maxPlayersPerGroup", maxPlayersPerGroup);
        config.set(preString + ".multiFloor", isMultiFloor());
    }

    /* Statics */
    /**
     * @param block
     * a block which is protected by the returned GroupSign
     */
    public static GroupSign getByBlock(Block block) {
        if (!(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)) {
            return null;
        }

        int x = block.getX(), y = block.getY(), z = block.getZ();
        for (GlobalProtection protection : protections.getProtections(GroupSign.class)) {
            GroupSign groupSign = (GroupSign) protection;

            int sx1 = groupSign.startSign.getX(), sy1 = groupSign.startSign.getY(), sz1 = groupSign.startSign.getZ();
            int sx2 = sx1 + (groupSign.dGroups.length - 1) * groupSign.directionX;
            int sy2 = sy1 - groupSign.verticalSigns + 1;
            int sz2 = sz1 + (groupSign.dGroups.length - 1) * groupSign.directionZ;

            if (sx1 > sx2) {
                if (x < sx2 || x > sx1) {
                    continue;
                }

            } else if (sx1 < sx2) {
                if (x > sx2 || x < sx1) {
                    continue;
                }

            } else if (x != sx1) {
                continue;
            }

            if (sy1 > sy2) {
                if (y < sy2 || y > sy1) {
                    continue;
                }

            } else if (y != sy1) {
                continue;
            }

            if (sz1 > sz2) {
                if (z < sz2 || z > sz1) {
                    continue;
                }

            } else if (sz1 < sz2) {
                if (z > sz2 || z < sz1) {
                    continue;
                }

            } else if (z != sz1) {
                continue;
            }

            return groupSign;
        }

        return null;
    }

    /* SUBJECT TO CHANGE */
    @Deprecated
    public static GroupSign tryToCreate(Block startSign, String mapName, int maxGroups, int maxPlayersPerGroup, boolean multiFloor) {
        World world = startSign.getWorld();
        int direction = startSign.getData();
        int x = startSign.getX(), y = startSign.getY(), z = startSign.getZ();

        int verticalSigns = (int) Math.ceil((float) (1 + maxPlayersPerGroup) / 4);

        CopyOnWriteArrayList<Block> changeBlocks = new CopyOnWriteArrayList<>();

        int xx, yy, zz;
        switch (direction) {
            case 2:
                zz = z;

                for (yy = y; yy > y - verticalSigns; yy--) {
                    for (xx = x; xx > x - maxGroups; xx--) {
                        Block block = world.getBlockAt(xx, yy, zz);

                        if (block.getType() != Material.AIR && block.getType() != Material.WALL_SIGN) {
                            return null;
                        }

                        if (block.getRelative(0, 0, 1).getType() == Material.AIR) {
                            return null;
                        }

                        changeBlocks.add(block);
                    }
                }

                break;

            case 3:
                zz = z;
                for (yy = y; yy > y - verticalSigns; yy--) {
                    for (xx = x; xx < x + maxGroups; xx++) {

                        Block block = world.getBlockAt(xx, yy, zz);
                        if (block.getType() != Material.AIR && block.getType() != Material.WALL_SIGN) {
                            return null;
                        }

                        if (block.getRelative(0, 0, -1).getType() == Material.AIR) {
                            return null;
                        }

                        changeBlocks.add(block);
                    }
                }

                break;

            case 4:
                xx = x;
                for (yy = y; yy > y - verticalSigns; yy--) {
                    for (zz = z; zz < z + maxGroups; zz++) {

                        Block block = world.getBlockAt(xx, yy, zz);
                        if (block.getType() != Material.AIR && block.getType() != Material.WALL_SIGN) {
                            return null;
                        }

                        if (block.getRelative(1, 0, 0).getType() == Material.AIR) {
                            return null;
                        }

                        changeBlocks.add(block);
                    }
                }
                break;

            case 5:
                xx = x;
                for (yy = y; yy > y - verticalSigns; yy--) {
                    for (zz = z; zz > z - maxGroups; zz--) {

                        Block block = world.getBlockAt(xx, yy, zz);
                        if (block.getType() != Material.AIR && block.getType() != Material.WALL_SIGN) {
                            return null;
                        }

                        if (block.getRelative(-1, 0, 0).getType() == Material.AIR) {
                            return null;
                        }

                        changeBlocks.add(block);
                    }
                }

                break;
        }

        for (Block block : changeBlocks) {
            block.setTypeIdAndData(68, startSign.getData(), true);
        }

        GroupSign sign = new GroupSign(protections.generateId(GroupSign.class, world), startSign, mapName, maxGroups, maxPlayersPerGroup, multiFloor);

        return sign;
    }

    @Deprecated
    public static boolean playerInteract(Block block, Player player) {
        int x = block.getX(), y = block.getY(), z = block.getZ();
        GroupSign groupSign = getByBlock(block);

        if (groupSign == null) {
            return false;
        }

        if (DGroup.getByPlayer(player) != null) {
            MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.ERROR_LEAVE_GROUP));
            return true;
        }

        int sx1 = groupSign.startSign.getX(), sy1 = groupSign.startSign.getY(), sz1 = groupSign.startSign.getZ();

        Block topBlock = block.getRelative(0, sy1 - y, 0);

        int column;
        if (groupSign.directionX != 0) {
            column = Math.abs(x - sx1);

        } else {
            column = Math.abs(z - sz1);
        }

        if (!(topBlock.getState() instanceof Sign)) {
            return true;
        }

        Sign topSign = (Sign) topBlock.getState();

        if (topSign.getLine(0).equals(NEW_GROUP)) {
            if (groupSign.isMultiFloor()) {
                groupSign.dGroups[column] = new DGroup(player, groupSign.dungeonName, groupSign.isMultiFloor());

            } else {
                groupSign.dGroups[column] = new DGroup(player, groupSign.mapName, groupSign.isMultiFloor());
            }
            groupSign.update();

        } else if (topSign.getLine(0).equals(JOIN_GROUP)) {
            groupSign.dGroups[column].addPlayer(player);
            groupSign.update();
        }

        return true;
    }

    @Deprecated
    public static void updatePerGroup(DGroup dGroupSearch) {
        for (GlobalProtection protection : protections.getProtections(GroupSign.class)) {
            GroupSign groupSign = (GroupSign) protection;

            int i = 0;
            for (DGroup dGroup : groupSign.dGroups) {
                if (dGroup == null) {
                    continue;
                }

                if (dGroup == dGroupSearch) {
                    if (dGroupSearch.isEmpty()) {
                        groupSign.dGroups[i] = null;
                    }
                    groupSign.update();
                }

                i++;
            }
        }
    }

    @Deprecated
    public static int[] getDirection(byte data) {
        int[] direction = new int[2];

        switch (data) {
            case 2:
                direction[0] = -1;
                break;

            case 3:
                direction[0] = 1;
                break;

            case 4:
                direction[1] = 1;
                break;

            case 5:
                direction[1] = -1;
                break;
        }
        return direction;
    }

}
