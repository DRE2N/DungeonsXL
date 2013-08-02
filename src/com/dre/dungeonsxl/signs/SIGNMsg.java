package com.dre.dungeonsxl.signs;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNMsg extends DSign {

	public static String name = "Msg";
	public String buildPermissions = "dxl.sign.msg";
	public boolean onDungeonInit = false;

	// Variables
	private String msg;
	private boolean initialized;
	private CopyOnWriteArrayList<Player> done = new CopyOnWriteArrayList<Player>();

	public SIGNMsg(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		if (sign.getLine(1).equals("")) {
			return false;
		}

		return true;
	}

	@Override
	public void onInit() {
		String lines[] = sign.getLines();

		if (!lines[1].equals("")) {
			String msg = gworld.config.getMsg(p.parseInt(lines[1]), true);
			if (msg != null) {
				this.msg = msg;
				sign.getBlock().setTypeId(0);
			}
		}

		initialized = true;
	}

	@Override
	public boolean onPlayerTrigger(Player player) {
		if (initialized) {
			if (!done.contains(player)) {
				p.msg(player, msg);
				done.add(player);
			}
			if (done.size() >= gworld.world.getPlayers().size()) {
				remove();
			}
		}
		return true;
	}

	@Override
	public void onTrigger() {
		if (initialized) {
			for (Player player : gworld.world.getPlayers()) {
				p.msg(player, msg);
			}
			remove();
		}
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
