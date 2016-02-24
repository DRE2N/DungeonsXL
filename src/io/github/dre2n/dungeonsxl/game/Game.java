package io.github.dre2n.dungeonsxl.game;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.player.DGroup;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class Game {
	
	protected static DungeonsXL plugin = DungeonsXL.getPlugin();
	
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
	
	/**
	 * @return if the DGroup list is empty
	 */
	public boolean isEmpty() {
		return dGroups.isEmpty();
	}
	
	// Static
	
	public static Game getByDGroup(DGroup dGroup) {
		for (Game game : plugin.getGames()) {
			if (game.getDGroups().contains(dGroup)) {
				return game;
			}
		}
		
		return null;
	}
	
	public static Game getByPlayer(Player player) {
		return getByDGroup(DGroup.getByPlayer(player));
	}
	
	public static Game getByGameWorld(GameWorld gameWorld) {
		for (Game game : plugin.getGames()) {
			if (game.getDGroups().get(0).getGameWorld().equals(gameWorld)) {
				return game;
			}
		}
		
		return null;
	}
	
}
