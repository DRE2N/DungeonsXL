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

import de.erethon.caliburn.item.ExItem;
import de.erethon.commons.misc.BlockUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class PlaceableBlock extends GameBlock {

    // Variables
    private Set<ExItem> materials = new HashSet<>();
    private Set<BlockFace> faces = new HashSet<>();

    public PlaceableBlock(DungeonsXL plugin, Block block, String ids, String directions) {
        super(plugin, block);

        for (String id : ids.split(",")) {
            ExItem item = plugin.getCaliburn().getExItem(id);
            if (item != null) {
                materials.add(item);
            }
        }

        faces.add(BlockFace.SELF);
        for (String direction : directions.split(",")) {
            faces.add(BlockUtil.lettersToBlockFace(direction));
        }
    }

    /* Actions */
    @Override
    public boolean onBreak(BlockBreakEvent event) {
        return false;
    }

    public boolean canPlace(Block toPlace, ExItem material) {
        return faces.contains(toPlace.getFace(block)) && (materials.isEmpty() || materials.contains(material));
    }

    public static boolean canBuildHere(Block block, BlockFace blockFace, ExItem material, DGameWorld gameWorld) {
        for (PlaceableBlock gamePlaceableBlock : gameWorld.getPlaceableBlocks()) {
            if (gamePlaceableBlock.canPlace(block, material)) {
                return true;
            }
        }
        return false;
    }

}
