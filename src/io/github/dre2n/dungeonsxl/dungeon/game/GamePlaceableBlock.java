package io.github.dre2n.dungeonsxl.dungeon.game;

import io.github.dre2n.dungeonsxl.util.IntegerUtil;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class GamePlaceableBlock {
	
	// Variables
	private Block block;
	private Set<Material> mats = new HashSet<Material>();
	
	private boolean onTop = false;
	private boolean onBottom = false;
	private boolean onNorth = false;
	private boolean onSouth = false;
	private boolean onEast = false;
	private boolean onWest = false;
	
	public GamePlaceableBlock(Block block, String ids, String directions) {
		this.block = block;
		
		// Split ids
		if ( !ids.equals("")) {
			String[] splittedIds = ids.split(",");
			for (String id : splittedIds) {
				@SuppressWarnings("deprecation")
                Material mat = Material.getMaterial(IntegerUtil.parseInt(id));
				if (mat != null) {
					mats.add(mat);
				}
			}
		}
		
		// Read directions
		if (directions.length() == 6) {
			for (int direction = 0; direction < 6; direction++) {
				boolean positive = String.valueOf(directions.charAt(direction)).equals("x");
				
				if (positive) {
					if (direction == 0) {
						onTop = true;
					}
					if (direction == 1) {
						onBottom = true;
					}
					
					if (block.getType() == Material.WALL_SIGN) {
						@SuppressWarnings("deprecation")
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
						@SuppressWarnings("deprecation")
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
	
	// Canbuild
	public static boolean canBuildHere(Block block, BlockFace blockFace, Material mat, GameWorld gWorld) {
		for (GamePlaceableBlock gPBlock : gWorld.placeableBlocks) {
			if (gPBlock.block.getFace(block) == BlockFace.SELF) {
				if (gPBlock.mats.contains(mat) || gPBlock.mats.isEmpty()) {
					if (blockFace == BlockFace.NORTH && gPBlock.onNorth) {
						return true;
					}
					if (blockFace == BlockFace.SOUTH && gPBlock.onSouth) {
						return true;
					}
					if (blockFace == BlockFace.EAST && gPBlock.onEast) {
						return true;
					}
					if (blockFace == BlockFace.WEST && gPBlock.onWest) {
						return true;
					}
					if (blockFace == BlockFace.UP && gPBlock.onTop) {
						return true;
					}
					if (blockFace == BlockFace.DOWN && gPBlock.onBottom) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
