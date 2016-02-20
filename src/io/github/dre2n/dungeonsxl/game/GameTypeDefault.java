package io.github.dre2n.dungeonsxl.game;

import org.bukkit.GameMode;

public enum GameTypeDefault implements GameType {
	
	ADVENTURE("Adventure", "Adventure", false, false, false, true, false, true, GameMode.ADVENTURE),
	ADVENTURE_TIME_IS_RUNNING("Adventure - Time is Running", "Adventure TiR", false, false, false, true, true, true, GameMode.ADVENTURE),
	APOCALYPSE_LAST_MAN_STANDING("Apocalypse", "Apocalypse LMS", true, true, true, true, false, false, GameMode.SURVIVAL),
	APOCALYPSE_LIMITED_MOBS("Apocalypse - Limited Mobs", "Apc Limited", true, true, true, true, false, false, GameMode.SURVIVAL),
	APOCALYPSE_TIME_IS_RUNNING("Apocalypse - Time is Running", "Apocalypse TiR", true, true, true, true, true, false, GameMode.SURVIVAL),
	PVE_LAST_MAN_STANDING("Player versus Environment - Last Man Standing", "PvE LMS", false, false, true, true, false, false, GameMode.SURVIVAL),
	PVE_LIMITED_MOBS("Player versus Environment - Limited Mobs", "PvE Limited", false, false, true, true, false, false, GameMode.SURVIVAL),
	PVE_TIME_IS_RUNNING("Player versus Environment - Time is Running", "PvE TiR", false, false, true, true, true, false, GameMode.SURVIVAL),
	PVP_FACTIONS_BATTLEFIELD("Player versus Player - Factions Battlefield", "FactionsPvP", true, false, false, false, false, false, GameMode.SURVIVAL),
	PVP_LAST_MAN_STANDING("Player versus Player - Last Man Standing", "PvP LMS", true, false, false, false, false, false, GameMode.SURVIVAL),
	QUEST("Quest", "Quest", false, false, false, true, false, false, GameMode.SURVIVAL),
	QUEST_TIME_IS_RUNNING("Quest - Time is Running", "Quest TiR", false, false, false, true, true, false, GameMode.SURVIVAL),
	TEST("Test", "Test", false, false, false, false, true, true, GameMode.SURVIVAL),
	TUTORIAL("Tutorial", "Tutorial", false, false, false, true, false, false, GameMode.SURVIVAL),
	DEFAULT("Default", "Default", false, false, false, true, false, false, GameMode.SURVIVAL);
	
	private String displayName;
	private String signName;
	private boolean playerVersusPlayer;// TODO: Testing
	private boolean friendlyFire;// TODO: Testing
	private boolean mobWaves;// TODO: Implementing
	private boolean rewards;// TODO: Testing
	private boolean showTime;// TODO: Implementing
	private boolean build;// TODO: Testing
	private GameMode gameMode;// TODO: Testing
	
	GameTypeDefault(String displayName, String signName, boolean playerVersusPlayer, boolean friendlyFire, boolean mobWaves, boolean rewards, boolean showTime, boolean build, GameMode gameMode) {
		this.displayName = displayName;
		this.signName = signName;
		this.playerVersusPlayer = playerVersusPlayer;
		this.friendlyFire = friendlyFire;
		this.mobWaves = mobWaves;
		this.rewards = rewards;
		this.showTime = showTime;
		this.build = build;
		this.gameMode = gameMode;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String getSignName() {
		return signName;
	}
	
	@Override
	public void setSignName(String signName) {
		this.signName = signName;
	}
	
	@Override
	public boolean isPlayerVersusPlayer() {
		return playerVersusPlayer;
	}
	
	@Override
	public void setPlayerVersusPlayer(boolean playerVersusPlayer) {
		this.playerVersusPlayer = playerVersusPlayer;
	}
	
	@Override
	public boolean isFriendlyFire() {
		return friendlyFire;
	}
	
	@Override
	public void setFriendlyFire(boolean friendlyFire) {
		this.friendlyFire = friendlyFire;
	}
	
	@Override
	public boolean hasMobWaves() {
		return mobWaves;
	}
	
	@Override
	public void setMobWaves(boolean mobWaves) {
		this.mobWaves = mobWaves;
	}
	
	@Override
	public boolean hasRewards() {
		return rewards;
	}
	
	@Override
	public void setRewards(boolean rewards) {
		this.rewards = rewards;
	}
	
	@Override
	public boolean getShowTime() {
		return showTime;
	}
	
	@Override
	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}
	
	@Override
	public boolean canBuild() {
		return build;
	}
	
	@Override
	public void setBuild(boolean build) {
		this.build = build;
	}
	
	@Override
	public GameMode getGameMode() {
		return gameMode;
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}
	
}
