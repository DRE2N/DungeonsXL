/*
 * Copyright (C) 2012-2023 Frank Baumann
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
package de.erethon.dungeonsxl.adapter.block;

import de.erethon.dungeonsxl.api.player.PlayerGroup.Color;
import org.bukkit.Axis;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.type.Bed;

/**
 * Modern Paper API implementation for block adapter.
 * Updated for Paper 1.21.8 - no longer uses magic values or deprecated MaterialData.
 *
 * @author Daniel Saukel
 */
public class BlockAdapterMagicValues implements BlockAdapter {

    @Override
    public boolean isBedHead(Block block) {
        BlockData data = block.getBlockData();
        if (!(data instanceof Bed)) {
            throw new IllegalArgumentException("Block is not Bed");
        }
        return ((Bed) data).getPart() == Bed.Part.HEAD;
    }

    @Override
    public void openDoor(Block block) {
        BlockData data = block.getBlockData();
        if (!(data instanceof Openable)) {
            throw new IllegalArgumentException("Block is not Openable");
        }
        ((Openable) data).setOpen(true);
        block.setBlockData(data);
    }

    @Override
    public void closeDoor(Block block) {
        BlockData data = block.getBlockData();
        if (!(data instanceof Openable)) {
            throw new IllegalArgumentException("Block is not Openable");
        }
        ((Openable) data).setOpen(false);
        block.setBlockData(data);
    }

    @Override
    public void setBlockWoolColor(Block block, Color color) {
        DyeColor dyeColor = color.getDyeColor();
        Material woolMaterial = Material.valueOf(dyeColor.name() + "_WOOL");
        block.setType(woolMaterial);
    }

    @Override
    public BlockFace getFacing(Block block) {
        BlockData data = block.getBlockData();
        if (!(data instanceof Directional)) {
            throw new IllegalArgumentException("Block is not Directional");
        }
        return ((Directional) data).getFacing();
    }

    @Override
    public void setFacing(Block block, BlockFace facing) {
        BlockData data = block.getBlockData();
        if (!(data instanceof Directional)) {
            throw new IllegalArgumentException("Block is not Directional");
        }
        ((Directional) data).setFacing(facing);
        block.setBlockData(data);
    }

    @Override
    public void setAxis(Block block, boolean z) {
        BlockData data = block.getBlockData();
        if (!(data instanceof Orientable)) {
            throw new IllegalArgumentException("Block is not Orientable");
        }
        ((Orientable) data).setAxis(z ? Axis.Z : Axis.X);
        block.setBlockData(data);
    }
}
