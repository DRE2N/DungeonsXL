package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Sign;

public class ChunkUpdaterSign extends DSign {
	
	public static String name = "ChunkUpdater";
	public String buildPermissions = "dxl.sign.chunkupdater";
	public boolean onDungeonInit = true;
	
	public ChunkUpdaterSign(Sign sign, GameWorld gWorld) {
		super(sign, gWorld);
	}
	
	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		
		return true;
	}
	
	@Override
	public void onInit() {
		String lines[] = getSign().getLines();
		Chunk chunk = getGWorld().world.getChunkAt(getSign().getBlock());
		if ( !lines[1].equals("")) {
			Integer radius = IntegerUtil.parseInt(lines[1]);
			for (int x = -radius; x < radius; x++) {
				for (int z = -radius; z < radius; z++) {
					Chunk chunk1 = getGWorld().world.getChunkAt(chunk.getX() - x, chunk.getZ() - z);
					chunk1.load();
					getGWorld().loadedChunks.add(chunk1);
				}
			}
		} else {
			chunk.load();
			getGWorld().loadedChunks.add(chunk);
		}
		getSign().getBlock().setType(Material.AIR);
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
