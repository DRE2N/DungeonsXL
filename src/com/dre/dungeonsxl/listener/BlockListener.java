package com.dre.dungeonsxl.listener;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.dre.dungeonsxl.DGSign;
import com.dre.dungeonsxl.DPortal;
import com.dre.dungeonsxl.P;
import com.dre.dungeonsxl.EditWorld;
import com.dre.dungeonsxl.LeaveSign;
import com.dre.dungeonsxl.game.GamePlaceableBlock;
import com.dre.dungeonsxl.game.GameWorld;
import com.dre.dungeonsxl.signs.DSign;

public class BlockListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPhysics(BlockPhysicsEvent event){
		if(event.getBlock().getTypeId()==90){
			if(DPortal.get(event.getBlock())!=null){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event){
		Block block=event.getBlock();
		
		//Deny DPortal destroying 
		if(block.getTypeId()==90){
			if(DPortal.get(event.getBlock())!=null){
				event.setCancelled(true);
			}
		}
		
		//Deny DGSignblocks destroying
		if(DGSign.isRelativeSign(block,1, 0)|| 
			DGSign.isRelativeSign(block,-1, 0)|| 
			DGSign.isRelativeSign(block,0, 1)|| 
			DGSign.isRelativeSign(block,0, -1)
				)
		{
			event.setCancelled(true);
		}
		
		//DGSign destroying
		if(DGSign.getSign(block)!=null){
			DGSign.dgsigns.remove(DGSign.getSign(block));
		}
		
		//Deny LeaveSignblocks destroying
		if(LeaveSign.isRelativeSign(block,1, 0)|| 
				LeaveSign.isRelativeSign(block,-1, 0)|| 
				LeaveSign.isRelativeSign(block,0, 1)|| 
				LeaveSign.isRelativeSign(block,0, -1)
				)
		{
			event.setCancelled(true);
		}
		
		//LeaveSign destroying
		if(LeaveSign.getSign(block)!=null){
			event.setCancelled(true);
			//LeaveSign.lsigns.remove(LeaveSign.getSign(block));
		}
		
		//Editworld Signs
		EditWorld eworld = EditWorld.get(block.getWorld());
		if(eworld != null){
			eworld.sign.remove(event.getBlock());
		}
		
		//Deny GameWorld Blocks
		GameWorld gworld=GameWorld.get(block.getWorld());
		if(gworld!=null){
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event){
		Block block=event.getBlock();
		
		
		
		//Deny GameWorld Blocks
		GameWorld gworld=GameWorld.get(block.getWorld());
		if(gworld!=null){
			if(!GamePlaceableBlock.canBuildHere(block, block.getFace(event.getBlockAgainst()), event.getItemInHand().getType(), gworld)){
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event){
		Player player = event.getPlayer();
		Block block = event.getBlock();
		String[] lines = event.getLines();
		EditWorld eworld = EditWorld.get(player.getWorld());
		
		//Group Signs
		if (eworld == null) {
			if (player.isOp() || P.p.permission.has(player, "dxl.sign")) {
				if (lines[0].equalsIgnoreCase("[DXL]")) {
					if (lines[1].equalsIgnoreCase("Group")) {
						String dungeonName = lines[2];
						
						String[] data = lines[3].split("\\,");
						if (data.length == 2) {
							int maxGroups = P.p.parseInt(data[0]);
							int maxPlayersPerGroup = P.p.parseInt(data[1]);
							if (maxGroups > 0 && maxPlayersPerGroup > 0) {
								if (DGSign.tryToCreate(event.getBlock(), dungeonName, maxGroups, maxPlayersPerGroup) != null) {
									event.setCancelled(true);
								}
							}
						}
					} else if(lines[1].equalsIgnoreCase("Leave")) {
						if (block.getState() instanceof Sign) {
							Sign sign = (Sign) block.getState();
							new LeaveSign(sign);
						}
						event.setCancelled(true);
					}
				}
			}
		} else { //Editworld Signs
			Sign sign = (Sign) block.getState();
			if(sign != null){
				sign.setLine(0, lines[0]);
				sign.setLine(1, lines[1]);
				sign.setLine(2, lines[2]);
				sign.setLine(3, lines[3]);
				
				DSign dsign = DSign.create(sign, null);
				
				if (dsign != null) {
					if (player.isOp() || P.p.permission.has(player, dsign.getPermissions())) {
						if (dsign.check()) {
							eworld.checkSign(block);
							eworld.sign.add(block);
							P.p.msg(player, P.p.language.get("Player_SignCreated"));
						} else {
							P.p.msg(player, P.p.language.get("Error_SignWrongFormat"));
						}
					} else {
						P.p.msg(player, P.p.language.get("Error_NoPermissions"));
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockSpread(BlockSpreadEvent event){
		Block block=event.getBlock();
		//Block the Spread off Vines
		if(block.getTypeId()==106){
			//Check GameWorlds
			GameWorld gworld=GameWorld.get(event.getBlock().getWorld());
			if(gworld!=null){
				event.setCancelled(true);
			}
			
			//Check EditWorlds
			EditWorld eworld=EditWorld.get(event.getBlock().getWorld());
			if(eworld!=null){
				event.setCancelled(true);
			}
		}
		
	}
	 
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockRedstoneEvent(BlockRedstoneEvent event){
		new RedstoneEventTask(event).runTaskLater(P.p, 1);
	}
	
	public class RedstoneEventTask extends BukkitRunnable  {
	    private final BlockRedstoneEvent event;
	 
	    public RedstoneEventTask(BlockRedstoneEvent event) {
	        this.event = event;
	    }
	 
	    public void run() {
	    	for(GameWorld gworld : GameWorld.gworlds){
				if(event.getBlock().getWorld() == gworld.world){
					for(DSign sign : gworld.dSigns){
						if(sign!=null){
							if(sign.isRedstoneTrigger()){
								if(sign.getRtBlock().isBlockPowered()){
									sign.onUpdate(0,true);
								}else{
									sign.onUpdate(0,false);
								}
							}
						}
					}
				}
			}
	    }
	 
	}
	
	
}
