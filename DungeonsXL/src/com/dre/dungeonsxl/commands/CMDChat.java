package com.dre.dungeonsxl.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DPlayer;

public class CMDChat extends DCommand{

	public CMDChat(){
		this.command="chat";
		this.args=0;
		this.help="/dxl chat - Ändert den Chat-Modus";
	}
	
	
	@Override
	public void onExecute(String[] args, Player player) {
		DPlayer dplayer=DPlayer.get(player);
		if(dplayer!=null){
			if(dplayer.isInWorldChat) {
				dplayer.isInWorldChat=false;
				p.msg(player,ChatColor.YELLOW+"Du bist nun im öffentlichen Chat");
			}else{
				dplayer.isInWorldChat=true;
				p.msg(player,ChatColor.YELLOW+"Du bist nun im Dungeon-Chat");
			}
		}else{
			p.msg(player,ChatColor.RED+"Du bist in keinem Dungeon!");
		}
	}
	
}
