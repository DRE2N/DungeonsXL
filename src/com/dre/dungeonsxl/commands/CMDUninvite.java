package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;

import com.dre.dungeonsxl.EditWorld;

public class CMDUninvite extends DCommand{
	public CMDUninvite(){
		this.args=2;
		this.command="uninvite";
		this.help=p.language.get("help_cmd_uninvite");
		this.permissions="dxl.uninvite";
	}
	
	@Override
	public void onExecute(String[] args, Player player) {
		if(EditWorld.removeInvitedPlayer(args[2], args[1])){
			p.msg(player, p.language.get("cmd_uninvite_success",args[1],args[2]));
		}else{
			p.msg(player, p.language.get("cmd_uninvite_error1",args[2]));
		}
	}
}
