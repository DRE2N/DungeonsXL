package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;

import org.bukkit.Material;
import org.bukkit.block.Sign;

public class LobbySign extends DSign {
	
	public static String name = "Lobby";
	public String buildPermissions = "dxl.sign.lobby";
	public boolean onDungeonInit = true;
	
	public LobbySign(Sign sign, GameWorld gWorld) {
		super(sign, gWorld);
	}
	
	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		
		return true;
	}
	
	@Override
	public void onInit() {
		getGWorld().locLobby = getSign().getLocation();
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
