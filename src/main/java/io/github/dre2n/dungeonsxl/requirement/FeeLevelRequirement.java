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
package io.github.dre2n.dungeonsxl.requirement;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.config.DMessages;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class FeeLevelRequirement extends Requirement {

    private RequirementType type = RequirementTypeDefault.FEE_LEVEL;

    private int fee;

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
    public boolean check(Player player) {
        return player.getLevel() >= fee;
    }

    @Override
    public void demand(Player player) {
        player.setLevel(player.getLevel() - fee);
        MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.REQUIREMENT_FEE, fee + " levels"));
    }

    @Override
    public RequirementType getType() {
        return type;
    }

}
