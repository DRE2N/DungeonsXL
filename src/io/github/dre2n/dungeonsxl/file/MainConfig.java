package io.github.dre2n.dungeonsxl.file;

import io.github.dre2n.dungeonsxl.dungeon.WorldConfig;

import java.io.File;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MainConfig {
	
	private String language = "en";
	private boolean enableEconomy = false;
	
	/* Tutorial */
	private boolean tutorialActivated = false;
	private String tutorialDungeon = "tutorial";
	private String tutorialStartGroup = "default";
	private String tutorialEndGroup = "player";
	
	/* Default Dungeon Settings */
	public WorldConfig defaultDungeon;
	
	public MainConfig(File file) {
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
		
		/* Main Config */
		if (configFile.contains("language")) {
			language = configFile.getString("language");
		}
		
		if (configFile.contains("enableEconomy")) {
			enableEconomy = configFile.getBoolean("enableEconomy");
		}
		
		if (configFile.contains("tutorial.activated")) {
			tutorialActivated = configFile.getBoolean("tutorial.activated");
		}
		
		if (configFile.contains("tutorial.dungeon")) {
			tutorialDungeon = configFile.getString("tutorial.dungeon");
		}
		
		if (configFile.contains("tutorial.startgroup")) {
			tutorialStartGroup = configFile.getString("tutorial.startgroup");
		}
		
		if (configFile.contains("tutorial.endgroup")) {
			tutorialEndGroup = configFile.getString("tutorial.endgroup");
		}
		
		/* Default Dungeon Config */
		ConfigurationSection configSetion = configFile.getConfigurationSection("default");
		if (configSetion != null) {
			defaultDungeon = new WorldConfig(configSetion);
			WorldConfig.defaultConfig = defaultDungeon;
		}
	}
	
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * @param language
	 * the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	/**
	 * @return the enableEconomy
	 */
	public boolean enableEconomy() {
		return enableEconomy;
	}
	
	/**
	 * @return the tutorialActivated
	 */
	public boolean isTutorialActivated() {
		return tutorialActivated;
	}
	
	/**
	 * @return the tutorialDungeon
	 */
	public String getTutorialDungeon() {
		return tutorialDungeon;
	}
	
	/**
	 * @return the tutorialStartGroup
	 */
	public String getTutorialStartGroup() {
		return tutorialStartGroup;
	}
	
	/**
	 * @return the tutorialEndGroup
	 */
	public String getTutorialEndGroup() {
		return tutorialEndGroup;
	}
	
}
