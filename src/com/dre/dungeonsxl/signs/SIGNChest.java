package com.dre.dungeonsxl.signs;

import org.bukkit.Material;
import org.bukkit.block.Sign;

import com.dre.dungeonsxl.game.GameChest;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNChest extends DSign {

	public static String name = "Chest";
	public String buildPermissions = "dxl.sign.chest";
	public boolean onDungeonInit = false;

	// Variables
	private double moneyReward;

	public SIGNChest(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		String lines[] = sign.getLines();
		if (lines[1].equals("")) {
			return false;
		}
		return true;
	}

	@Override
	public void onInit() {
		String lines[] = sign.getLines();
		if (!lines[1].equals("")) {
			moneyReward = Double.parseDouble(lines[1]);
		}
		for (int i = -1; i <= 1; i++) {
			if (sign.getBlock().getRelative(i, 0, 0).getType() == Material.CHEST) {
				new GameChest(sign.getBlock().getRelative(i, 0, 0), gworld, moneyReward);
			}
			if (sign.getBlock().getRelative(0, 0, i).getType() == Material.CHEST) {
				new GameChest(sign.getBlock().getRelative(0, 0, i), gworld, moneyReward);
			}
			if (sign.getBlock().getRelative(0, i, 0).getType() == Material.CHEST) {
				new GameChest(sign.getBlock().getRelative(0, i, 0), gworld, moneyReward);
			}
		}

		sign.getBlock().setType(Material.AIR);
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
