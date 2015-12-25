package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class CheckpointSign extends DSign {
	
	private DSignType type = DSignTypeDefault.CHECKPOINT;
	
	// Variables
	private boolean initialized;
	private CopyOnWriteArrayList<DPlayer> done = new CopyOnWriteArrayList<DPlayer>();
	
	public CheckpointSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}
	
	@Override
	public boolean check() {
		return true;
	}
	
	@Override
	public void onInit() {
		getSign().getBlock().setType(Material.AIR);
		
		initialized = true;
	}
	
	@Override
	public void onTrigger() {
		if ( !initialized) {
			return;
		}
		
		for (DPlayer dplayer : DPlayer.get(getGameWorld().getWorld())) {
			dplayer.setCheckpoint(getSign().getLocation());
			MessageUtil.sendMessage(dplayer.getPlayer(), DungeonsXL.getPlugin().getDMessages().get("Player_CheckpointReached"));
		}
		
		remove();
	}
	
	@Override
	public boolean onPlayerTrigger(Player player) {
		if ( !initialized) {
			return true;
		}
		
		DPlayer dplayer = DPlayer.get(player);
		if (dplayer != null) {
			if ( !done.contains(dplayer)) {
				done.add(dplayer);
				dplayer.setCheckpoint(getSign().getLocation());
				MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().get("Player_CheckpointReached"));
			}
		}
		
		if (done.size() >= DPlayer.get(getGameWorld().getWorld()).size()) {
			remove();
		}
		
		return true;
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
