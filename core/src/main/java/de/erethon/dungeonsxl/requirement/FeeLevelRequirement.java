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
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.game.GameRuleProvider;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DPlayerData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class FeeLevelRequirement extends Requirement {

    private RequirementType type = RequirementTypeDefault.FEE_LEVEL;

    private int fee;
    private Boolean keepInventory;

    public FeeLevelRequirement(DungeonsXL plugin) {
        super(plugin);
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

    @Override
    public RequirementType getType() {
        return type;
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

        DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
        return dPlayer != null ? dPlayer.getData().getOldLevel() >= fee : true;
    }

    @Override
    public void demand(Player player) {
        if (isKeepInventory(player)) {
            player.setLevel(player.getLevel() - fee);

        } else {
            DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
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

        Game game = Game.getByPlayer(player);
        GameRuleProvider rules = null;
        if (game != null) {
            rules = game.getRules();
        }
        if (rules != null) {
            keepInventory = rules.getKeepInventoryOnEnter();
            return keepInventory;
        }

        keepInventory = GameRuleProvider.DEFAULT_VALUES.getKeepInventoryOnEnter();
        return keepInventory;
    }

}
