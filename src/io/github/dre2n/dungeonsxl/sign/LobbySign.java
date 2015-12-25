package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;

import org.bukkit.Material;
import org.bukkit.block.Sign;

public class LobbySign extends DSign {
	
	private DSignType type = DSignTypeDefault.LOBBY;
	
	public LobbySign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@Override
	public void onInit() {
		getGameWorld().setLocLobby(getSign().getLocation());
		getSign().getBlock().setType(Material.AIR);
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
