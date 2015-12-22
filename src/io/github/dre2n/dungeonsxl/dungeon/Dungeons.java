package io.github.dre2n.dungeonsxl.dungeon;

import io.github.dre2n.dungeonsxl.DungeonsXL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Dungeons {
	
	private List<Dungeon> dungeons = new ArrayList<Dungeon>();
	
	public Dungeons() {
		File folder = new File(DungeonsXL.getPlugin().getDataFolder() + "/dungeons");
		
		if ( !folder.exists()) {
			folder.mkdir();
		}
		
		for (File file : folder.listFiles()) {
			dungeons.add(new Dungeon(file));
		}
	}
	
	/**
	 * @return the dungeons
	 */
	public List<Dungeon> getDungeons() {
		return dungeons;
	}
	
	/**
	 * @param name
	 * the name of the Dungeon
	 * @return the Dungeon that has the name
	 */
	public Dungeon getDungeon(String name) {
		for (Dungeon dungeon : dungeons) {
			if (dungeon.getName().equals(name)) {
				return dungeon;
			}
		}
		
		return null;
	}
	
	/**
	 * @param name
	 * the name of the Dungeon
	 * @return the Dungeon that has the name
	 */
	public Dungeon loadDungeon(String name) {
		Dungeon dungeon = new Dungeon(name);
		dungeons.add(dungeon);
		return dungeon;
	}
	
	/**
	 * @param dungeon
	 * the dungeon to add
	 */
	public void addDungeon(Dungeon dungeon) {
		dungeons.add(dungeon);
	}
	
	/**
	 * @param dungeon
	 * the dungeon to remove
	 */
	public void removeDungeon(Dungeon dungeon) {
		dungeons.remove(dungeon);
	}
	
}
