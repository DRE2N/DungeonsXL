package com.dre.dungeonsxl.signs;

import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.game.GameWorld;

public class SIGNSoundMsg extends DSign{
	
	public static String name = "SoundMsg";
	public String buildPermissions = "dxl.sign.soundmsg";
	public boolean onDungeonInit = false;
	
	//Variables
	private boolean initialized;
	private String msg;
	private CopyOnWriteArrayList<Player> done = new CopyOnWriteArrayList<Player>();
	
	public SIGNSoundMsg(Sign sign, GameWorld gworld) {
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
		
		if(lines[1]!=""&&lines[2]!=""){
			String msg = gworld.config.getMsg(p.parseInt(lines[1]),true);
			if(msg!=null){
				this.msg = msg;
				sign.getBlock().setTypeId(0);
			}
		}
		
		initialized = true;
	}

	@Override
	public void onUpdate(int type,boolean powered) {
		if(initialized){
			setPowered(type,powered);
			if(!isDistanceTrigger()){
				if(isPowered()){
					onTrigger();
				}
			}
		}
	}

	@Override
	public void onTrigger() {
		if(initialized){
			if(P.p.isSpoutEnabled){
				for(Player player : gworld.world.getPlayers()){
					if(!done.contains(player)){
						if(!isDistanceTrigger() || player.getLocation().distance(sign.getLocation()) < getDtDistance()){
							done.add(player);
							SpoutPlayer sPlayer = Spout.getServer().getPlayer(player.getName());
							if(sPlayer.isSpoutCraftEnabled()){
								SpoutManager.getSoundManager().playCustomMusic(P.p, sPlayer, this.msg, false, this.sign.getLocation());
							}
						}
					}
				}
			}
			
			if(done.size() >= gworld.world.getPlayers().size()){
				this.gworld.dSigns.remove(this);
			}
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
