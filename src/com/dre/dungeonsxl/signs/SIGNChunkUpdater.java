package com.dre.dungeonsxl.signs;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameWorld;

public class SIGNChunkUpdater extends DSign {

	public static String name = "ChunkUpdater";
	public String buildPermissions = "dxl.sign.chunkupdater";
	public boolean onDungeonInit = true;

	public SIGNChunkUpdater(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public void onInit() {
		String lines[] = sign.getLines();
		Chunk chunk = gworld.world.getChunkAt(sign.getBlock());
		if (!lines[1].equals("")) {
			Integer radius = p.parseInt(lines[1]);
			for (int x = -radius; x < radius; x++) {
				for (int z = -radius; z < radius; z++) {
					Chunk chunk1 = gworld.world.getChunkAt(chunk.getX() - x, chunk.getZ() - z);
					chunk1.load();
					gworld.loadedChunks.add(chunk1);
				}
			}
		} else {
			chunk.load();
			gworld.loadedChunks.add(chunk);
		}
		sign.getBlock().setType(Material.AIR);
	}

	@Override
	public String getPermissions() {
		return buildPermissions;
	}

	@Override
	public boolean isOnDungeonInit() {
		return onDungeonInit;
	}
}
