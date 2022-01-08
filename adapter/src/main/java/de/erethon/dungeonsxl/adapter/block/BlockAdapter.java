/*
 * Copyright (C) 2012-2022 Frank Baumann
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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * @author Daniel Saukel
 */
public interface BlockAdapter {

    boolean isBedHead(Block block);

    void openDoor(Block block);

    void closeDoor(Block block);

    void setBlockWoolColor(Block block, Color color);

    BlockFace getFacing(Block block);

    void setFacing(Block block, BlockFace facing);

    void setAxis(Block block, boolean z);

}
