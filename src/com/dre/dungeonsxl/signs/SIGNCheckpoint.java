package com.dre.dungeonsxl.signs;

import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNCheckpoint extends DSign {

	public static String name = "Checkpoint";
	private String buildPermissions = "dxl.sign.checkpoint";
	private boolean onDungeonInit = false;

	// Variables
	private boolean initialized;
	private CopyOnWriteArrayList<DPlayer> done = new CopyOnWriteArrayList<DPlayer>();

	public SIGNCheckpoint(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public void onInit() {
		sign.getBlock().setTypeId(0);

		initialized = true;
	}

	@Override
	public void onTrigger() {
		if (initialized) {
			for (DPlayer dplayer : DPlayer.get(this.gworld.world)) {
				dplayer.setCheckpoint(this.sign.getLocation());
				P.p.msg(dplayer.player, P.p.language.get("Player_CheckpointReached"));
			}

			remove();
		}
	}

	@Override
	public boolean onPlayerTrigger(Player player) {
		if (initialized) {
			DPlayer dplayer = DPlayer.get(player);
			if (dplayer != null) {
				if (!done.contains(dplayer)) {
					done.add(dplayer);
					dplayer.setCheckpoint(this.sign.getLocation());
					P.p.msg(player, P.p.language.get("Player_CheckpointReached"));
				}
			}
			if (done.size() >= DPlayer.get(this.gworld.world).size()) {
				remove();
			}
		}
		return true;
	}

	@Override
	public String getPermissions() {
		return buildPermissions;
	}

	@Override
	public boolean isOnDungeonInit() {
		return onDungeonInit;
	}
}
