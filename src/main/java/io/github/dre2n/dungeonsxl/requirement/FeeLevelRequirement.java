/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package io.github.dre2n.dungeonsxl.requirement;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DPlayerData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class FeeLevelRequirement extends Requirement {

    private RequirementType type = RequirementTypeDefault.FEE_LEVEL;

    private int fee;

    /* Getters and setters */
    /**
     * @return the fee
     */
    public int getFee() {
        return fee;
    }

    /**
     * @param fee
     * the fee to set
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
        return player.getLevel() >= fee;
    }

    @Override
    public void demand(Player player) {
        DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
        if (dPlayer == null) {
            return;
        }

        DPlayerData data = dPlayer.getData();
        data.setOldLevel(data.getOldLevel() - fee);

        MessageUtil.sendMessage(player, DMessage.REQUIREMENT_FEE.getMessage(fee + " levels"));
    }

}
