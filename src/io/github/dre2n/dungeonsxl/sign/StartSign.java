package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;

import org.bukkit.Material;
import org.bukkit.block.Sign;

public class StartSign extends DSign {
	
	public static String name = "Start";
	public String buildPermissions = "dxl.sign.start";
	public boolean onDungeonInit = true;
	
	public StartSign(Sign sign, GameWorld gWorld) {
		super(sign, gWorld);
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@Override
	public void onInit() {
		getGWorld().locStart = getSign().getLocation();
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
