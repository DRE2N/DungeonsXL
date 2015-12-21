package io.github.dre2n.dungeonsxl.dungeon;

import io.github.dre2n.dungeonsxl.DungeonsXL;

import java.io.File;

public class Dungeon {
	
	private String name;
	private DungeonConfig config;
	
	public Dungeon(File file) {
		this.name = file.getName().replaceAll(".yml", "");
		this.config = new DungeonConfig(file);
	}
	
	public Dungeon(String name) {
		this.name = name;
		this.config = new DungeonConfig(new File(DungeonsXL.getPlugin().getDataFolder() + "/dungeons", name + ".yml"));
		System.out.println("Dungeon" + config.getFloors());
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the config
	 */
	public DungeonConfig getConfig() {
		return config;
	}
	
}
