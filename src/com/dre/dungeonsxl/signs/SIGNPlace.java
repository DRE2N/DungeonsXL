package com.dre.dungeonsxl.signs;

import org.bukkit.Material;
import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GamePlaceableBlock;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNPlace extends DSign {

	public static String name = "Place";
	public String buildPermissions = "dxl.sign.place";
	public boolean onDungeonInit = false;

	public SIGNPlace(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		return true;
	}

	@Override
	public void onInit() {
		String lines[] = sign.getLines();
		gworld.placeableBlocks.add(new GamePlaceableBlock(sign.getBlock(), lines[1], lines[2]));
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
