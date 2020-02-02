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
package de.erethon.dungeonsxl.world.block;

import de.erethon.dungeonsxl.DungeonsXL;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * A locked door that may be opened with a trigger.
 *
 * @author Daniel Saukel
 */
public class LockedDoor extends GameBlock implements MultiBlock {

    private Block attachedBlock;

    public LockedDoor(DungeonsXL plugin, Block block) {
        super(plugin, block);
        attachedBlock = getAttachedBlock();
    }

    /* Getters and setters */
    @Override
    public Block getAttachedBlock() {
        if (attachedBlock != null) {
            return attachedBlock;

        } else {
            return block.getRelative(BlockFace.UP);
        }
    }

    /* Actions */
    @Override
    public boolean onBreak(BlockBreakEvent event) {
        return true;
    }

    /**
     * Opens the door.
     */
    public void open() {
        DungeonsXL.BLOCK_ADAPTER.openDoor(block);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{block=" + block + "; attachedBlock=" + attachedBlock + "}";
    }

}
