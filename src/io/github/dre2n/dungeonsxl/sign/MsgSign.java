package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class MsgSign extends DSign {
	
	public static String name = "Msg";
	public String buildPermissions = "dxl.sign.msg";
	public boolean onDungeonInit = false;
	
	// Variables
	private String msg;
	private boolean initialized;
	private CopyOnWriteArrayList<Player> done = new CopyOnWriteArrayList<Player>();
	
	public MsgSign(Sign sign, GameWorld gWorld) {
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
	public boolean onPlayerTrigger(Player player) {
		if (initialized) {
			if ( !done.contains(player)) {
				MessageUtil.sendMessage(player, msg);
				done.add(player);
			}
			if (done.size() >= getGWorld().world.getPlayers().size()) {
				remove();
			}
		}
		return true;
	}
	
	@Override
	public void onTrigger() {
		if (initialized) {
			for (Player player : getGWorld().world.getPlayers()) {
				MessageUtil.sendMessage(player, msg);
			}
			remove();
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
