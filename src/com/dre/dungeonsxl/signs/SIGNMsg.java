package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNMsg extends DSign{
	
	public static String name = "Msg";
	public static String buildPermissions = "dxl.sign.msg";
	public static boolean onDungeonInit = false;
	
	//Variables
	private String msg;
	
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
				this.msg = msg;
				sign.setTypeId(0);
			}
		}
	}

	@Override
	public void onTrigger() {
		for(Player player : gworld.world.getPlayers()){
			p.msg(player, msg);
		}
	}
}
