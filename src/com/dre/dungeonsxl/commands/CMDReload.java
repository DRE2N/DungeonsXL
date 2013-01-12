package com.dre.dungeonsxl.commands;


import org.bukkit.entity.Player;

public class CMDReload extends DCommand{

	public CMDReload(){
		this.command="reload";
		this.args=0;
		this.help=p.language.get("Help_Cmd_Reload");
		this.permissions="dxl.reload";
	}

	@Override
	public void onExecute(String[] args, Player player) {
		p.msg(player, p.language.get("Cmd_Reload_Start"));
		p.onDisable();
		p.onEnable();
		p.msg(player, p.language.get("Cmd_Reload_Done"));
	}
}
