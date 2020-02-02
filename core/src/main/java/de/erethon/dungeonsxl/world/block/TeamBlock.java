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
import de.erethon.dungeonsxl.player.DGroup;
import org.bukkit.block.Block;

/**
 * @author Daniel Saukel
 */
public abstract class TeamBlock extends GameBlock {

    protected DGroup owner;

    public TeamBlock(DungeonsXL plugin, Block block, DGroup owner) {
        super(plugin, block);
        this.owner = owner;
    }

    /* Getters and setters */
    /**
     * @return the group that owns the flag
     */
    public DGroup getOwner() {
        return owner;
    }

    /**
     * @param owner the owner group to set
     */
    public void setOwner(DGroup owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{block=" + block + "; owner=" + owner + "}";
    }

}
