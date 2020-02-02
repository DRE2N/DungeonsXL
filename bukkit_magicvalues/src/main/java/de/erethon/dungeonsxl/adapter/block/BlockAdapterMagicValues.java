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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Bed;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

/**
 * @author Daniel Saukel
 */
public class BlockAdapterMagicValues implements BlockAdapter {

    @Override
    public boolean isBedHead(Block block) {
        MaterialData data = block.getState().getData();
        if (!(data instanceof Bed)) {
            throw new IllegalArgumentException("Block is not Bed");
        }
        return ((Bed) data).isHeadOfBed();
    }

    @Override
    public void openDoor(Block block) {
        block.setData((byte) (block.getData() + 4));
    }

    @Override
    public void setBlockWoolColor(Block block, Color color) {
        block.setTypeIdAndData(Material.WOOL.getId(), color.getDyeColor().getWoolData(), false);
    }

    @Override
    public BlockFace getFacing(Block block) {
        MaterialData data = block.getState().getData();
        if (!(data instanceof Directional)) {
            throw new IllegalArgumentException("Block is not Directional");
        }
        return ((Directional) data).getFacing();
    }

    @Override
    public void setFacing(Block block, BlockFace facing) {
        BlockState state = block.getState();
        MaterialData data = state.getData();
        if (!(data instanceof Directional)) {
            throw new IllegalArgumentException("Block is not Directional");
        }
        ((Directional) data).setFacingDirection(facing);
        state.setData(data);
        state.update();
    }

    @Override
    public void setAxis(Block block, boolean z) {
        block.setData(z ? (byte) 2 : 1);
    }

}
