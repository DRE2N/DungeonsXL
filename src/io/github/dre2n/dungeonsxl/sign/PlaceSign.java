package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GamePlaceableBlock;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;

import org.bukkit.Material;
import org.bukkit.block.Sign;

public class PlaceSign extends DSign {
	
	public static String name = "Place";
	public String buildPermissions = "dxl.sign.place";
	public boolean onDungeonInit = false;
	
	public PlaceSign(Sign sign, GameWorld gWorld) {
		super(sign, gWorld);
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@Override
	public void onInit() {
		String lines[] = getSign().getLines();
		getGWorld().placeableBlocks.add(new GamePlaceableBlock(getSign().getBlock(), lines[1], lines[2]));
		getSign().getBlock().setType(Material.AIR);
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
