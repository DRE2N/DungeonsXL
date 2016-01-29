package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.game.GameWorld;

import org.bukkit.Material;
import org.bukkit.block.Sign;

public class StartSign extends DSign {
	
	private DSignType type = DSignTypeDefault.START;
	
	public StartSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@Override
	public void onInit() {
		getGameWorld().setLocStart(getSign().getLocation());
		getSign().getBlock().setType(Material.AIR);
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
