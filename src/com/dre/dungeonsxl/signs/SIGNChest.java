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
		if (sign.getTypeId() == 63) {
			for (int x = -1; x <= 1; x++) {
				if (sign.getBlock().getRelative(x, 0, 0).getTypeId() == 54) {
					new GameChest(sign.getBlock().getRelative(x, 0, 0), gworld);
				}
			}

			for (int z = -1; z <= 1; z++) {
				if (sign.getBlock().getRelative(0, 0, z).getTypeId() == 54) {
					if (sign.getBlock().getRelative(0, 0, z) != null) {
						new GameChest(sign.getBlock().getRelative(0, 0, z), gworld);
					}
				}
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
