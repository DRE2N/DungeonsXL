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

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.misc.BlockUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.util.LWCUtil;
import de.erethon.dungeonsxl.world.DResourceWorld;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.Attachable;

/**
 * @author Daniel Saukel
 */
public class JoinSign extends GlobalProtection {

    protected Dungeon dungeon;
    protected int maxElements;
    protected int startIfElementsAtLeast = -1;
    protected Block startSign;
    protected int verticalSigns;
    protected Set<Block> blocks;
    protected boolean loadedWorld;

    protected JoinSign(DungeonsXL plugin, int id, Block startSign, String identifier, int maxElements, int startIfElementsAtLeast) {
        super(plugin, startSign.getWorld(), id);

        this.startSign = startSign;
        dungeon = plugin.getDungeonCache().getByName(identifier);
        if (dungeon == null) {
            DResourceWorld resource = plugin.getDWorldCache().getResourceByName(identifier);
            if (resource != null) {
                dungeon = new Dungeon(plugin, resource);
            }
        }

        verticalSigns = (int) Math.ceil((float) (1 + maxElements) / 4);

        this.maxElements = maxElements;
        if (startIfElementsAtLeast > 0 && startIfElementsAtLeast <= maxElements) {
            this.startIfElementsAtLeast = startIfElementsAtLeast;
        }

        update();
    }

    protected JoinSign(DungeonsXL plugin, World world, int id, ConfigurationSection config) {
        super(plugin, world, id);

        startSign = world.getBlockAt(config.getInt("x"), config.getInt("y"), config.getInt("z"));
        String identifier = config.getString("dungeon");
        dungeon = plugin.getDungeonCache().getByName(identifier);
        if (dungeon == null) {
            DResourceWorld resource = plugin.getDWorldCache().getResourceByName(identifier);
            if (resource != null) {
                dungeon = new Dungeon(plugin, resource);
            }
        }

        // LEGACY
        if (config.contains("maxElements")) {
            maxElements = config.getInt("maxElements");
        } else if (config.contains("maxGroupsPerGame")) {
            maxElements = config.getInt("maxGroupsPerGame");
        } else if (config.contains("maxPlayersPerGroup")) {
            maxElements = config.getInt("maxPlayersPerGroup");
        }

        verticalSigns = (int) Math.ceil((float) (1 + maxElements) / 4);

        if (startIfElementsAtLeast > 0 && startIfElementsAtLeast <= maxElements) {
            startIfElementsAtLeast = config.getInt("startIfElementsAtLeast");
        }

        update();
    }

    /**
     * @return the dungeon
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * @param dungeon the dungeon to set
     */
    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    /**
     * @return the maximum element count per sign
     */
    public int getMaxElements() {
        return maxElements;
    }

    /**
     * @param amount the maximum element count per sign
     */
    public void setMaxElements(int amount) {
        maxElements = amount;
    }

    /**
     * Returns the minimum amount of elements required to start the dungeon countdown
     *
     * @return the minimum amount of elements required to start the dungeon countdown;<br>
     * -1 if the dungeon is not instantiated only through the sign.
     */
    public int getStartIfElementsAtLeastAmount() {
        return startIfElementsAtLeast;
    }

    /**
     * Sets the minimum amount of elements required to start the dungeon countdown
     *
     * @param amount the amount to set
     */
    public void setStartIfElementsAtLeastAmount(int amount) {
        if ((amount > 0 || amount == -1) && amount <= maxElements) {
            startIfElementsAtLeast = amount;
        } else {
            throw new IllegalArgumentException("startIfElementsAtLeastAmount is < 0 or < maxElements");
        }
    }

    @Override
    public Set<Block> getBlocks() {
        if (blocks == null) {
            blocks = new HashSet<>();

            HashSet<Block> toAdd = new HashSet<>();
            int y = -1 * verticalSigns;
            while (y != 1) {
                blocks.add(startSign.getRelative(0, y, 0));
                y++;
            }

            for (Block block : blocks) {
                int i = verticalSigns;
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

    /**
     * Clears signs
     */
    public void update() {
        int y = -1 * verticalSigns;
        while (startSign.getRelative(0, y + 1, 0).getState() instanceof Sign && y != 0) {
            Sign subsign = (Sign) startSign.getRelative(0, y + 1, 0).getState();
            subsign.setLine(0, "");
            subsign.setLine(1, "");
            subsign.setLine(2, "");
            subsign.setLine(3, "");
            subsign.update();
            y++;
        }
    }

    @Override
    public void save(FileConfiguration config) {
        String preString = "protections.groupSigns." + getWorld().getName() + "." + getId();

        config.set(preString + ".x", startSign.getX());
        config.set(preString + ".y", startSign.getY());
        config.set(preString + ".z", startSign.getZ());
        if (dungeon != null) {
            config.set(preString + ".dungeon", dungeon.getName());
        }
        config.set(preString + ".maxElements", maxElements);
        if (startIfElementsAtLeast != -1) {
            config.set(preString + ".startIfElementsAtLeast", startIfElementsAtLeast);
        }
    }

    protected static void onCreation(DungeonsXL plugin, Block startSign, String identifier, int maxElements, int startIfElementsAtLeast) {
        // TODO: Replace as soon as versions older than 1.13 are dropped

        World world = startSign.getWorld();
        BlockFace facing = ((Attachable) startSign.getState().getData()).getAttachedFace().getOppositeFace();
        //BlockFace facing = ((Directional) startSign.getBlockData()).getFacing().getOppositeFace();
        int x = startSign.getX(), y = startSign.getY(), z = startSign.getZ();

        int verticalSigns = (int) Math.ceil((float) (1 + maxElements) / 4);
        while (verticalSigns > 1) {
            Block block = world.getBlockAt(x, y - verticalSigns + 1, z);
            block.setType(VanillaItem.WALL_SIGN.getMaterial(), false);
            BlockState state = block.getState();
            org.bukkit.material.Sign signData = (org.bukkit.material.Sign) state.getData();
            signData.setFacingDirection(facing);
            state.setData(signData);
            state.update(true, false);
            // Directional directional = (Directional) block.getBlockData();
            // directional.setFacing(facing);
            // block.setBlockData(directional, false);

            verticalSigns--;
        }

        LWCUtil.removeProtection(startSign);
    }

}
