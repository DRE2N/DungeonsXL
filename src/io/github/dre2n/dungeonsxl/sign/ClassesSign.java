package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.player.DClass;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class ClassesSign extends DSign {
	
	public static String name = "Classes";
	public String buildPermissions = "dxl.sign.classes";
	public boolean onDungeonInit = true;
	
	public ClassesSign(Sign sign, GameWorld gWorld) {
		super(sign, gWorld);
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onInit() {
		if ( !getGWorld().getConfig().isLobbyDisabled()) {
			
			int[] direction = GroupSign.getDirection(getSign().getBlock().getData());
			int directionX = direction[0];
			int directionZ = direction[1];
			
			int xx = 0, zz = 0;
			for (DClass dclass : getGWorld().getConfig().getClasses()) {
				
				// Check existing signs
				boolean isContinued = true;
				for (Sign isusedsign : getGWorld().signClass) {
					if (dclass.getName().equalsIgnoreCase(ChatColor.stripColor(isusedsign.getLine(1)))) {
						isContinued = false;
					}
				}
				
				if (isContinued) {
					Block classBlock = getSign().getBlock().getRelative(xx, 0, zz);
					
					if (classBlock.getData() == getSign().getData().getData() && classBlock.getType() == Material.WALL_SIGN && classBlock.getState() instanceof Sign) {
						Sign classSign = (Sign) classBlock.getState();
						
						classSign.setLine(0, ChatColor.DARK_BLUE + "############");
						classSign.setLine(1, ChatColor.DARK_GREEN + dclass.getName());
						classSign.setLine(2, "");
						classSign.setLine(3, ChatColor.DARK_BLUE + "############");
						classSign.update();
						
						getGWorld().signClass.add(classSign);
					} else {
						break;
					}
					
					xx = xx + directionX;
					zz = zz + directionZ;
				}
			}
		} else {
			getSign().getBlock().setType(Material.AIR);
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
