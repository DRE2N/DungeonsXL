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
package de.erethon.dungeonsxl.adapter.block;

import de.erethon.dungeonsxl.api.player.PlayerGroup.Color;
import org.bukkit.Axis;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Bed;

/**
 * @author Daniel Saukel
 */
public class BlockAdapterBlockData implements BlockAdapter {

    @Override
    public boolean isBedHead(Block block) {
        if (!(block.getBlockData() instanceof Bed)) {
            throw new IllegalArgumentException("Block is not Bed");
        }
        return ((Bed) block.getBlockData()).getPart() == Bed.Part.HEAD;
    }

    @Override
    public void openDoor(Block block) {
        if (!(block.getBlockData() instanceof Openable)) {
            throw new IllegalArgumentException("Block is not Openable");
        }
        Openable data = ((Openable) block.getBlockData());
        data.setOpen(true);
        block.setBlockData(data);
    }

    @Override
    public void setBlockWoolColor(Block block, Color color) {
        block.setType(color.getWoolMaterial().getMaterial());
    }

    @Override
    public BlockFace getFacing(Block block) {
        if (block.getBlockData() instanceof Directional) {
            return ((Directional) block.getBlockData()).getFacing();
        } else if (block.getBlockData() instanceof Rotatable) {
            return ((Rotatable) block.getBlockData()).getRotation();
        } else {
            throw new IllegalArgumentException("Block is not Directional or Rotatable");
        }
    }

    @Override
    public void setFacing(Block block, BlockFace facing) {
        BlockData data = block.getBlockData();
        if (data instanceof Directional) {
            ((Directional) data).setFacing(facing);
        } else if (data instanceof Rotatable) {
            ((Rotatable) data).setRotation(facing);
        } else {
            throw new IllegalArgumentException("Block is not Directional or Rotatable");
        }
        block.setBlockData(data, false);
    }

    @Override
    public void setAxis(Block block, boolean z) {
        if (!(block.getBlockData() instanceof Orientable)) {
            throw new IllegalArgumentException("Block is not Orientable");
        }
        Orientable data = (Orientable) block.getBlockData();
        data.setAxis(z ? Axis.Z : Axis.X);
    }

}
