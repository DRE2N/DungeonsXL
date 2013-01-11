package com.dre.dungeonsxl.commands;


import org.bukkit.entity.Player;

public class CMDReload extends DCommand{

	public CMDReload(){
		this.command="reload";
		this.args=0;
		this.help=p.language.get("help_cmd_reload");//"/dxl reload - Reloadet das Plugin";
		this.permissions="dxl.reload";
	}
	
	@Override
	public void onExecute(String[] args, Player player) {
		p.msg(player, p.language.get("cmd_reload_start"));//ChatColor.GREEN+"DungeonsXL wird neu geladen");
		p.onDisable();
		p.onEnable();
		p.msg(player, p.language.get("cmd_reload_done"));//ChatColor.GREEN+"DungeonsXL neuladen erfolgreich!");
	}
}
