package com.dre.dungeonsxl.signs;

import org.bukkit.Material;
import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameWorld;

public class SIGNLobby extends DSign {

	public static String name = "Lobby";
	public String buildPermissions = "dxl.sign.lobby";
	public boolean onDungeonInit = true;

	public SIGNLobby(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public void onInit() {
		gworld.locLobby = sign.getLocation();
		sign.getBlock().setType(Material.AIR);
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
