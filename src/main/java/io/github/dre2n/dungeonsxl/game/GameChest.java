/*
 * Copyright (C) 2012-2016 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.dungeonsxl.game;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.MessageConfig.Messages;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.reward.LevelReward;
import io.github.dre2n.dungeonsxl.reward.MoneyReward;
import io.github.dre2n.dungeonsxl.reward.Reward;
import io.github.dre2n.dungeonsxl.reward.RewardTypeDefault;
import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class GameChest {

    protected static DungeonsXL plugin = DungeonsXL.getInstance();

    // Variables
    private boolean used = false;
    private Chest chest;
    private GameWorld gameWorld;
    private double moneyReward;
    private int levelReward;

    public GameChest(Block chest, GameWorld gameWorld, double moneyReward, int levelReward) {
        if (!(chest.getState() instanceof Chest)) {
            return;
        }

        this.chest = (Chest) chest.getState();
        this.gameWorld = gameWorld;
        this.moneyReward = moneyReward;
        this.levelReward = levelReward;

        gameWorld.getGameChests().add(this);
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

    /**
     * @return the levelReward
     */
    public double getLevelReward() {
        return levelReward;
    }

    /**
     * @param levelReward
     * the levelReward to set
     */
    public void setLevelReward(int levelReward) {
        this.levelReward = levelReward;
    }

    public void addTreasure(DGroup dGroup) {
        if (dGroup == null) {
            return;
        }

        boolean hasMoneyReward = false;
        boolean hasLevelReward = false;

        for (Reward reward : dGroup.getRewards()) {
            if (reward instanceof MoneyReward) {
                hasMoneyReward = true;
                ((MoneyReward) reward).addMoney(moneyReward);
            } else if (reward instanceof LevelReward) {
                hasLevelReward = true;
                ((LevelReward) reward).addLevels(levelReward);
            }
        }

        if (!hasMoneyReward) {
            Reward reward = Reward.create(RewardTypeDefault.MONEY);
            ((MoneyReward) reward).addMoney(moneyReward);
            dGroup.addReward(reward);
        }

        if (!hasLevelReward) {
            Reward reward = Reward.create(RewardTypeDefault.LEVEL);
            ((LevelReward) reward).addLevels(levelReward);
            dGroup.addReward(reward);
        }

        for (Player player : dGroup.getPlayers()) {
            DPlayer dPlayer = DPlayer.getByPlayer(player);
            if (dPlayer == null) {
                continue;
            }

            String msg = "";
            for (ItemStack itemStack : chest.getInventory().getContents()) {

                if (itemStack == null) {
                    continue;
                }

                dPlayer.getTreasureInv().addItem(itemStack);
                String name = null;

                if (itemStack.hasItemMeta()) {
                    if (itemStack.getItemMeta().hasDisplayName()) {
                        name = itemStack.getItemMeta().getDisplayName();
                    }

                }

                if (name == null && Bukkit.getPluginManager().getPlugin("Vault") != null) {
                    ItemInfo itemInfo = Items.itemByStack(itemStack);
                    if (itemInfo != null) {
                        name = itemInfo.getName();
                    } else {
                        name = itemStack.getType().name();
                    }
                }

                msg += ChatColor.RED + " " + itemStack.getAmount() + " " + name + ChatColor.GOLD + ",";
            }

            if (msg.length() >= 1) {
                msg = msg.substring(0, msg.length() - 1);
            }

            MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.PLAYER_LOOT_ADDED, msg));
            if (moneyReward != 0) {
                MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.PLAYER_LOOT_ADDED, plugin.getEconomyProvider().format(moneyReward)));
            }
            if (levelReward != 0) {
                MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(Messages.PLAYER_LOOT_ADDED, levelReward + " levels"));
            }
        }
    }

    /* Statics */
    /**
     * @param event
     * event.getPlayer() has to be a Player
     */
    public static void onOpenInventory(InventoryOpenEvent event) {
        InventoryView inventory = event.getView();

        GameWorld gameWorld = GameWorld.getByWorld(event.getPlayer().getWorld());

        if (gameWorld == null) {
            return;
        }

        if (!(inventory.getTopInventory().getHolder() instanceof Chest)) {
            return;
        }

        Chest chest = (Chest) inventory.getTopInventory().getHolder();

        for (GameChest gameChest : gameWorld.getGameChests()) {
            if (!gameChest.chest.equals(chest)) {
                continue;
            }

            if (gameChest.used) {
                MessageUtil.sendMessage(plugin.getServer().getPlayer(event.getPlayer().getUniqueId()), plugin.getMessageConfig().getMessage(Messages.ERROR_CHEST_IS_OPENED));
                event.setCancelled(true);
                continue;
            }

            if (gameChest.chest.getLocation().distance(chest.getLocation()) < 1) {
                gameChest.addTreasure(DGroup.getByPlayer((Player) event.getPlayer()));
                gameChest.used = true;
                event.setCancelled(true);
            }
        }
    }

}
