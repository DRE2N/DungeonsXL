package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import io.github.dre2n.dungeonsxl.util.NumberUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class WaveSign extends DSign {
	
	private DSignType type = DSignTypeDefault.WAVE;
	
	private double mobCountIncreaseRate;
	
	public WaveSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	/**
	 * @return the mobCountIncreaseRate
	 */
	public double getMobCountIncreaseRate() {
		return mobCountIncreaseRate;
	}
	
	/**
	 * @param mobCountIncreaseRate
	 * the mobCountIncreaseRate to set
	 */
	public void setMobCountIncreaseRate(double mobCountIncreaseRate) {
		this.mobCountIncreaseRate = mobCountIncreaseRate;
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@Override
	public void onInit() {
		String[] lines = getSign().getLines();
		if ( !lines[1].equals("")) {
			mobCountIncreaseRate = NumberUtil.parseDouble(lines[1], 2);
		}
		
		if ( !getTriggers().isEmpty()) {
			getSign().getBlock().setType(Material.AIR);
			return;
		}
		
		InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), getGameWorld());
		if (trigger != null) {
			trigger.addListener(this);
			addTrigger(trigger);
		}
		
		getSign().setLine(0, ChatColor.DARK_BLUE + "############");
		getSign().setLine(1, ChatColor.DARK_GREEN + "START");
		getSign().setLine(2, ChatColor.DARK_GREEN + "NEXT WAVE");
		getSign().setLine(3, ChatColor.DARK_BLUE + "############");
		getSign().update();
	}
	
	@Override
	public boolean onPlayerTrigger(Player player) {
		DGroup dGroup = DGroup.getByPlayer(player);
		if (dGroup == null) {
			return true;
		}
		
		if (getGameWorld() == null) {
			return true;
		}
		
		dGroup.finishWave(mobCountIncreaseRate);
		return true;
	}
	
	@Override
	public void onTrigger() {
		for (DGroup dGroup : plugin.getDGroups()) {
			dGroup.finishWave(mobCountIncreaseRate);
		}
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
