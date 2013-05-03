package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNBlock extends DSign {

	public static String name = "Block";
	public String buildPermissions = "dxl.sign.block";
	public boolean onDungeonInit = false;

	// Variables
	private boolean initialized;
	private boolean active;
	private int offBlockId = 0;
	private int onBlockId = 0;

	public SIGNBlock(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public void onInit() {
		String lines[] = sign.getLines();
		offBlockId = p.parseInt(lines[1]);
		onBlockId = p.parseInt(lines[2]);
		sign.getBlock().setTypeId(offBlockId);
		initialized = true;
	}

	@Override
	public void onTrigger() {
		if (initialized && !active) {
			sign.getBlock().setTypeId(onBlockId);
			active = true;
		}
	}

	@Override
	public void onDisable() {
		if (initialized && active) {
			sign.getBlock().setTypeId(offBlockId);
			active = false;
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
