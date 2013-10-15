package com.dre.dungeonsxl.game;

import net.milkbowl.vault.item.Items;
import net.milkbowl.vault.item.ItemInfo;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.dre.dungeonsxl.DGroup;
import com.dre.dungeonsxl.DPlayer;
import com.dre.dungeonsxl.P;

public class GameChest {

	// Variables
	public boolean isUsed = false;
	public Chest chest;
	public GameWorld gworld;

	public GameChest(Block chest, GameWorld gworld) {
		if (chest.getState() instanceof Chest) {
			this.chest = (Chest) chest.getState();

			this.gworld = gworld;

			gworld.gchests.add(this);
		}
	}

	public void addTreasure(DGroup dgroup) {
		if (dgroup != null) {
			for (Player player : dgroup.getPlayers()) {
				DPlayer dplayer = DPlayer.get(player);
				if (dplayer != null) {
					String msg = "";
					for (ItemStack istack : this.chest.getInventory().getContents()) {
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

					P.p.msg(player, P.p.language.get("Player_LootAdded", msg));
				}
			}
		}
	}

	// Statics
	public static void onOpenInventory(InventoryOpenEvent event) {
		InventoryView inventory = event.getView();

		GameWorld gworld = GameWorld.get(event.getPlayer().getWorld());

		if (gworld != null) {
			if (inventory.getTopInventory().getHolder() instanceof Chest) {
				Chest chest = (Chest) inventory.getTopInventory().getHolder();

				for (GameChest gchest : gworld.gchests) {
					if (gchest.chest.equals(chest)) {
						if (!gchest.isUsed) {
							if (gchest.chest.getLocation().distance(chest.getLocation()) < 1) {
								gchest.addTreasure(DGroup.get(gworld));
								gchest.isUsed = true;
								event.setCancelled(true);
							}
						} else {
							P.p.msg(P.p.getServer().getPlayer(event.getPlayer().getName()), ChatColor.RED + "Diese Kiste wurde schon geöffnet!");
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

}
