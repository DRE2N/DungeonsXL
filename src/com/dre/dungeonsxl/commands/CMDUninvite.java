package com.dre.dungeonsxl.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.EditWorld;

public class CMDUninvite extends DCommand{
	public CMDUninvite(){
		this.args=2;
		this.command="uninvite";
		this.help="/dxl uninvite <player> <dungeon> - Uninvites a Player to edit a Dungeon";
		this.permissions="dxl.uninvite";
	}
	
	@Override
	public void onExecute(String[] args, Player player) {
		if(EditWorld.removeInvitedPlayer(args[2], args[1])){
			p.msg(player, ChatColor.GREEN+"Spieler "+ChatColor.GOLD+args[1]+ChatColor.GREEN+" wurde erfolgreich ausgeladen am Dungeon "+ChatColor.GOLD+args[2]+ChatColor.GREEN+" zu arbeiten!");
		}else{
			p.msg(player, ChatColor.RED+"Spieler "+ChatColor.GOLD+args[1]+ChatColor.GOLD+" konnte nicht ausgeladen werden. Existiert der Dungeon?");
		}
		
	}
}
