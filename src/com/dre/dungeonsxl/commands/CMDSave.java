package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;

import com.dre.dungeonsxl.EditWorld;

public class CMDSave extends DCommand{

	public CMDSave(){
		this.command="save";
		this.args=0;
		this.help=p.language.get("Help_Cmd_Save");
		this.permissions="dxl.save";
	}


	@Override
	public void onExecute(String[] args, Player player) {
		EditWorld eworld=EditWorld.get(player.getWorld());
		if(eworld!=null){
			eworld.save();
			p.msg(player,p.language.get("Cmd_Save_Success"));
		}else{
			p.msg(player,p.language.get("Error_NotInDungeon"));
		}
	}
}
