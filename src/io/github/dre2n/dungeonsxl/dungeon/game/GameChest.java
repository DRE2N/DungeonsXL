package io.github.dre2n.dungeonsxl.dungeon.game;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.file.DMessages.Messages;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.messageutil.MessageUtil;
import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class GameChest {
	
	// Variables
	private boolean used = false;
	private Chest chest;
	private GameWorld gameWorld;
	private double moneyReward;
	
	public GameChest(Block chest, GameWorld gameWorld, double moneyReward) {
		if ( !(chest.getState() instanceof Chest)) {
			return;
		}
		
		this.chest = (Chest) chest.getState();
		
		this.gameWorld = gameWorld;
		
		this.moneyReward = moneyReward;
		
		gameWorld.getGameChests().add(this);
	}
	
	public void addTreasure(DGroup dGroup) {
		if (dGroup == null) {
			return;
		}
		
		for (Player player : dGroup.getPlayers()) {
			DPlayer dPlayer = DPlayer.getByPlayer(player);
			if (dPlayer == null) {
				continue;
			}
			
			dPlayer.setTreasureMoney(dPlayer.getTreasureMoney() + moneyReward);
			String msg = "";
			for (ItemStack itemStack : chest.getInventory().getContents()) {
				
				if (itemStack == null) {
					continue;
				}
				
				dPlayer.getTreasureInv().addItem(itemStack);
				String name;
				
				if ( !itemStack.hasItemMeta()) {
					continue;
				}
				
				if (itemStack.getItemMeta().hasDisplayName()) {
					name = itemStack.getItemMeta().getDisplayName();
					
				} else {
					ItemInfo itemInfo = Items.itemByStack(itemStack);
					if (itemInfo != null) {
						name = itemInfo.getName();
					} else {
						name = itemStack.getType().name();
					}
				}
				
				msg = msg + ChatColor.RED + " " + itemStack.getAmount() + " " + name + ChatColor.GOLD + ",";
			}
			
			msg = msg.substring(0, msg.length() - 1);
			
			MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().getMessage(Messages.PLAYER_LOOT_ADDED, msg));
			if (moneyReward != 0) {
				MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().getMessage(Messages.PLAYER_LOOT_ADDED, String.valueOf(moneyReward)));
			}
		}
	}
	
	// Statics
	public static void onOpenInventory(InventoryOpenEvent event) {
		InventoryView inventory = event.getView();
		
		GameWorld gameWorld = GameWorld.getByWorld(event.getPlayer().getWorld());
		
		if (gameWorld == null) {
			return;
		}
		
		if (inventory.getTopInventory().getHolder() instanceof Chest) {
			return;
		}
		
		Chest chest = (Chest) inventory.getTopInventory().getHolder();
		
		for (GameChest gameChest : gameWorld.getGameChests()) {
			if ( !gameChest.chest.equals(chest)) {
				continue;
			}
			
			if ( !gameChest.used) {
				MessageUtil
				        .sendMessage(DungeonsXL.getPlugin().getServer().getPlayer(event.getPlayer().getUniqueId()), DungeonsXL.getPlugin().getDMessages().getMessage(Messages.ERROR_CHEST_IS_OPENED));
				event.setCancelled(true);
				continue;
			}
			
			if (gameChest.chest.getLocation().distance(chest.getLocation()) < 1) {
				gameChest.addTreasure(DGroup.getByGameWorld(gameWorld));
				gameChest.used = true;
				event.setCancelled(true);
			}
		}
	}
	
	/**
	 * @return if the GameChest is used
	 */
	public boolean isUsed() {
		return used;
	}
	
	/**
	 * @param used
	 * set if the chest is used
	 */
	public void setUsed(boolean used) {
		this.used = used;
	}
	
	/**
	 * @return the chest
	 */
	public Chest getChest() {
		return chest;
	}
	
	/**
	 * @param chest
	 * the chest to set
	 */
	public void setChest(Chest chest) {
		this.chest = chest;
	}
	
	/**
	 * @return the gameWorld
	 */
	public GameWorld getGameWorld() {
		return gameWorld;
	}
	
	/**
	 * @param gameWorld
	 * the gameWorld to set
	 */
	public void setGameWorld(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
	}
	
	/**
	 * @return the moneyReward
	 */
	public double getMoneyReward() {
		return moneyReward;
	}
	
	/**
	 * @param moneyReward
	 * the moneyReward to set
	 */
	public void setMoneyReward(double moneyReward) {
		this.moneyReward = moneyReward;
	}
	
}
