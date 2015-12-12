package io.github.dre2n.dungeonsxl.player;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.WorldConfig;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.entity.Player;

public class DGroup {
	
	static DungeonsXL plugin = DungeonsXL.getPlugin();
	
	private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<Player>();
	private String dungeonname;
	private GameWorld gWorld;
	private boolean playing;
	
	public DGroup(Player player, String dungeonname) {
		plugin.getDGroups().add(this);
		
		getPlayers().add(player);
		playing = false;
		setDungeonname(dungeonname);
	}
	
	// Getters and setters
	
	public CopyOnWriteArrayList<Player> getPlayers() {
		return players;
	}
	
	public void setPlayers(CopyOnWriteArrayList<Player> players) {
		this.players = players;
	}
	
	public GameWorld getGworld() {
		return gWorld;
	}
	
	public void setGworld(GameWorld gWorld) {
		this.gWorld = gWorld;
	}
	
	public String getDungeonname() {
		return dungeonname;
	}
	
	public void setDungeonname(String dungeonname) {
		this.dungeonname = dungeonname;
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
	
	public void addPlayer(Player player) {
		// Send message
		for (Player groupPlayer : getPlayers()) {
			MessageUtil.sendMessage(groupPlayer, DungeonsXL.getPlugin().getDMessages().get("Player_JoinGroup", player.getName()));
		}
		
		// Add player
		getPlayers().add(player);
	}
	
	public void removePlayer(Player player) {
		getPlayers().remove(player);
		GroupSign.updatePerGroup(this);
		
		// Send message
		for (Player groupPlayer : getPlayers()) {
			MessageUtil.sendMessage(groupPlayer, DungeonsXL.getPlugin().getDMessages().get("Player_LeftGroup", player.getName()));
		}
		
		// Check group
		if (isEmpty()) {
			remove();
		}
	}
	
	public boolean isEmpty() {
		return getPlayers().isEmpty();
	}
	
	public void remove() {
		plugin.getDGroups().remove(this);
		GroupSign.updatePerGroup(this);
	}
	
	public void startGame() {
		playing = true;
		getGworld().startGame();
		for (Player player : getPlayers()) {
			DPlayer dplayer = DPlayer.get(player);
			dplayer.respawn();
			if (DungeonsXL.getPlugin().getMainConfig().enableEconomy()) {
				File file = new File(DungeonsXL.getPlugin().getDataFolder() + "/maps/" + dungeonname + "/config.yml");
				if (file != null) {
					WorldConfig confReader = new WorldConfig(file);
					if (confReader != null) {
						DungeonsXL.getPlugin().economy.withdrawPlayer(player, confReader.getFee());
					}
				}
			}
		}
		GroupSign.updatePerGroup(this);
		
	}
	
	// Statics
	public static DGroup get(Player player) {
		for (DGroup dgroup : plugin.getDGroups()) {
			if (dgroup.getPlayers().contains(player)) {
				return dgroup;
			}
		}
		return null;
	}
	
	public static DGroup get(GameWorld gWorld) {
		for (DGroup dgroup : plugin.getDGroups()) {
			if (dgroup.getGworld() == gWorld) {
				return dgroup;
			}
		}
		return null;
	}
	
	public static void leaveGroup(Player player) {
		for (DGroup dgroup : plugin.getDGroups()) {
			if (dgroup.getPlayers().contains(player)) {
				dgroup.getPlayers().remove(player);
			}
		}
	}
	
}
