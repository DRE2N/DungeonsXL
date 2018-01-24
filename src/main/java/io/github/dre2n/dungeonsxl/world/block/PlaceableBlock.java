/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package io.github.dre2n.dungeonsxl.world.block;

import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.dungeonsxl.util.LegacyUtil;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class PlaceableBlock extends GameBlock {

    // Variables
    private Set<Material> materials = new HashSet<>();

    private boolean onTop = false;
    private boolean onBottom = false;
    private boolean onNorth = false;
    private boolean onSouth = false;
    private boolean onEast = false;
    private boolean onWest = false;

    public PlaceableBlock(Block block, String ids, String directions) {
        super(block);

        // Split ids
        if (!ids.isEmpty()) {
            String[] splittedIds = ids.split(",");
            for (String id : splittedIds) {
                Material material = LegacyUtil.getMaterial(NumberUtil.parseInt(id));
                if (material != null) {
                    materials.add(material);
                }
            }
        }

        // Read directions
        if (directions.length() == 6) {
            for (int direction = 0; direction < 6; direction++) {
                boolean positive = String.valueOf(directions.charAt(direction)).equals("x");

                if (!positive) {
                    continue;
                }

                if (direction == 0) {
                    onTop = true;
                }

                if (direction == 1) {
                    onBottom = true;
                }

                if (block.getType() == Material.WALL_SIGN) {
                    int data = block.getData();
                    switch (data) {
                        case 3:
                            if (direction == 2) {
                                onNorth = true;
                            }

                            if (direction == 3) {
                                onEast = true;
                            }

                            if (direction == 4) {
                                onSouth = true;
                            }

                            if (direction == 5) {
                                onWest = true;
                            }

                            break;

                        case 4:
                            if (direction == 5) {
                                onNorth = true;
                            }

                            if (direction == 2) {
                                onEast = true;
                            }

                            if (direction == 3) {
                                onSouth = true;
                            }

                            if (direction == 4) {
                                onWest = true;
                            }

                            break;

                        case 2:
                            if (direction == 4) {
                                onNorth = true;
                            }

                            if (direction == 5) {
                                onEast = true;
                            }

                            if (direction == 2) {
                                onSouth = true;
                            }

                            if (direction == 3) {
                                onWest = true;
                            }

                            break;

                        case 5:
                            if (direction == 3) {
                                onNorth = true;
                            }

                            if (direction == 4) {
                                onEast = true;
                            }

                            if (direction == 5) {
                                onSouth = true;
                            }

                            if (direction == 2) {
                                onWest = true;

                            }
                            break;
                    }

                } else {
                    int data = block.getData();
                    switch (data) {
                        case 0:
                        case 1:
                        case 2:
                        case 15:
                            if (direction == 2) {
                                onNorth = true;
                            }

                            if (direction == 3) {
                                onEast = true;
                            }

                            if (direction == 4) {
                                onSouth = true;
                            }

                            if (direction == 5) {
                                onWest = true;
                            }

                            break;
                        case 4:
                        case 3:
                        case 5:
                        case 6:
                            if (direction == 5) {
                                onNorth = true;
                            }

                            if (direction == 2) {
                                onEast = true;
                            }

                            if (direction == 3) {
                                onSouth = true;
                            }

                            if (direction == 4) {
                                onWest = true;
                            }

                            break;

                        case 8:
                        case 7:
                        case 9:
                        case 10:
                            if (direction == 4) {
                                onNorth = true;
                            }

                            if (direction == 5) {
                                onEast = true;
                            }

                            if (direction == 2) {
                                onSouth = true;
                            }

                            if (direction == 3) {
                                onWest = true;
                            }

                            break;
                        case 12:
                        case 11:
                        case 13:
                        case 14:
                            if (direction == 3) {
                                onNorth = true;
                            }

                            if (direction == 4) {
                                onEast = true;
                            }

                            if (direction == 5) {
                                onSouth = true;
                            }

                            if (direction == 2) {
                                onWest = true;
                            }

                            break;
                    }
                }
            }

        } else {
            onTop = true;
            onBottom = true;
            onNorth = true;
            onEast = true;
            onSouth = true;
            onWest = true;
        }
    }

    /* Actions */
    @Override
    public boolean onBreak(BlockBreakEvent event) {
        return false;
    }

    // Can build
    public static boolean canBuildHere(Block block, BlockFace blockFace, Material mat, DGameWorld gameWorld) {
        for (PlaceableBlock gamePlacableBlock : gameWorld.getPlaceableBlocks()) {
            if (gamePlacableBlock.block.getFace(block) != BlockFace.SELF) {
                continue;
            }

            if (!(gamePlacableBlock.materials.contains(mat) || gamePlacableBlock.materials.isEmpty())) {
                continue;
            }

            if (blockFace == BlockFace.NORTH && gamePlacableBlock.onNorth) {
                return true;
            }

            if (blockFace == BlockFace.SOUTH && gamePlacableBlock.onSouth) {
                return true;
            }

            if (blockFace == BlockFace.EAST && gamePlacableBlock.onEast) {
                return true;
            }

            if (blockFace == BlockFace.WEST && gamePlacableBlock.onWest) {
                return true;
            }

            if (blockFace == BlockFace.UP && gamePlacableBlock.onTop) {
                return true;
            }

            if (blockFace == BlockFace.DOWN && gamePlacableBlock.onBottom) {
                return true;
            }
        }

        return false;
    }

}
