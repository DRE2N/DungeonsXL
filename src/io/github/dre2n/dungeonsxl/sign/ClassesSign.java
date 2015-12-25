package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.player.DClass;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class ClassesSign extends DSign {
	
	private DSignType type = DSignTypeDefault.CLASSES;
	
	public ClassesSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onInit() {
		if (getGameWorld().getConfig().isLobbyDisabled()) {
			getSign().getBlock().setType(Material.AIR);
			return;
		}
		
		int[] direction = GroupSign.getDirection(getSign().getBlock().getData());
		int directionX = direction[0];
		int directionZ = direction[1];
		
		int xx = 0, zz = 0;
		for (DClass dclass : getGameWorld().getConfig().getClasses()) {
			
			// Check existing signs
			boolean isContinued = true;
			for (Sign isusedsign : getGameWorld().getSignClass()) {
				if (dclass.getName().equalsIgnoreCase(ChatColor.stripColor(isusedsign.getLine(1)))) {
					isContinued = false;
				}
			}
			
			if ( !isContinued) {
				continue;
			}
			
			Block classBlock = getSign().getBlock().getRelative(xx, 0, zz);
			
			if (classBlock.getData() == getSign().getData().getData() && classBlock.getType() == Material.WALL_SIGN && classBlock.getState() instanceof Sign) {
				Sign classSign = (Sign) classBlock.getState();
				
				classSign.setLine(0, ChatColor.DARK_BLUE + "############");
				classSign.setLine(1, ChatColor.DARK_GREEN + dclass.getName());
				classSign.setLine(2, "");
				classSign.setLine(3, ChatColor.DARK_BLUE + "############");
				classSign.update();
				
				getGameWorld().getSignClass().add(classSign);
				
			} else {
				break;
			}
			
			xx = xx + directionX;
			zz = zz + directionZ;
		}
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
