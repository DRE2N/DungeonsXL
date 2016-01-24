package io.github.dre2n.dungeonsxl.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class DungeonConfig extends WorldConfig {
	
	private String startFloor;
	private String endFloor;
	private List<String> floors = new ArrayList<String>();
	private int floorCount;
	private boolean removeWhenPlayed;
	
	public DungeonConfig(File file) {
		super(file);
		load(YamlConfiguration.loadConfiguration(file));
	}
	
	public DungeonConfig(ConfigurationSection configFile) {
		super(configFile);
		load(configFile);
	}
	
	@Override
	public void load(ConfigurationSection configFile) {
		super.load(configFile);
		
		/* Floors */
		if (configFile.contains("floors")) {
			floors = configFile.getStringList("floors");
		}
		
		if (configFile.contains("startFloor")) {
			startFloor = configFile.getString("startFloor");
		}
		
		if (configFile.contains("endFloor")) {
			endFloor = configFile.getString("endFloor");
		}
		
		if (configFile.contains("floorCount")) {
			floorCount = configFile.getInt("floorCount");
		}
		
		if (configFile.contains("removeWhenPlayed")) {
			removeWhenPlayed = configFile.getBoolean("removeWhenPlayed");
		}
	}
	
	/**
	 * @return the startFloor
	 */
	public String getStartFloor() {
		return startFloor;
	}
	
	/**
	 * @param startFloor
	 * the startFloor to set
	 */
	public void setStartFloor(String startFloor) {
		this.startFloor = startFloor;
	}
	
	/**
	 * @return the endFloor
	 */
	public String getEndFloor() {
		return endFloor;
	}
	
	/**
	 * @param endFloor
	 * the endFloor to set
	 */
	public void setEndFloor(String endFloor) {
		this.endFloor = endFloor;
	}
	
	/**
	 * @return the floors
	 */
	public List<String> getFloors() {
		return floors;
	}
	
	/**
	 * @param gameWorld
	 * the gameWorld to add
	 */
	public void addFloor(String gameWorld) {
		floors.add(gameWorld);
	}
	
	/**
	 * @param gameWorld
	 * the gameWorld to remove
	 */
	public void removeFloor(String gameWorld) {
		floors.remove(gameWorld);
	}
	
	/**
	 * @return the floorCount
	 */
	public int getFloorCount() {
		return floorCount;
	}
	
	/**
	 * @param floorCount
	 * the floorCount to set
	 */
	public void setFloorCount(int floorCount) {
		this.floorCount = floorCount;
	}
	
	/**
	 * @return the removeWhenPlayed
	 */
	public boolean getRemoveWhenPlayed() {
		return removeWhenPlayed;
	}
	
	/**
	 * @param removeWhenPlayed
	 * the removeWhenPlayed to set
	 */
	public void setRemoveWhenPlayed(boolean removeWhenPlayed) {
		this.removeWhenPlayed = removeWhenPlayed;
	}
	
}
