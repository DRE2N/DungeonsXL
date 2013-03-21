package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameChest;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNChest extends DSign{

	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onDungeonInit(Sign sign, GameWorld gworld) {
		if(sign.getTypeId()==63){
			for(int x=-1;x<=1;x++){
				if(sign.getBlock().getRelative(x, 0, 0).getTypeId()==54){
					new GameChest(sign.getBlock().getRelative(x, 0, 0), gworld);
				}
			}
			
			for(int z=-1;z<=1;z++){
				if(sign.getBlock().getRelative(0, 0, z).getTypeId()==54){
					if(sign.getBlock().getRelative(0, 0, z)!=null){
						new GameChest(sign.getBlock().getRelative(0, 0, z), gworld);
					}
				}
			}
		}
		
		sign.setTypeId(0);
	}

	@Override
	public void onTrigger(Sign sign, GameWorld gworld) {
		// TODO Auto-generated method stub
		
	}
}
