package io.github.dre2n.dungeonsxl.player;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.Dungeon;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupStartFloorEvent;
import io.github.dre2n.dungeonsxl.event.requirement.RequirementDemandEvent;
import io.github.dre2n.dungeonsxl.event.reward.RewardAdditionEvent;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.requirement.Requirement;
import io.github.dre2n.dungeonsxl.reward.Reward;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.entity.Player;

public class DGroup {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	private String name;
	private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<Player>();
	private String dungeonName;
	private String mapName;
	private List<String> unplayedFloors = new ArrayList<String>();
	private GameWorld gameWorld;
	private boolean playing;
	private int floorCount;
	private List<Reward> rewards = new ArrayList<Reward>();
	
	public DGroup(String name, Player player, String identifier, boolean multiFloor) {
		plugin.getDGroups().add(this);
		this.name = name;
		
		this.players.add(player);
		
		Dungeon dungeon = plugin.getDungeons().getDungeon(identifier);
		if (multiFloor && dungeon != null) {
			this.dungeonName = identifier;
			this.mapName = dungeon.getConfig().getStartFloor();
			this.unplayedFloors = dungeon.getConfig().getFloors();
			
		} else {
			this.mapName = identifier;
		}
		this.playing = false;
		this.floorCount = 0;
	}
	
	public DGroup(Player player, String identifier, boolean multiFloor) {
		plugin.getDGroups().add(this);
		this.name = "Group_" + plugin.getDGroups().size();
		
		this.players.add(player);
		
		Dungeon dungeon = plugin.getDungeons().getDungeon(identifier);
		if (multiFloor && dungeon != null) {
			this.dungeonName = identifier;
			this.mapName = dungeon.getConfig().getStartFloor();
			this.unplayedFloors = dungeon.getConfig().getFloors();
			
		} else {
			this.mapName = identifier;
		}
		this.playing = false;
		this.floorCount = 0;
	}
	
	// Getters and setters
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name
	 * the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the players
	 */
	public CopyOnWriteArrayList<Player> getPlayers() {
		return players;
	}
	
	/**
	 * @param player
	 * the player to add
	 */
	public void addPlayer(Player player) {
		// Send message
		for (Player groupPlayer : getPlayers()) {
			MessageUtil.sendMessage(groupPlayer, plugin.getDMessages().getMessage(Messages.PLAYER_JOIN_GROUP, player.getName()));
		}
		
		// Add player
		getPlayers().add(player);
	}
	
	/**
	 * @param player
	 * the player to remove
	 */
	public void removePlayer(Player player) {
		getPlayers().remove(player);
		GroupSign.updatePerGroup(this);
		
		// Send message
		for (Player groupPlayer : getPlayers()) {
			MessageUtil.sendMessage(groupPlayer, plugin.getDMessages().getMessage(Messages.PLAYER_LEFT_GROUP, player.getName()));
		}
		
		// Check group
		if (isEmpty()) {
			remove();
		}
	}
	
	/**
	 * @return the gameWorld
	 */
	public GameWorld getGameWorld() {
		return gameWorld;
	}
	
	/**
	 * @param gameWorld
	 * the gameWorld to set
	 */
	public void setGameWorld(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
	}
	
	/**
	 * @return the dungeonName
	 */
	public String getDungeonName() {
		return dungeonName;
	}
	
	/**
	 * @param dungeonName
	 * the dungeonName to set
	 */
	public void setDungeonName(String dungeonName) {
		this.dungeonName = dungeonName;
	}
	
	/**
	 * @return the dungeon (saved by name only)
	 */
	public Dungeon getDungeon() {
		return plugin.getDungeons().getDungeon(dungeonName);
	}
	
	/**
	 * @param dungeon
	 * the dungeon to set (saved by name only)
	 */
	public void setDungeon(Dungeon dungeon) {
		dungeonName = dungeon.getName();
	}
	
	/**
	 * @return if the group is playing
	 */
	public String getMapName() {
		return mapName;
	}
	
	/**
	 * @param name
	 * the name to set
	 */
	public void setMapName(String name) {
		this.mapName = name;
	}
	
