package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.util.NumberUtil;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class MessageSign extends DSign {
	
	private DSignType type = DSignTypeDefault.MESSAGE;
	
	// Variables
	private String msg;
	private boolean initialized;
	private CopyOnWriteArrayList<Player> done = new CopyOnWriteArrayList<Player>();
	
	public MessageSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
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
			String msg = getGameWorld().getConfig().getMsg(NumberUtil.parseInt(lines[1]), true);
			if (msg != null) {
				this.msg = msg;
				getSign().getBlock().setType(Material.AIR);
			}
		}
		
		initialized = true;
	}
	
	@Override
	public boolean onPlayerTrigger(Player player) {
		if ( !initialized) {
			return true;
		}
		
		if ( !done.contains(player)) {
			MessageUtil.sendMessage(player, msg);
			done.add(player);
		}
		
		if (done.size() >= getGameWorld().getWorld().getPlayers().size()) {
			remove();
		}
		
		return true;
	}
	
	@Override
	public void onTrigger() {
		if (initialized) {
			for (Player player : getGameWorld().getWorld().getPlayers()) {
				MessageUtil.sendMessage(player, msg);
			}
			remove();
		}
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
