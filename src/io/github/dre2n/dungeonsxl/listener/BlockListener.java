package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.dungeon.EditWorld;
import io.github.dre2n.dungeonsxl.game.GamePlaceableBlock;
import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.global.DPortal;
import io.github.dre2n.dungeonsxl.global.GroupSign;
import io.github.dre2n.dungeonsxl.global.LeaveSign;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.task.RedstoneEventTask;
import io.github.dre2n.dungeonsxl.util.NumberUtil;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;

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

public class BlockListener implements Listener {
	
	DungeonsXL plugin = DungeonsXL.getPlugin();
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPhysics(BlockPhysicsEvent event) {
		if (event.getBlock().getType() != Material.PORTAL) {
			return;
		}
		
		if (DPortal.getByBlock(event.getBlock()) != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		// Deny DPortal destroying
		if (block.getType() == Material.PORTAL) {
			DPortal dPortal = DPortal.getByBlock(event.getBlock());
			if (dPortal != null) {
				if (plugin.getInBreakMode().contains(player)) {
					dPortal.delete();
					MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.PLAYER_PROTECTED_BLOCK_DELETED));
					MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.CMD_BREAK_PROTECTED_MODE));
					plugin.getInBreakMode().remove(player);
					
				} else {
					event.setCancelled(true);
				}
				
				return;
			}
		}
		
		// Delete GroupSign
		GroupSign groupSign = GroupSign.getSign(block);
		if (groupSign != null) {
			if (plugin.getInBreakMode().contains(player)) {
				groupSign.delete();
				MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.PLAYER_PROTECTED_BLOCK_DELETED));
				MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.CMD_BREAK_PROTECTED_MODE));
				plugin.getInBreakMode().remove(player);
				
			} else {
				event.setCancelled(true);
			}
			
			return;
		}
		
		// Deny DGSignblocks destroying
		if (GroupSign.isRelativeSign(block, 1, 0) || GroupSign.isRelativeSign(block, -1, 0) || GroupSign.isRelativeSign(block, 0, 1) || GroupSign.isRelativeSign(block, 0, -1)) {
			event.setCancelled(true);
			return;
		}
		
		// Delete LeaveSign
		LeaveSign leaveSign = LeaveSign.getSign(block);
		if (leaveSign != null) {
			if (plugin.getInBreakMode().contains(player)) {
				leaveSign.delete();
				MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.PLAYER_PROTECTED_BLOCK_DELETED));
				MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.CMD_BREAK_PROTECTED_MODE));
				plugin.getInBreakMode().remove(player);
				
			} else {
				event.setCancelled(true);
			}
			
			return;
		}
		
		// Deny LeaveSignblocks destroying
		if (LeaveSign.isRelativeSign(block, 1, 0) || LeaveSign.isRelativeSign(block, -1, 0) || LeaveSign.isRelativeSign(block, 0, 1) || LeaveSign.isRelativeSign(block, 0, -1)) {
			event.setCancelled(true);
			return;
		}
		
		// Editworld Signs
		EditWorld editWorld = EditWorld.getByWorld(block.getWorld());
		if (editWorld != null) {
			editWorld.getSign().remove(event.getBlock());
		}
		
		// Deny GameWorld Blocks
		GameWorld gameWorld = GameWorld.getByWorld(block.getWorld());
		if (gameWorld != null) {
			if (gameWorld.getDSigns().contains(block) || !gameWorld.getConfig().canBuild()) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		
		// Deny GameWorld Blocks
		GameWorld gameWorld = GameWorld.getByWorld(block.getWorld());
		if (gameWorld == null) {
			return;
		}
		
		if (gameWorld.getConfig().canBuild() || GamePlaceableBlock.canBuildHere(block, block.getFace(event.getBlockAgainst()), event.getItemInHand().getType(), gameWorld)) {
			return;
		}
		
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
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		String[] lines = event.getLines();
		EditWorld editWorld = EditWorld.getByWorld(player.getWorld());
		
		// Group Signs
		if (editWorld == null) {
			if ( !player.hasPermission("dxl.sign")) {
				return;
			}
			
			if ( !lines[0].equalsIgnoreCase("[DXL]")) {
				return;
			}
			
			if (lines[1].equalsIgnoreCase("Group")) {
				String dungeonName = lines[2];
				
				String[] data = lines[3].split("\\,");
				if (data.length >= 2 && data.length <= 3) {
					int maxGroups = NumberUtil.parseInt(data[0]);
					int maxPlayersPerGroup = NumberUtil.parseInt(data[1]);
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
			
		} else { // Editworld Signs
			Sign sign = (Sign) block.getState();
			if (sign != null) {
				sign.setLine(0, lines[0]);
				sign.setLine(1, lines[1]);
				sign.setLine(2, lines[2]);
				sign.setLine(3, lines[3]);
				
				DSign dsign = DSign.create(sign, null);
				
				if (dsign == null) {
					return;
				}
				
				if ( !player.hasPermission(dsign.getType().getBuildPermission())) {
					MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.ERROR_NO_PERMISSIONS));
				}
				
				if (dsign.check()) {
					editWorld.checkSign(block);
					editWorld.getSign().add(block);
					MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.PLAYER_SIGN_CREATED));
					
				} else {
					MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.ERROR_SIGN_WRONG_FORMAT));
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSpread(BlockSpreadEvent event) {
		Block block = event.getBlock();
		// Block the Spread off Vines
		if (block.getType() != Material.VINE) {
			return;
		}
		
		// Check GameWorlds
		GameWorld gameWorld = GameWorld.getByWorld(event.getBlock().getWorld());
		if (gameWorld != null) {
			event.setCancelled(true);
		}
		
		// Check EditWorlds
		EditWorld editWorld = EditWorld.getByWorld(event.getBlock().getWorld());
		if (editWorld != null) {
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onRedstoneEvent(BlockRedstoneEvent event) {
		new RedstoneEventTask(event.getBlock()).runTaskLater(plugin, 1);
	}
	
}