	/**
	 * @return the unplayedFloors
	 */
	public List<String> getUnplayedFloors() {
		return unplayedFloors;
	}
	
	/**
	 * @param unplayedFloor
	 * the unplayedFloor to add
	 */
	public void addUnplayedFloor(String unplayedFloor) {
		unplayedFloors.add(unplayedFloor);
	}
	
	/**
	 * @param unplayedFloor
	 * the unplayedFloor to add
	 */
	public void removeUnplayedFloor(String unplayedFloor) {
		if (getDungeon().getConfig().getRemoveWhenPlayed()) {
			unplayedFloors.remove(unplayedFloor);
		}
	}
	
	/**
	 * @return if the group is playing
	 */
	public boolean isPlaying() {
		return playing;
	}
	
	/**
	 * @param playing
	 * set if the group is playing
	 */
	public void setPlaying(boolean playing) {
		this.playing = playing;
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
	 * @return the rewards
	 */
	public List<Reward> getRewards() {
		return rewards;
	}
	
	/**
	 * @param reward
	 * the rewards to add
	 */
	public void addReward(Reward reward) {
		RewardAdditionEvent event = new RewardAdditionEvent(reward, this);
		
		if (event.isCancelled()) {
			return;
		}
		
		rewards.add(reward);
	}
	
	/**
	 * @param reward
	 * the rewards to remove
	 */
	public void removeReward(Reward reward) {
		rewards.remove(reward);
	}
	
	/**
	 * @return whether there are players in the group
	 */
	public boolean isEmpty() {
		return players.isEmpty();
	}
	
	public void remove() {
		plugin.getDGroups().remove(this);
		GroupSign.updatePerGroup(this);
	}
	
	public void startGame() {
		DGroupStartFloorEvent event = new DGroupStartFloorEvent(this, gameWorld);
		
		if (event.isCancelled()) {
			return;
		}
		
		playing = true;
		gameWorld.startGame();
		floorCount++;
		
		for (Player player : getPlayers()) {
			DPlayer dPlayer = DPlayer.getByPlayer(player);
			dPlayer.respawn();
			if (dungeonName != null) {
				MessageUtil.sendScreenMessage(player, "&b&l" + dungeonName.replaceAll("_", " "), "&4&l" + mapName.replaceAll("_", ""));
				
			} else {
				MessageUtil.sendScreenMessage(player, "&4&l" + mapName.replaceAll("_", ""));
			}
			
			for (Requirement requirement : gameWorld.getConfig().getRequirements()) {
				RequirementDemandEvent requirementDemandEvent = new RequirementDemandEvent(requirement, player);
				
				if (requirementDemandEvent.isCancelled()) {
					continue;
				}
				
				requirement.demand(player);
			}
			
		}
		
		GroupSign.updatePerGroup(this);
	}
	
	/**
	 * Send a message to all players in the group
	 */
	public void sendMessage(String message) {
		for (Player player : players) {
			if (player.isOnline()) {
				MessageUtil.sendCenteredMessage(player, message);
			}
		}
	}
	
	/**
	 * Send a message to all players in the group
	 * 
	 * @param except
	 * Players who do not receive the message
	 */
	public void sendMessage(String message, Player... except) {
		for (Player player : players) {
			if (player.isOnline() && !player.equals(except)) {
				MessageUtil.sendCenteredMessage(player, message);
			}
		}
	}
	
	// Statics
	
	public static DGroup getByPlayer(Player player) {
		for (DGroup dGroup : plugin.getDGroups()) {
			if (dGroup.getPlayers().contains(player)) {
				return dGroup;
			}
		}
		
		return null;
	}
	
	public static DGroup getByGameWorld(GameWorld gameWorld) {
		for (DGroup dGroup : plugin.getDGroups()) {
			if (dGroup.getGameWorld() == gameWorld) {
				return dGroup;
			}
		}
		
		return null;
	}
	
	public static void leaveGroup(Player player) {
		for (DGroup dGroup : plugin.getDGroups()) {
			if (dGroup.getPlayers().contains(player)) {
				dGroup.getPlayers().remove(player);
			}
		}
	}
	
}
