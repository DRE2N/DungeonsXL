package io.github.dre2n.dungeonsxl.game;

import org.bukkit.GameMode;

public interface GameType {
	
	/**
	 * @return the displayName
	 */
	public String getDisplayName();
	
	/**
	 * @param displayName
	 * the displayName to set
	 */
	public void setDisplayName(String displayName);
	
	/**
	 * @return the signName
	 */
	public String getSignName();
	
	/**
	 * @param signName
	 * the signName to set
	 */
	public void setSignName(String signName);
	
	/**
	 * @return the playerVersusPlayer
	 */
	public boolean isPlayerVersusPlayer();
	
	/**
	 * @param playerVersusPlayer
	 * the playerVersusPlayer to set
	 */
	public void setPlayerVersusPlayer(boolean playerVersusPlayer);
	
	/**
	 * @return the mobWaves
	 */
	public boolean hasMobWaves();
	
	/**
	 * @param mobWaves
	 * the mobWaves to set
	 */
	public void setMobWaves(boolean mobWaves);
	
	/**
	 * @return the rewards
	 */
	public boolean hasRewards();
	
	/**
	 * @param rewards
	 * the rewards to set
	 */
	public void setRewards(boolean rewards);
	
	/**
	 * @return the showTime
	 */
	public boolean getShowTime();
	
	/**
	 * @param showTime
	 * the showTime to set
	 */
	public void setShowTime(boolean showTime);
	
	/**
	 * @return the build
	 */
	public boolean canBuild();
	
	/**
	 * @param build
	 * the build to set
	 */
	public void setBuild(boolean build);
	
	/**
	 * @return the gameMode
	 */
	public GameMode getGameMode();
	
	/**
	 * @param gameMode
	 * the gameMode to set
	 */
	public void setGameMode(GameMode gameMode);
	
}
