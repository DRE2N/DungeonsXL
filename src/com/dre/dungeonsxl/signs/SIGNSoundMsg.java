package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNSoundMsg extends DSign{
	
	public static String name = "SoundMsg";
	public static String buildPermissions = "dxl.sign.soundmsg";
	public static boolean onDungeonInit = false;
	
	//Variables
	private String msg;
	
	public SIGNSoundMsg(Sign sign, GameWorld gworld) {
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
		if(P.p.isSpoutEnabled){
			for(Player player : gworld.world.getPlayers()){
				SpoutPlayer sPlayer = Spout.getServer().getPlayer(player.getName());
				if(sPlayer.isSpoutCraftEnabled()){
					SpoutManager.getSoundManager().playCustomMusic(P.p, sPlayer, this.msg, false, this.sign.getLocation());
				}
			}
		}
	}
}
