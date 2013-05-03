package com.dre.dungeonsxl.signs;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameWorld;

public class SIGNRedstone extends DSign {

	public static String name = "Redstone";
	public String buildPermissions = "dxl.sign.redstone";
	public boolean onDungeonInit = false;

	// Variables
	private boolean initialized;
	private boolean active;
	private Block block;

	public SIGNRedstone(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {

		return true;
	}

	@Override
	public void onInit() {
		this.block = sign.getBlock();
		this.block.setTypeId(0);

		initialized = true;
	}

	@Override
	public void onTrigger() {
		if (initialized && !active) {
			block.setTypeId(152);
			active = true;
		}
	}

	@Override
	public void onDisable() {
		if (initialized && active) {
			block.setTypeId(0);
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
