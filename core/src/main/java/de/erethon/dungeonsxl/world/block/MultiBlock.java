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

import org.bukkit.block.Block;

/**
 * In some cases, a "game block" might actually be a structure with multiple blocks, like a bed or a door with two halfs. These GameBlocks implement MultiBlock.
 *
 * @author Daniel Saukel
 */
public interface MultiBlock {

    Block getAttachedBlock();

}
