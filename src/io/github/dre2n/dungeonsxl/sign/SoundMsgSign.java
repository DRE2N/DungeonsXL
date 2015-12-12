package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SoundMsgSign extends DSign {
	
	public static String name = "SoundMsg";
	public String buildPermissions = "dxl.sign.soundmsg";
	public boolean onDungeonInit = false;
	
	// Variables
	private boolean initialized;
	@SuppressWarnings("unused")
	private String msg;
	private CopyOnWriteArrayList<Player> done = new CopyOnWriteArrayList<Player>();
	
	public SoundMsgSign(Sign sign, GameWorld gWorld) {
		super(sign, gWorld);
	}
	
	@Override
	public boolean check() {
		if (getSign().getLine(1).equals("")) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onInit() {
		String lines[] = getSign().getLines();
		
		if ( !lines[1].equals("")) {
			String msg = getGWorld().getConfig().getMsg(IntegerUtil.parseInt(lines[1]), true);
			if (msg != null) {
				this.msg = msg;
				getSign().getBlock().setType(Material.AIR);
			}
		}
		
		initialized = true;
	}
	
	@Override
	public void onTrigger() {
		if (initialized) {
			remove();
		}
	}
	
	@Override
	public boolean onPlayerTrigger(Player player) {
		if (initialized) {
			remove();
			if (done.size() >= getGWorld().world.getPlayers().size()) {
				remove();
			}
		}
		return true;
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
