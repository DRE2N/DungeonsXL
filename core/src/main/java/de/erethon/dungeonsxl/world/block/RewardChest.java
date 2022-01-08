/*
 * Copyright (C) 2012-2022 Frank Baumann
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
package de.erethon.dungeonsxl.world.block;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.Reward;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.event.group.GroupCollectRewardEvent;
import de.erethon.dungeonsxl.api.player.GamePlayer;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.reward.ItemReward;
import de.erethon.dungeonsxl.reward.LevelReward;
import de.erethon.dungeonsxl.reward.MoneyReward;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.util.commons.misc.SimpleDateUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class RewardChest extends GameBlock {

    private Economy econ;

    private boolean used = false;
    private double moneyReward;
    private int levelReward;
    private ItemStack[] itemReward;

    public RewardChest(DungeonsXL plugin, Block container, double moneyReward, int levelReward, ItemStack[] itemReward) {
        super(plugin, container);
        econ = plugin.getEconomyProvider();

        this.moneyReward = moneyReward;
        this.levelReward = levelReward;
        this.itemReward = itemReward;
    }

    /**
     * @return if the RewardChest is used
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * @param used set if the chest is used
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * @return the moneyReward
     */
    public double getMoneyReward() {
        return moneyReward;
    }

    /**
     * @param moneyReward the moneyReward to set
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
     * @param levelReward the levelReward to set
     */
    public void setLevelReward(int levelReward) {
        this.levelReward = levelReward;
    }

    /* Actions */
    @Override
    public boolean onBreak(BlockBreakEvent event) {
        return true;
    }

    /**
     * @param opener the player who opens the chest
     */
    public void onOpen(Player opener) {
        if (used) {
            MessageUtil.sendMessage(Bukkit.getPlayer(opener.getUniqueId()), DMessage.ERROR_CHEST_IS_OPENED.getMessage());
            return;
        }

        if (block.getLocation().distance(block.getLocation()) < 1) {
            addTreasure(api.getPlayerGroup(opener), api.getPlayerCache().getGamePlayer(opener));
            used = true;
        }
    }

    public void addTreasure(PlayerGroup group, GamePlayer collector) {
        if (group == null) {
            return;
        }
        group.sendMessage(DMessage.GROUP_REWARD_CHEST.getMessage());

        boolean hasMoneyReward = false;
        boolean hasLevelReward = false;
        boolean hasItemReward = false;

        for (Reward reward : group.getRewards()) {
            if (reward instanceof MoneyReward) {
                hasMoneyReward = true;
                GroupCollectRewardEvent event = new GroupCollectRewardEvent(group, collector, reward);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    ((MoneyReward) reward).addMoney(moneyReward);
                }

            } else if (reward instanceof LevelReward) {
                hasLevelReward = true;
                GroupCollectRewardEvent event = new GroupCollectRewardEvent(group, collector, reward);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    ((LevelReward) reward).addLevels(levelReward);
                }

            } else if (reward instanceof ItemReward) {
                hasItemReward = true;
                GroupCollectRewardEvent event = new GroupCollectRewardEvent(group, collector, reward);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    ((ItemReward) reward).addItems(itemReward);
                }
            }
        }

        Game game = group.getGame();
        if (game == null || game.hasRewards()) {
            if (!hasMoneyReward) {
                MoneyReward reward = new MoneyReward(econ);
                reward.addMoney(moneyReward);
                GroupCollectRewardEvent event = new GroupCollectRewardEvent(group, collector, reward);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    group.getRewards().add(reward);
                }
            }

            if (!hasLevelReward) {
                LevelReward reward = new LevelReward();
                reward.addLevels(levelReward);
                GroupCollectRewardEvent event = new GroupCollectRewardEvent(group, collector, reward);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    group.getRewards().add(reward);
                }
            }

            if (!hasItemReward) {
                ItemReward reward = new ItemReward(api);
                reward.addItems(itemReward);
                GroupCollectRewardEvent event = new GroupCollectRewardEvent(group, collector, reward);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    group.getRewards().add(reward);
                }
            }
        }

        for (Player player : group.getMembers().getOnlinePlayers()) {
            DGamePlayer dPlayer = (DGamePlayer) api.getPlayerCache().getGamePlayer(player);
            if (!dPlayer.canLoot(game.getDungeon())) {
                MessageUtil.sendMessage(player, DMessage.ERROR_NO_REWARDS_TIME.getMessage(SimpleDateUtil.ddMMyyyyhhmm(dPlayer.getTimeNextLoot(game.getDungeon()))));
                continue;
            }

            if (itemReward != null) {
                String msg = "";
                for (ItemStack itemStack : itemReward) {
                    if (itemStack == null) {
                        continue;
                    }
                    String name = null;
                    if (itemStack.hasItemMeta()) {
                        if (itemStack.getItemMeta().hasDisplayName()) {
                            name = itemStack.getItemMeta().getDisplayName();
                        }
                    }
                    if (name == null) {
                        name = VanillaItem.get(itemStack.getType()).getName();
                    }
                    msg += ChatColor.RED + " " + itemStack.getAmount() + " " + name + ChatColor.GOLD + ",";
                }

                if (msg.length() >= 1) {
                    msg = msg.substring(0, msg.length() - 1);
                }

                MessageUtil.sendMessage(player, DMessage.PLAYER_LOOT_ADDED.getMessage(msg));
            }

            if (moneyReward != 0 && econ != null) {
                MessageUtil.sendMessage(player, DMessage.PLAYER_LOOT_ADDED.getMessage(econ.format(moneyReward)));
            }

            if (levelReward != 0) {
                MessageUtil.sendMessage(player, DMessage.PLAYER_LOOT_ADDED.getMessage(levelReward + " levels"));
            }
        }
    }

}
