package com.dre.dungeonsxl.signs;

import org.bukkit.Chunk;
import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNChunkUpdater extends DSign{

	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onDungeonInit(Sign sign, GameWorld gworld) {
		String lines[] = sign.getLines();
		Chunk chunk = gworld.world.getChunkAt(sign.getBlock());
		if(!lines[2].equals("")){
			Integer radius = p.parseInt(lines[2]);
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

	@Override
	public void onTrigger(Sign sign, GameWorld gworld) {
		// TODO Auto-generated method stub
		
	}
}
