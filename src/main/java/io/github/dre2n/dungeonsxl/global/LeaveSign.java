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
package io.github.dre2n.dungeonsxl.global;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.commons.misc.BlockUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessage;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * A sign to leave a group.
 *
 * @author Frank Baumann
 */
public class LeaveSign extends GlobalProtection {

    private Sign sign;
    private Set<Block> blocks;

    public LeaveSign(int id, Sign sign) {
        super(sign.getWorld(), id);

        this.sign = sign;
        setText();
    }

    /* Getters and setters */
    @Override
    public Set<Block> getBlocks() {
        if (blocks == null) {
            blocks = new HashSet<>();

            blocks.add(sign.getBlock());
            blocks.add(BlockUtil.getAttachedBlock(sign.getBlock()));
        }

        return blocks;
    }

    /* Actions */
    public void setText() {
        sign.setLine(0, ChatColor.BLUE + "############");
        sign.setLine(1, ChatColor.DARK_GREEN + "Leave");
        sign.setLine(2, "");
        sign.setLine(3, ChatColor.BLUE + "############");
        sign.update();
    }

    public void onPlayerInteract(Player player) {
        DGamePlayer dplayer = DGamePlayer.getByPlayer(player);

        if (dplayer != null) {
            dplayer.leave();

        } else {
            DGroup group = DGroup.getByPlayer(player);
            if (group != null) {
                group.removePlayer(player);
                MessageUtil.sendMessage(player, DMessage.PLAYER_LEAVE_GROUP.getMessage());
            }
        }
    }

    @Override
    public void save(FileConfiguration config) {
        String preString = "protections.leaveSigns." + sign.getWorld().getName() + "." + getId();
        config.set(preString + ".x", sign.getX());
        config.set(preString + ".y", sign.getY());
        config.set(preString + ".z", sign.getZ());
    }

    /* Statics */
    /**
     * @param block
     * a block which is protected by the returned LeaveSign
     */
    public static LeaveSign getByBlock(Block block) {
        for (GlobalProtection protection : DungeonsXL.getInstance().getGlobalProtections().getProtections(LeaveSign.class)) {
            LeaveSign leaveSign = (LeaveSign) protection;

            if (leaveSign.getBlocks().contains(block)) {
                return leaveSign;
            }
        }

        return null;
    }

}
