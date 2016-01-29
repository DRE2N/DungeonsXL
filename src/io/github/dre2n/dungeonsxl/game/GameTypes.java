package io.github.dre2n.dungeonsxl.game;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.game.GameType;
import io.github.dre2n.dungeonsxl.game.GameTypeDefault;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Sign;

public class GameTypes {
	
	private List<GameType> types = new ArrayList<GameType>();
	
	public GameTypes() {
		for (GameType type : GameTypeDefault.values()) {
			if (type == GameTypeDefault.PVP_FACTIONS_BATTLEFIELD) {
				try {
					Class.forName("com.massivecraft.factions.Patch");
					
				} catch (ClassNotFoundException exception) {
					DungeonsXL.getPlugin().getLogger().info("Could not find compatible Factions plugin. The game type PVP_FACTIONS_BATTLEFIELD will not get enabled...");
					continue;
				}
			}
			
			types.add(type);
		}
	}
	
	/**
	 * @return the game type which has the enum value name
	 */
	public GameType getByName(String name) {
		for (GameType type : types) {
			if (type.toString().equals(name)) {
				return type;
			}
		}
		
		return null;
	}
	
	/**
	 * @return the game type which has the enum value sign text in the second line of the sign
	 */
	public GameType getBySign(Sign sign) {
		String[] lines = sign.getLines();
		
		for (GameType type : types) {
			if (type.getSignName().equals(lines[1])) {
				return type;
			}
		}
		
		return null;
	}
	
	/**
	 * @return the game types
	 */
	public List<GameType> getGameTypes() {
		return types;
	}
	
	/**
	 * @param type
	 * the game type to add
	 */
	public void addGameType(GameType type) {
		types.add(type);
	}
	
	/**
	 * @param game
	 * the game to remove
	 */
	public void removeGameType(GameType type) {
		types.remove(type);
	}
	
}
