package com.dre.dungeonsxl;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.entity.Player;

import com.dre.dungeonsxl.game.GameWorld;

public class DGroup {
	public static CopyOnWriteArrayList<DGroup> dgroups = new CopyOnWriteArrayList<DGroup>();

	private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<Player>();
	private String dungeonname;
	private GameWorld gworld;
	public boolean isPlaying;

	public DGroup(Player player, String dungeonname) {
		dgroups.add(this);

		this.getPlayers().add(player);
		this.isPlaying = false;
		this.setDungeonname(dungeonname);
	}

	public void addPlayer(Player player) {
		// Send message
		for (Player groupPlayer : this.getPlayers()) {
			P.p.msg(groupPlayer, P.p.language.get("Player_JoinGroup", player.getName()));
		}

		// Add player
		this.getPlayers().add(player);
	}

	public void removePlayer(Player player) {
		this.getPlayers().remove(player);
		DGSign.updatePerGroup(this);

		// Send message
		for (Player groupPlayer : this.getPlayers()) {
			P.p.msg(groupPlayer, P.p.language.get("Player_LeftGroup", player.getName()));
		}

		// Check group
		if (this.isEmpty()) {
			this.remove();
		}
	}

	public boolean isEmpty() {
		return this.getPlayers().isEmpty();
	}

	public void remove() {
		dgroups.remove(this);
		DGSign.updatePerGroup(this);
	}

	public void startGame() {
		this.isPlaying = true;
		getGworld().startGame();
		for (Player player : getPlayers()) {
			DPlayer dplayer = DPlayer.get(player);
			dplayer.respawn();
			if (P.p.mainConfig.enableEconomy) {
				File file = new File(P.p.getDataFolder() + "/dungeons/" + dungeonname + "/config.yml");
				if (file != null) {
					DConfig confReader = new DConfig(file);
					if (confReader != null) {
						P.economy.withdrawPlayer(player, confReader.getFee());
					}
				}
			}
		}
		DGSign.updatePerGroup(this);

	}

	// Statics
	public static DGroup get(Player player) {
		for (DGroup dgroup : dgroups) {
			if (dgroup.getPlayers().contains(player)) {
				return dgroup;
			}
		}
		return null;
	}

	public static DGroup get(GameWorld gworld) {
		for (DGroup dgroup : dgroups) {
			if (dgroup.getGworld() == gworld) {
				return dgroup;
			}
		}
		return null;
	}

	public static void leaveGroup(Player player) {
		for (DGroup dgroup : dgroups) {
			if (dgroup.getPlayers().contains(player)) {
				dgroup.getPlayers().remove(player);
			}
		}
	}

	// Getters and setters

	public CopyOnWriteArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(CopyOnWriteArrayList<Player> players) {
		this.players = players;
	}

	public GameWorld getGworld() {
		return gworld;
	}

	public void setGworld(GameWorld gworld) {
		this.gworld = gworld;
	}

	public String getDungeonname() {
		return dungeonname;
	}

	public void setDungeonname(String dungeonname) {
		this.dungeonname = dungeonname;
	}

}
