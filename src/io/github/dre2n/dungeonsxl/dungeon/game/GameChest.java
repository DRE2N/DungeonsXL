package io.github.dre2n.dungeonsxl.dungeon.game;

import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.util.MessageUtil;
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
	public boolean isUsed = false;
	public Chest chest;
	public GameWorld gWorld;
	public double moneyReward;
	
	public GameChest(Block chest, GameWorld gWorld, double moneyReward) {
		if (chest.getState() instanceof Chest) {
			this.chest = (Chest) chest.getState();
			
			this.gWorld = gWorld;
			
			this.moneyReward = moneyReward;
			
			gWorld.gameChests.add(this);
		}
	}
	
	public void addTreasure(DGroup dgroup) {
		if (dgroup != null) {
			for (Player player : dgroup.getPlayers()) {
				DPlayer dplayer = DPlayer.get(player);
				if (dplayer != null) {
					dplayer.treasureMoney = dplayer.treasureMoney + moneyReward;
					String msg = "";
					for (ItemStack istack : chest.getInventory().getContents()) {
						
						if (istack != null) {
							dplayer.treasureInv.addItem(istack);
							String name;
							
							if (istack.hasItemMeta() && istack.getItemMeta().hasDisplayName()) {
								name = istack.getItemMeta().getDisplayName();
								
							} else {
								ItemInfo itemInfo = Items.itemByStack(istack);
								if (itemInfo != null) {
									name = itemInfo.getName();
								} else {
									name = istack.getType().name();
								}
							}
							msg = msg + ChatColor.RED + " " + istack.getAmount() + " " + name + ChatColor.GOLD + ",";
						}
					}
					
					msg = msg.substring(0, msg.length() - 1);
					
					MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().get("Player_LootAdded", msg));
					if (moneyReward != 0) {
						MessageUtil.sendMessage(player, DungeonsXL.getPlugin().getDMessages().get("Player_LootAdded", String.valueOf(moneyReward)));
					}
				}
			}
		}
	}
	
	// Statics
	public static void onOpenInventory(InventoryOpenEvent event) {
		InventoryView inventory = event.getView();
		
		GameWorld gWorld = GameWorld.get(event.getPlayer().getWorld());
		
		if (gWorld != null) {
			if (inventory.getTopInventory().getHolder() instanceof Chest) {
				Chest chest = (Chest) inventory.getTopInventory().getHolder();
				
				for (GameChest gchest : gWorld.gameChests) {
					if (gchest.chest.equals(chest)) {
						
						if ( !gchest.isUsed) {
							if (gchest.chest.getLocation().distance(chest.getLocation()) < 1) {
								gchest.addTreasure(DGroup.get(gWorld));
								gchest.isUsed = true;
								event.setCancelled(true);
							}
							
						} else {
							MessageUtil.sendMessage(DungeonsXL.getPlugin().getServer().getPlayer(event.getPlayer().getUniqueId()), DungeonsXL.getPlugin().getDMessages().get("Error_ChestIsOpened"));
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
}
