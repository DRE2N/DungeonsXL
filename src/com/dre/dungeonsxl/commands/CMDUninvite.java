package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;

import com.dre.dungeonsxl.EditWorld;

public class CMDUninvite extends DCommand{
	public CMDUninvite(){
		this.args=2;
		this.command="uninvite";
		this.help=p.language.get("Help_Cmd_Uninvite");
		this.permissions="dxl.uninvite";
	}

	@Override
	public void onExecute(String[] args, Player player) {
		if(EditWorld.removeInvitedPlayer(args[2], args[1])){
			p.msg(player, p.language.get("Cmd_Uninvite_Success",args[1],args[2]));
		}else{
			p.msg(player, p.language.get("Error_DungeonNotExist",args[2]));
		}
	}
}
