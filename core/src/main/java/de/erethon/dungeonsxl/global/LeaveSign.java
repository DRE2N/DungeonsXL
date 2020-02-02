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
package de.erethon.dungeonsxl.global;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.misc.BlockUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.util.LWCUtil;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * A sign to leave a group.
 *
 * @author Frank Baumann
 */
public class LeaveSign extends GlobalProtection {

    public static final String LEAVE_SIGN_TAG = "Leave";

    private Block sign;
    private Set<Block> blocks;

    public LeaveSign(DungeonsXL plugin, int id, Sign sign) {
        super(plugin, sign.getWorld(), id);

        this.sign = sign.getBlock();
        setText();

        LWCUtil.removeProtection(sign.getBlock());
    }

    public LeaveSign(DungeonsXL plugin, World world, int id, ConfigurationSection config) {
        super(plugin, world, id);

        sign = world.getBlockAt(config.getInt("x"), config.getInt("y"), config.getInt("z"));
        setText();

        LWCUtil.removeProtection(sign);
    }

    /* Getters and setters */
    @Override
    public Set<Block> getBlocks() {
        if (blocks == null) {
            blocks = new HashSet<>();

            blocks.add(sign);
            blocks.add(BlockUtil.getAttachedBlock(sign));
        }

        return blocks;
    }

    /* Actions */
    public void setText() {
        BlockState state = sign.getState();
        if (state instanceof Sign) {
            Sign sign = (Sign) state;
            sign.setLine(0, ChatColor.BLUE + "############");
            sign.setLine(1, DMessage.SIGN_LEAVE.getMessage());
            sign.setLine(2, "");
            sign.setLine(3, ChatColor.BLUE + "############");
            sign.update();
        }
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
     * @param plugin the plugin instance
     * @param block  a block which is protected by the returned LeaveSign
     * @return the leave sign the block belongs to, null if it belongs to none
     */
    public static LeaveSign getByBlock(DungeonsXL plugin, Block block) {
        for (GlobalProtection protection : plugin.getGlobalProtectionCache().getProtections(LeaveSign.class)) {
            LeaveSign leaveSign = (LeaveSign) protection;

            if (leaveSign.getBlocks().contains(block)) {
                return leaveSign;
            }
        }

        return null;
    }

}
