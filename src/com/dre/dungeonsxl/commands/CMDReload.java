package com.dre.dungeonsxl.commands;


import java.io.File;

import org.bukkit.command.CommandSender;
import com.dre.dungeonsxl.DMobType;
import com.dre.dungeonsxl.LanguageReader;
import com.dre.dungeonsxl.MainConfig;

public class CMDReload extends DCommand{

	public CMDReload(){
		this.command="reload";
		this.args=0;
		this.help=p.language.get("Help_Cmd_Reload");
		this.permissions="dxl.reload";
		this.isPlayerCommand = true;
		this.isConsoleCommand = true;
	}

	@Override
	public void onExecute(String[] args, CommandSender sender) {
		p.msg(sender, p.language.get("Cmd_Reload_Start"));
		
		//Save
		p.saveData();
		p.language.save();
		
		//Load Config
		p.mainConfig = new MainConfig(new File(p.getDataFolder(), "config.yml"));
				
		//Load Language
		p.language = new LanguageReader(new File(p.getDataFolder(), "languages/"+p.mainConfig.language+".yml"));
		
		//Mobtype
		DMobType.clear();
		DMobType.load(new File(p.getDataFolder(), "mobs.yml"));
		
		p.msg(sender, p.language.get("Cmd_Reload_Done"));
	}
}
