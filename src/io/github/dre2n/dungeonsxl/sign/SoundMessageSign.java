package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.util.NumberUtil;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SoundMessageSign extends DSign {
	
	private DSignType type = DSignTypeDefault.SOUND_MESSAGE;
	
	// Variables
	private boolean initialized;
	@SuppressWarnings("unused")
	private String msg;
	private CopyOnWriteArrayList<Player> done = new CopyOnWriteArrayList<Player>();
	
	public SoundMessageSign(Sign sign, GameWorld gameWorld) {
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
	public void onTrigger() {
		if (initialized) {
			remove();
		}
	}
	
	@Override
	public boolean onPlayerTrigger(Player player) {
		if (initialized) {
			remove();
			if (done.size() >= getGameWorld().getWorld().getPlayers().size()) {
				remove();
			}
		}
		
		return true;
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
