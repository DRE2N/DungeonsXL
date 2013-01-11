package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DPlayer;

public class CMDChat extends DCommand{

	public CMDChat(){
		this.command="chat";
		this.args=0;
		this.help=p.language.get("help_cmd_chat");//"/dxl chat - Ändert den Chat-Modus";
	}
	
	
	@Override
	public void onExecute(String[] args, Player player) {
		DPlayer dplayer=DPlayer.get(player);
		if(dplayer!=null){
			if(dplayer.isInWorldChat) {
				dplayer.isInWorldChat=false;
				p.msg(player,p.language.get("cmd_chat_normalchat"));
			}else{
				dplayer.isInWorldChat=true;
				p.msg(player,p.language.get("cmd_chat_dungeonchat"));
			}
		}else{
			p.msg(player,p.language.get("cmd_chat_error1"));
		}
	}
	
}
