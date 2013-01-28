package com.dre.dungeonsxl;

import java.io.File;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MainConfig {
	
	public String language = "de";
	public boolean enableEconomy = false;
	public boolean enableSpout = false;
	
	/* Tutorial */
	public boolean tutorialActivated = false;
	public String tutorialDungeon = "tutorial";
	public String tutorialStartGroup = "default";
	public String tutorialEndGroup = "player";
	
	/* Default Dungeon Settings */
	public DConfig defaultDungeon;
	
	public MainConfig(File file){
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
		
		/* Main Config */
		if(configFile.contains("language")){
			this.language = configFile.getString("language");
		}
		
		if(configFile.contains("enableSpout")){
			this.enableSpout = configFile.getBoolean("enableSpout");
		}
		
		if(configFile.contains("enableEconomy")){
			this.enableEconomy = configFile.getBoolean("enableEconomy");
		}
		
		if(configFile.contains("tutorial.activated")){
			this.tutorialActivated = configFile.getBoolean("tutorial.activated");
		}
		
		if(configFile.contains("tutorial.dungeon")){
			this.tutorialDungeon = configFile.getString("tutorial.dungeon");
		}
		
		if(configFile.contains("tutorial.startgroup")){
			this.tutorialStartGroup = configFile.getString("tutorial.startgroup");
		}
		
		if(configFile.contains("tutorial.endgroup")){
			this.tutorialEndGroup = configFile.getString("tutorial.endgroup");
		}
		
		/* Default Dungeon Config */
		ConfigurationSection configSetion = configFile.getConfigurationSection("default");
		if(configSetion!=null){
			defaultDungeon = new DConfig(configSetion);
			DConfig.mainConfig = defaultDungeon;
		}
		
	}
}
