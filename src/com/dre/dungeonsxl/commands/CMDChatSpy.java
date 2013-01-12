package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;

public class CMDChatSpy extends DCommand{
	public CMDChatSpy(){
		this.command="chatspy";
		this.args=0;
		this.help=p.language.get("Help_Cmd_Chatspy");
		this.permissions="dxl.chatspy";
	}

	@Override
	public void onExecute(String[] args, Player player) {
		if(p.chatSpyer.contains(player)){
			p.chatSpyer.remove(player);
			p.msg(player, p.language.get("Cmd_Chatspy_Stopped"));
		}

		else{
			p.chatSpyer.add(player);
			p.msg(player, p.language.get("Cmd_Chatspy_Start"));
		}
	}
}
