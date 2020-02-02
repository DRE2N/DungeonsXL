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
import org.bukkit.event.block.BlockBreakEvent;

/**
 * A block that has a special purpose in a game.
 *
 * @author Daniel Saukel
 */
public abstract class GameBlock {

    protected DungeonsXL plugin;

    protected Block block;

    public GameBlock(DungeonsXL plugin, Block block) {
        this.plugin = plugin;
        this.block = block;
    }

    /* Getters and setters */
    /**
     * @return the block
     */
    public Block getBlock() {
        return block;
    }

    /**
     * @param block the block to set
     */
    public void setBlock(Block block) {
        this.block = block;
    }

    /* Abstracts */
    /**
     * Handles what happens when a player breaks the block.
     *
     * @param event the passed Bukkit event
     * @return if the event is cancelled
     */
    public abstract boolean onBreak(BlockBreakEvent event);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{block=" + block + "}";
    }

}
