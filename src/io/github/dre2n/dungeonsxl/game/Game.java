package io.github.dre2n.dungeonsxl.game;

import io.github.dre2n.dungeonsxl.player.DGroup;

import java.util.ArrayList;
import java.util.List;

public class Game {
	
	private List<DGroup> dGroups = new ArrayList<DGroup>();
	private GameType type;
	
	public Game(DGroup dGroup) {
		this.dGroups.add(dGroup);
		this.type = GameTypeDefault.DEFAULT;
	}
	
	public Game(DGroup dGroup, GameType type) {
		this.dGroups.add(dGroup);
		this.type = type;
	}
	
	/**
	 * @return the dGroups
	 */
	public List<DGroup> getDGroups() {
		return dGroups;
	}
	
	/**
	 * @param dGroup
	 * the dGroups to add
	 */
	public void addDGroup(DGroup dGroup) {
		dGroups.add(dGroup);
	}
	
	/**
	 * @param dGroup
	 * the dGroups to remove
	 */
	public void removeDGroup(DGroup dGroup) {
		dGroups.remove(dGroup);
	}
	
	/**
	 * @return the type
	 */
	public GameType getType() {
		return type;
	}
	
	/**
	 * @param type
	 * the type to set
	 */
	public void setType(GameType type) {
		this.type = type;
	}
	
}
