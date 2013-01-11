package com.dre.dungeonsxl.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.P;

public abstract class DCommand {
	public P p=P.p;
	
	public Player player;
	public boolean costsMoney;
	public String command;
	public int args;
	public String help;
	public String permissions;
	
	// TODO : Add Aliases
	
	public DCommand(){
		costsMoney = false;
	}
	
	public void displayhelp(Player player){
		p.msg(player,ChatColor.RED+this.help);
	}
	
	public boolean playerHasPermissions(Player player){
		if(this.permissions==null){
			return true;
		}
		if(p.permission.has(player, this.permissions)||player.isOp()){
			return true;
		}
		
		return false;
	}
	
	//Abstracts
	public abstract void onExecute(String[] args, Player player);

	
}
