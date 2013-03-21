package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameMessage;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNSoundMsg extends DSign{

	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onDungeonInit(Sign sign, GameWorld gworld) {
		String lines[] = sign.getLines();
		
		if(lines[2]!=""&&lines[3]!=""){
			String msg = gworld.config.getMsg(p.parseInt(lines[2]),true);
			if(msg!=null){
				gworld.messages.add(new GameMessage(sign.getBlock().getLocation(), msg,p.parseInt(lines[3]), true));
				sign.setTypeId(0);
			}
		}
	}

	@Override
	public void onTrigger(Sign sign, GameWorld gworld) {
		// TODO Auto-generated method stub
		
	}
}
