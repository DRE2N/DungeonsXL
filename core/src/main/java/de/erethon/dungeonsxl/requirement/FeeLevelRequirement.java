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
package de.erethon.dungeonsxl.requirement;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.api.dungeon.Game;
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.dungeon.GameRuleContainer;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DPlayerData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class FeeLevelRequirement implements Requirement {

    private DungeonsAPI api;

    private int fee;
    private Boolean keepInventory;

    public FeeLevelRequirement(DungeonsAPI api) {
        this.api = api;
    }

    /* Getters and setters */
    /**
     * @return the fee
     */
    public int getFee() {
        return fee;
    }

    /**
     * @param fee the fee to set
     */
    public void setFee(int fee) {
        this.fee = fee;
    }

    /* Actions */
    @Override
    public void setup(ConfigurationSection config) {
        fee = config.getInt("feeLevel");
    }

    @Override
    public boolean check(Player player) {
        if (isKeepInventory(player)) {
            return player.getLevel() >= fee;
        }

        DGamePlayer dPlayer = (DGamePlayer) api.getPlayerCache().getGamePlayer(player);
        return dPlayer != null ? dPlayer.getData().getOldLevel() >= fee : true;
    }

    @Override
    public void demand(Player player) {
        if (isKeepInventory(player)) {
            player.setLevel(player.getLevel() - fee);

        } else {
            DGamePlayer dPlayer = (DGamePlayer) api.getPlayerCache().getGamePlayer(player);
            if (dPlayer == null) {
                return;
            }
            DPlayerData data = dPlayer.getData();
            data.setOldLevel(data.getOldLevel() - fee);
        }

        MessageUtil.sendMessage(player, DMessage.REQUIREMENT_FEE.getMessage(fee + " levels"));
    }

    private boolean isKeepInventory(Player player) {
        if (keepInventory != null) {
            return keepInventory;
        }

        Game game = api.getGame(player);
        GameRuleContainer rules = null;
        if (game != null) {
            rules = game.getRules();
        }
        if (rules != null) {
            keepInventory = rules.getState(GameRule.KEEP_INVENTORY_ON_ENTER);
            return keepInventory;
        }

        keepInventory = GameRuleContainer.DEFAULT_VALUES.getState(GameRule.KEEP_INVENTORY_ON_ENTER);
        return keepInventory;
    }

}
