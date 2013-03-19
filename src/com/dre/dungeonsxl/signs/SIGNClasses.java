package com.dre.dungeonsxl.signs;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.dre.dungeonsxl.DClass;
import com.dre.dungeonsxl.DGSign;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNClasses extends DSignType{

	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onDungeonInit(Sign sign, GameWorld gworld) {
		if(!gworld.config.isLobbyDisabled()){
			int[] direction=DGSign.getDirection(sign.getBlock().getData());
			int directionX=direction[0];
			int directionZ=direction[1];

			int xx=0,zz=0;
			for(DClass dclass:gworld.config.getClasses()){

				//Check existing signs
				boolean isContinued=true;
				for(Sign isusedsign:gworld.signClass){
					if(dclass.name.equalsIgnoreCase(ChatColor.stripColor(isusedsign.getLine(1)))){
						isContinued=false;
					}
				}

				if(isContinued){
					Block classBlock=sign.getBlock().getRelative(xx,0,zz);

					if(classBlock.getData()==sign.getData().getData()&&classBlock.getTypeId()==68&&(classBlock.getState() instanceof Sign)){
						Sign classSign = (Sign) classBlock.getState();

						classSign.setLine(0, ChatColor.DARK_BLUE+"############");
						classSign.setLine(1, ChatColor.DARK_GREEN+dclass.name);
						classSign.setLine(2, "");
						classSign.setLine(3, ChatColor.DARK_BLUE+"############");
						classSign.update();
						gworld.signClass.add(classSign);
					}else{
						break;
					}
					xx=xx+directionX;
					zz=zz+directionZ;
				}
			}
		} else {
			sign.setTypeId(0);
		}
	}

	@Override
	public void onTrigger(Sign sign, GameWorld gworld) {
		// TODO Auto-generated method stub
		
	}
}
