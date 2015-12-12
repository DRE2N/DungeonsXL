package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;

import org.bukkit.Material;
import org.bukkit.block.Sign;

public class BlockSign extends DSign {
	
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
	
	public BlockSign(Sign sign, GameWorld gworld) {
		super(sign, gworld);
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onInit() {
		String lines[] = getSign().getLines();
		if ( !lines[1].equals("")) {
			String line1[] = lines[1].split(",");
			Material offBlock = Material.matchMaterial(line1[0]);
			if (offBlock != null) {
				offBlockId = offBlock.getId();
			} else {
				offBlockId = IntegerUtil.parseInt(line1[0]);
			}
			if (line1.length > 1) {
				offBlockData = (byte) IntegerUtil.parseInt(line1[1]);
			}
		}
		
		if ( !lines[2].equals("")) {
			String line2[] = lines[2].split(",");
			Material onBlock = Material.matchMaterial(line2[0]);
			if (onBlock != null) {
				onBlockId = onBlock.getId();
			} else {
				onBlockId = IntegerUtil.parseInt(line2[0]);
			}
			if (line2.length > 1) {
				onBlockData = (byte) IntegerUtil.parseInt(line2[1]);
			}
		}
		
		getSign().getBlock().setTypeIdAndData(offBlockId, offBlockData, true);
		initialized = true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onTrigger() {
		if (initialized && !active) {
			getSign().getBlock().setTypeIdAndData(onBlockId, onBlockData, true);
			active = true;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDisable() {
		if (initialized && active) {
			getSign().getBlock().setTypeIdAndData(offBlockId, offBlockData, true);
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
