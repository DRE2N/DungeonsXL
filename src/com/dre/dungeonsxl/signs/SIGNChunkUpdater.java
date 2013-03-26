package com.dre.dungeonsxl.signs;

import org.bukkit.Chunk;
import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNChunkUpdater extends DSign{
	
	public static String name = "ChunkUpdater";
	public static String buildPermissions = "dxl.sign.chunkupdater";
	public static boolean onDungeonInit = true;
	
	public SIGNChunkUpdater(Sign sign, GameWorld gworld) {
		super(sign, gworld);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onInit() {
		String lines[] = sign.getLines();
		Chunk chunk = gworld.world.getChunkAt(sign.getBlock());
		if(!lines[1].equals("")){
			Integer radius = p.parseInt(lines[1]);
			for(int x = -radius; x<radius; x++){
				for(int z = -radius; z<radius; z++){
					Chunk chunk1 = gworld.world.getChunkAt(chunk.getX()-x,chunk.getZ()-z);
					chunk1.load();
					gworld.loadedChunks.add(chunk1);
				}
			}
		} else {
			chunk.load();
			gworld.loadedChunks.add(chunk);
		}
		sign.setTypeId(0);
	}
}
