package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;
import org.bukkit.Material;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNBlock extends DSign {

	public static String name = "Block";
	public String buildPermissions = "dxl.sign.block";
	public boolean onDungeonInit = false;

	// Variables
	private boolean initialized;
	private boolean active;
	private int offBlockId = 0;
	private int onBlockId = 0;
	private byte offBlockData = 0x0;
	private byte onBlockData = 0x0;

	public SIGNBlock(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public void onInit() {
		String lines[] = sign.getLines();
		if (!lines[1].equals("")) {
			String line1[] = lines[1].split(",");
			Material offBlock = Material.matchMaterial(line1[0]);
			if (offBlock != null) {
				offBlockId = offBlock.getId();
			} else {
				offBlockId = p.parseInt(line1[0]);
			}
			if (line1.length > 1) {
				offBlockData = (byte) p.parseInt(line1[1]);
			}
		}

		if (!lines[2].equals("")) {
			String line2[] = lines[2].split(",");
			Material onBlock = Material.matchMaterial(line2[0]);
			if (onBlock != null) {
				onBlockId = onBlock.getId();
			} else {
				onBlockId = p.parseInt(line2[0]);
			}
			if (line2.length > 1) {
				onBlockData = (byte) p.parseInt(line2[1]);
			}
		}

		sign.getBlock().setTypeIdAndData(offBlockId, offBlockData, true);
		initialized = true;
	}

	@Override
	public void onTrigger() {
		if (initialized && !active) {
			sign.getBlock().setTypeIdAndData(onBlockId, onBlockData, true);
			active = true;
		}
	}

	@Override
	public void onDisable() {
		if (initialized && active) {
			sign.getBlock().setTypeIdAndData(offBlockId, offBlockData, true);
			active = false;
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
