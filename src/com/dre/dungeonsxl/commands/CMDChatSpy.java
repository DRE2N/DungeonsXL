package com.dre.dungeonsxl.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CMDChatSpy extends DCommand{
	public CMDChatSpy(){
		this.command="chatspy";
		this.args=0;
		this.help="/dxl chatspy - Spionier den DXL-Chat";
		this.permissions="dxl.chatspy";
	}
	
	@Override
	public void onExecute(String[] args, Player player) {
		if(p.chatSpyer.contains(player)){
			p.chatSpyer.remove(player);
			p.msg(player, ChatColor.GOLD+"Du hast aufgehört den DXL-Chat auszuspähen!");
		}
		
		else{
			p.chatSpyer.add(player);
			p.msg(player, ChatColor.GOLD+"Du hast begonnen den DXL-Chat auszuspähen!");
		}
	}
}
