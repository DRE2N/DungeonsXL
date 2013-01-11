package com.dre.dungeonsxl.commands;

import org.bukkit.entity.Player;

import com.dre.dungeonsxl.EditWorld;

public class CMDInvite extends DCommand{
	public CMDInvite(){
		this.args=2;
		this.command="invite";
		this.help=p.language.get("help_cmd_invite");//"/dxl invite <player> <dungeon> - Invites a Player to edit a Dungeon";
		this.permissions="dxl.invite";
	}
	
	@Override
	public void onExecute(String[] args, Player player) {
		if(EditWorld.addInvitedPlayer(args[2], args[1])){
			p.msg(player, p.language.get("cmd_invite_success",args[1],args[2]));//ChatColor.GREEN+"Spieler "+ChatColor.GOLD+args[1]+ChatColor.GREEN+" wurde erfolgreich eingeladen am Dungeon "+ChatColor.GOLD+args[2]+ChatColor.GREEN+" zu arbeiten!");
		}else{
			p.msg(player, p.language.get("cmd_invite_error1",args[2]));//ChatColor.RED+"Spieler "+ChatColor.GOLD+args[1]+ChatColor.RED+" konnte nicht eingeladen werden. Existiert der Dungeon?");
		}
		
	}
}
