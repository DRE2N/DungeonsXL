/*
 * Copyright (C) 2012-2020 Frank Baumann
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
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.misc.SimpleDateUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.game.GameTypeDefault;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.reward.ItemReward;
import de.erethon.dungeonsxl.reward.LevelReward;
import de.erethon.dungeonsxl.reward.MoneyReward;
import de.erethon.dungeonsxl.reward.Reward;
import de.erethon.dungeonsxl.reward.RewardTypeDefault;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class RewardChest extends GameBlock {

    // Variables
    private boolean used = false;
    private Container container;
    private double moneyReward;
    private int levelReward;
    private ItemStack[] itemReward;

    public RewardChest(DungeonsXL plugin, Block container, double moneyReward, int levelReward, ItemStack[] itemReward) {
        super(plugin, container);

        if (!(container.getState() instanceof Container)) {
            return;
        }

        this.container = (Container) container.getState();
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
     * @return the chest
     */
    public Container getContainer() {
        return container;
    }

    /**
     * @param container the container to set
     */
    public void setContainer(Container container) {
        this.container = container;
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

        if (container.getLocation().distance(container.getLocation()) < 1) {
            addTreasure(DGroup.getByPlayer(opener));
            used = true;
        }
    }

    public void addTreasure(DGroup dGroup) {
        if (dGroup == null) {
            return;
        }
        dGroup.sendMessage(DMessage.GROUP_REWARD_CHEST.getMessage());

        boolean hasMoneyReward = false;
        boolean hasLevelReward = false;
        boolean hasItemReward = false;

        for (Reward reward : dGroup.getRewards()) {
            if (reward.getType() == RewardTypeDefault.MONEY) {
                hasMoneyReward = true;
                ((MoneyReward) reward).addMoney(moneyReward);

            } else if (reward.getType() == RewardTypeDefault.LEVEL) {
                hasLevelReward = true;
                ((LevelReward) reward).addLevels(levelReward);

            } else if (reward.getType() == RewardTypeDefault.ITEM) {
                hasItemReward = true;
                ((ItemReward) reward).addItems(itemReward);
            }
        }

        Game game = Game.getByDGroup(dGroup);
        if (game == null || game.getType() == GameTypeDefault.CUSTOM || game.getType().hasRewards()) {
            if (!hasMoneyReward) {
                Reward reward = Reward.create(plugin, RewardTypeDefault.MONEY);
                ((MoneyReward) reward).addMoney(moneyReward);
                dGroup.addReward(reward);
            }

            if (!hasLevelReward) {
                Reward reward = Reward.create(plugin, RewardTypeDefault.LEVEL);
                ((LevelReward) reward).addLevels(levelReward);
                dGroup.addReward(reward);
            }

            if (!hasItemReward) {
                Reward reward = Reward.create(plugin, RewardTypeDefault.ITEM);
                ((ItemReward) reward).addItems(itemReward);
                dGroup.addReward(reward);
            }
        }

        for (Player player : dGroup.getPlayers().getOnlinePlayers()) {
            DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
            if (dPlayer == null || !dPlayer.canLoot(game.getRules())) {
                MessageUtil.sendMessage(player, DMessage.ERROR_NO_REWARDS_TIME.getMessage(SimpleDateUtil.ddMMyyyyhhmm(dPlayer.getTimeNextLoot(game.getRules()))));
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
                        name = VanillaItem.get(itemStack.getType()).getName().replace("_", " ");
                    }
                    msg += ChatColor.RED + " " + itemStack.getAmount() + " " + name + ChatColor.GOLD + ",";
                }

                if (msg.length() >= 1) {
                    msg = msg.substring(0, msg.length() - 1);
                }

                MessageUtil.sendMessage(player, DMessage.PLAYER_LOOT_ADDED.getMessage(msg));
            }

            if (moneyReward != 0 && plugin.getEconomyProvider() != null) {
                MessageUtil.sendMessage(player, DMessage.PLAYER_LOOT_ADDED.getMessage(plugin.getEconomyProvider().format(moneyReward)));
            }

            if (levelReward != 0) {
                MessageUtil.sendMessage(player, DMessage.PLAYER_LOOT_ADDED.getMessage(levelReward + " levels"));
            }
        }
    }

}
