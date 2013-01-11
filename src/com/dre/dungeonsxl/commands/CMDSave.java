package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;

import com.dre.dungeonsxl.EditWorld;

public class CMDSave extends DCommand{

	public CMDSave(){
		this.command="save";
		this.args=0;
		this.help=p.language.get("help_cmd_save");//"/dxl save - Save the current dungeon.";
		this.permissions="dxl.save";
	}
	
	
	@Override
	public void onExecute(String[] args, Player player) {
		EditWorld eworld=EditWorld.get(player.getWorld());
		if(eworld!=null){
			eworld.save();
			p.msg(player,p.language.get("cmd_save_success"));//ChatColor.GOLD+"Dungeon erfolgreich gespeichert!");
		}else{
			p.msg(player,p.language.get("cmd_save_error1"));//ChatColor.RED+"Du musst einen Dungeon editieren, um ihn zu speichern!");
		}
	}
}
