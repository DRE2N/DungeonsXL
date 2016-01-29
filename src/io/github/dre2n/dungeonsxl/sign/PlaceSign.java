package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.game.GamePlaceableBlock;
import io.github.dre2n.dungeonsxl.game.GameWorld;

import org.bukkit.Material;
import org.bukkit.block.Sign;

public class PlaceSign extends DSign {
	
	private DSignType type = DSignTypeDefault.PLACE;
	
	public PlaceSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@Override
	public void onInit() {
		String lines[] = getSign().getLines();
		getGameWorld().getPlaceableBlocks().add(new GamePlaceableBlock(getSign().getBlock(), lines[1], lines[2]));
		getSign().getBlock().setType(Material.AIR);
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
