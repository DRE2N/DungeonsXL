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
package io.github.dre2n.dungeonsxl.world.block;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.util.LegacyUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Daniel Saukel
 */
public class TeamFlag extends TeamBlock {

    public TeamFlag(Block block, DGroup owner) {
        super(block, owner);
        reset();
    }

    /* Actions */
    /**
     * Reset a team flag when the capturer dies.
     */
    public void reset() {
        LegacyUtil.setBlockWoolColor(block, owner.getDColor());
    }

    @Override
    public boolean onBreak(BlockBreakEvent event) {
        Player breaker = event.getPlayer();
        DGamePlayer gamePlayer = DGamePlayer.getByPlayer(breaker);
        if (gamePlayer == null) {
            return true;
        }

        if (owner.getPlayers().contains(breaker)) {
            MessageUtil.sendMessage(breaker, DMessage.ERROR_BLOCK_OWN_TEAM.getMessage());
            return true;
        }

        owner.getGameWorld().sendMessage(DMessage.GROUP_FLAG_STEALING.getMessage(gamePlayer.getName(), owner.getName()));
        gamePlayer.setRobbedGroup(owner);
        event.getBlock().setType(Material.AIR);
        return true;
    }

}
