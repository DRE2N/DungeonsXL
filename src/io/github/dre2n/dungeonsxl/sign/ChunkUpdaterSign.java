package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Sign;

public class ChunkUpdaterSign extends DSign {
	
	private DSignType type = DSignTypeDefault.CHUNK_UPDATER;
	
	public ChunkUpdaterSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@Override
	public void onInit() {
		String lines[] = getSign().getLines();
		Chunk chunk = getGameWorld().getWorld().getChunkAt(getSign().getBlock());
		
		if ( !lines[1].equals("")) {
			Integer radius = IntegerUtil.parseInt(lines[1]);
			for (int x = -radius; x < radius; x++) {
				for (int z = -radius; z < radius; z++) {
					Chunk chunk1 = getGameWorld().getWorld().getChunkAt(chunk.getX() - x, chunk.getZ() - z);
					chunk1.load();
					getGameWorld().getLoadedChunks().add(chunk1);
				}
			}
			
		} else {
			chunk.load();
			getGameWorld().getLoadedChunks().add(chunk);
		}
		
		getSign().getBlock().setType(Material.AIR);
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
