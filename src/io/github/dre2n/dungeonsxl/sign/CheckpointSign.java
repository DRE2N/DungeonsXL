package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

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
		
		for (DPlayer dplayer : DPlayer.getByWorld(getGameWorld().getWorld())) {
			dplayer.setCheckpoint(getSign().getLocation());
			MessageUtil.sendMessage(dplayer.getPlayer(), plugin.getDMessages().getMessage(Messages.PLAYER_CHECKPOINT_REACHED));
		}
		
		remove();
	}
	
	@Override
	public boolean onPlayerTrigger(Player player) {
		if ( !initialized) {
			return true;
		}
		
		DPlayer dplayer = DPlayer.getByPlayer(player);
		if (dplayer != null) {
			if ( !done.contains(dplayer)) {
				done.add(dplayer);
				dplayer.setCheckpoint(getSign().getLocation());
				MessageUtil.sendMessage(player, plugin.getDMessages().getMessage(Messages.PLAYER_CHECKPOINT_REACHED));
			}
		}
		
		if (done.size() >= DPlayer.getByWorld(getGameWorld().getWorld()).size()) {
			remove();
		}
		
		return true;
	}
	
	@Override
	public DSignType getType() {
		return type;
	}
	
}
