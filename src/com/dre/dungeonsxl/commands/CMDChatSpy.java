package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;

public class CMDChatSpy extends DCommand{
	public CMDChatSpy(){
		this.command="chatspy";
		this.args=0;
		this.help=p.language.get("help_cmd_chatspy");
		this.permissions="dxl.chatspy";
	}
	
	@Override
	public void onExecute(String[] args, Player player) {
		if(p.chatSpyer.contains(player)){
			p.chatSpyer.remove(player);
			p.msg(player, p.language.get("cmd_chatspy_stopped"));//ChatColor.GOLD+"Du hast aufgehört den DXL-Chat auszuspähen!");
		}
		
		else{
			p.chatSpyer.add(player);
			p.msg(player, p.language.get("cmd_chatspy_start"));//ChatColor.GOLD+"Du hast begonnen den DXL-Chat auszuspähen!");
		}
	}
}
