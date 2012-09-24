package com.dre.dungeonsxl.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.EditWorld;

public class CMDSave extends DCommand{

	public CMDSave(){
		this.command="save";
		this.args=0;
		this.help="/dxl save - Save the current dungeon.";
		this.permissions="dxl.save";
	}
	
	
	@Override
	public void onExecute(String[] args, Player player) {
		EditWorld eworld=EditWorld.get(player.getWorld());
		if(eworld!=null){
			eworld.save();
			p.msg(player,ChatColor.GOLD+"Dungeon erfolgreich gespeichert!");
		}else{
			p.msg(player,ChatColor.RED+"Du musst einen Dungeon editieren, um ihn zu speichern!");
		}
		
		
	}
}
