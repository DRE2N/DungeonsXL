package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.dungeon.game.GamePlaceableBlock;
import io.github.dre2n.dungeonsxl.dungeon.game.GameWorld;
import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.global.LeaveSign;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.trigger.RedstoneTrigger;
import io.github.dre2n.dungeonsxl.util.IntegerUtil;
import io.github.dre2n.dungeonsxl.util.MessageUtil;

import org.bukkit.Location;
import org.bukkit.Material;
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

public class BlockListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.getBlock().getType() == Material.PORTAL) {
			if (DPortal.get(event.getBlock()) != null) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		
		// Deny DPortal destroying
		if (block.getType() == Material.PORTAL) {
			if (DPortal.get(event.getBlock()) != null) {
				event.setCancelled(true);
			}
		}
		
		// Deny DGSignblocks destroying
		if (GroupSign.isRelativeSign(block, 1, 0) || GroupSign.isRelativeSign(block, -1, 0) || GroupSign.isRelativeSign(block, 0, 1) || GroupSign.isRelativeSign(block, 0, -1)) {
			event.setCancelled(true);
		}
		
		// DGSign destroying
		if (GroupSign.getSign(block) != null) {
			DungeonsXL.getPlugin().getGroupSigns().remove(GroupSign.getSign(block));
		}
		
		// Deny LeaveSignblocks destroying
		if (LeaveSign.isRelativeSign(block, 1, 0) || LeaveSign.isRelativeSign(block, -1, 0) || LeaveSign.isRelativeSign(block, 0, 1) || LeaveSign.isRelativeSign(block, 0, -1)) {
			event.setCancelled(true);
		}
		
		// LeaveSign destroying
		if (LeaveSign.getSign(block) != null) {
			event.setCancelled(true);
			// LeaveSign.lsigns.remove(LeaveSign.getSign(block));
		}
		
		// Editworld Signs
		EditWorld eworld = EditWorld.get(block.getWorld());
		if (eworld != null) {
			eworld.sign.remove(event.getBlock());
		}
		
		// Deny GameWorld Blocks
		GameWorld gworld = GameWorld.get(block.getWorld());
		if (gworld != null) {
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		
		// Deny GameWorld Blocks
		GameWorld gworld = GameWorld.get(block.getWorld());
		if (gworld != null) {
			if ( !GamePlaceableBlock.canBuildHere(block, block.getFace(event.getBlockAgainst()), event.getItemInHand().getType(), gworld)) {
				
				// Workaround for a bug that would allow 3-Block-high jumping
				Location loc = event.getPlayer().getLocation();
				if (loc.getY() > block.getY() + 1.0 && loc.getY() <= block.getY() + 1.5) {
					if (loc.getX() >= block.getX() - 0.3 && loc.getX() <= block.getX() + 1.3) {
						if (loc.getZ() >= block.getZ() - 0.3 && loc.getZ() <= block.getZ() + 1.3) {
							loc.setX(block.getX() + 0.5);
							loc.setY(block.getY());
							loc.setZ(block.getZ() + 0.5);
							event.getPlayer().teleport(loc);
						}
					}
				}
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		String[] lines = event.getLines();
		EditWorld eworld = EditWorld.get(player.getWorld());
		
		// Group Signs
		if (eworld == null) {
			if (player.isOp() || player.hasPermission("dxl.sign")) {
				if (lines[0].equalsIgnoreCase("[DXL]")) {
					if (lines[1].equalsIgnoreCase("Group")) {
						String dungeonName = lines[2];
						
						String[] data = lines[3].split("\\,");
						if (data.length >= 2 && data.length <= 3) {
							int maxGroups = IntegerUtil.parseInt(data[0]);
							int maxPlayersPerGroup = IntegerUtil.parseInt(data[1]);
							boolean multiFloor = false;
							if (data.length == 3) {
								if (data[2].equals("+")) {
									multiFloor = true;
								}
							}
							if (maxGroups > 0 && maxPlayersPerGroup > 0) {
								if (GroupSign.tryToCreate(event.getBlock(), dungeonName, maxGroups, maxPlayersPerGroup, multiFloor) != null) {
									event.setCancelled(true);
								}
							}
						}
					} else if (lines[1].equalsIgnoreCase("Leave")) {
						if (block.getState() instanceof Sign) {
							Sign sign = (Sign) block.getState();
							new LeaveSign(sign);
						}
						event.setCancelled(true);
					}
				}
			}
		} else { // Editworld Signs
			Sign sign = (Sign) block.getState();
			if (sign != null) {
				sign.setLine(0, lines[0]);
				sign.setLine(1, lines[1]);
				sign.setLine(2, lines[2]);
				sign.setLine(3, lines[3]);
				
				DSign dsign = DSign.create(sign, null);
				
				if (dsign != null) {
					if (player.isOp() || player.hasPermission(dsign.getPermissions())) {
						if (dsign.check()) {
							eworld.checkSign(block);
							eworld.sign.add(block);
							MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().get("Player_SignCreated"));
							
						} else {
							MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().get("Error_SignWrongFormat"));
						}
						
					} else {
						MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().get("Error_NoPermissions"));
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockSpread(BlockSpreadEvent event) {
		Block block = event.getBlock();
		// Block the Spread off Vines
		if (block.getType() == Material.VINE) {
			// Check GameWorlds
			GameWorld gworld = GameWorld.get(event.getBlock().getWorld());
			if (gworld != null) {
				event.setCancelled(true);
			}
			
			// Check EditWorlds
			EditWorld eworld = EditWorld.get(event.getBlock().getWorld());
			if (eworld != null) {
				event.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
		new RedstoneEventTask(event.getBlock()).runTaskLater(DungeonsXL.getPlugin(), 1);
	}
	
	public class RedstoneEventTask extends BukkitRunnable {
		private final Block block;
		
		public RedstoneEventTask(final Block block) {
			this.block = block;
		}
		
		@Override
		public void run() {
			for (GameWorld gworld : DungeonsXL.getPlugin().getGameWorlds()) {
				if (block.getWorld() == gworld.world) {
					RedstoneTrigger.updateAll(gworld);
				}
			}
		}
		
	}
	
}
