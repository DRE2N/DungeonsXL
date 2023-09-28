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
package de.erethon.dungeonsxl.util;

import de.erethon.bedrock.compatibility.Version;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

/**
 * @author Daniel Saukel
 */
public class BlockUtilCompat {

    /**
     * Returns the block the given block is attached to.
     *
     * @param block the block to check
     * @return the attached block
     */
    public static Block getAttachedBlock(Block block) {
        if (Version.isAtLeast(Version.MC1_13)) {
            if (block.getBlockData() instanceof Directional) {
                Directional data = (Directional) block.getBlockData();
                if (data.getFaces().size() == 4) {
                    return block.getRelative(data.getFacing().getOppositeFace());
                }
            }
            return block.getRelative(BlockFace.DOWN);

        } else {
            MaterialData meta = block.getState().getData();
            BlockFace blockFace = BlockFace.DOWN;
            if (meta instanceof Attachable) {
                blockFace = ((Attachable) meta).getAttachedFace();
            }
            return block.getRelative(blockFace);
        }
    }
}
