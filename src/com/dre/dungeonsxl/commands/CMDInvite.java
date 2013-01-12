package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;

import com.dre.dungeonsxl.EditWorld;

public class CMDInvite extends DCommand{
	public CMDInvite(){
		this.args=2;
		this.command="invite";
		this.help=p.language.get("Help_Cmd_Invite");
		this.permissions="dxl.invite";
	}

	@Override
	public void onExecute(String[] args, Player player) {
		if(EditWorld.addInvitedPlayer(args[2], args[1])){
			p.msg(player, p.language.get("Cmd_Invite_Success",args[1],args[2]));
		}else{
			p.msg(player, p.language.get("Error_DungeonNotExist",args[2]));
		}

	}
}
