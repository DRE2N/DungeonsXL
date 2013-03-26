package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameMessage;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNMsg extends DSign{
	
	public static String name = "Msg";
	public static String buildPermissions = "dxl.sign.msg";
	public static boolean onDungeonInit = false;
	
	public SIGNMsg(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}
	
	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onInit() {
		String lines[] = sign.getLines();
		
		if(lines[1]!=""&&lines[2]!=""){
			String msg = gworld.config.getMsg(p.parseInt(lines[1]),true);
			if(msg!=null){
				gworld.messages.add(new GameMessage(sign.getBlock().getLocation(), msg,p.parseInt(lines[2]), false));
				sign.setTypeId(0);
			}
		}
	}

	@Override
	public void onTrigger() {
		// TODO Auto-generated method stub
		
	}
}
