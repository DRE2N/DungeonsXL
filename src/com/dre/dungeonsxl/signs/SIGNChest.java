package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameChest;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNChest extends DSign {

	public static String name = "Chest";
	public String buildPermissions = "dxl.sign.chest";
	public boolean onDungeonInit = false;

	public SIGNChest(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public void onInit() {
		for (int i = -1; i <= 1; i++) {
			if (sign.getBlock().getRelative(i, 0, 0).getTypeId() == 54) {
				new GameChest(sign.getBlock().getRelative(i, 0, 0), gworld);
			}
			if (sign.getBlock().getRelative(0, 0, i).getTypeId() == 54) {
				new GameChest(sign.getBlock().getRelative(0, 0, i), gworld);
			}
			if (sign.getBlock().getRelative(0, i, 0).getTypeId() == 54) {
				new GameChest(sign.getBlock().getRelative(0, i, 0), gworld);
			}
		}

		sign.getBlock().setTypeId(0);
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
