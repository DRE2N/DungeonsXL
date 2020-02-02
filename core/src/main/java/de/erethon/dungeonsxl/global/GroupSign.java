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

import de.erethon.caliburn.category.Category;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGroup;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

/**
 * A sign to form a group and to define its dungeon.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class GroupSign extends JoinSign {

    public static final String GROUP_SIGN_TAG = "Group";

    private String groupName;
    private DGroup group;

    public GroupSign(DungeonsXL plugin, int id, Block startSign, String identifier, int maxPlayersPerGroup, int startIfElementsAtLeast, String groupName) {
        super(plugin, id, startSign, identifier, maxPlayersPerGroup, startIfElementsAtLeast);
        this.groupName = groupName;
    }

    public GroupSign(DungeonsXL plugin, World world, int id, ConfigurationSection config) {
        super(plugin, world, id, config);
        groupName = config.getString("groupName");
    }

    /**
     * @return the attached group
     */
    public DGroup getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(DGroup group) {
        this.group = group;
    }

    /**
     * Update this group sign to show the group(s) correctly.
     */
    @Override
    public void update() {
        if (!(startSign.getState() instanceof Sign)) {
            return;
        }

        super.update();
        Sign sign = (Sign) startSign.getState();

        if (group == null) {
            loadedWorld = false;
            sign.setLine(0, DMessage.SIGN_GLOBAL_NEW_GROUP.getMessage());
            sign.update();
            return;
        }

        if (group.getPlayers().size() >= startIfElementsAtLeast && startIfElementsAtLeast != -1 && !loadedWorld) {
            loadedWorld = true;
            group.teleport();
        }

        if (group.isPlaying()) {
            sign.setLine(0, DMessage.SIGN_GLOBAL_IS_PLAYING.getMessage());

        } else if (group.getPlayers().size() >= maxElements) {
            sign.setLine(0, DMessage.SIGN_GLOBAL_FULL.getMessage());

        } else {
            sign.setLine(0, DMessage.SIGN_GLOBAL_JOIN_GROUP.getMessage());
        }

        int j = 1;
        Sign rowSign = sign;

        for (Player player : group.getPlayers().getOnlinePlayers()) {
            if (j > 3) {
                j = 0;
                rowSign = (Sign) sign.getBlock().getRelative(0, -1, 0).getState();
            }

            if (rowSign != null) {
                rowSign.setLine(j, player.getName());
            }

            j++;
            rowSign.update();
        }

        sign.update();
    }

    @Override
    public String getDataPath() {
        return "protections.groupSigns";
    }

    @Override
    public void save(FileConfiguration config) {
        super.save(config);
        String preString = getDataPath() + "." + getWorld().getName() + "." + getId();
        config.set(preString + ".groupName", groupName);
    }

    public void onPlayerInteract(Block block, Player player) {
        if (DGroup.getByPlayer(player) != null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_GROUP.getMessage());
            return;
        }

        Block topBlock = block.getRelative(0, startSign.getY() - block.getY(), 0);
        if (!(topBlock.getState() instanceof Sign)) {
            return;
        }

        Sign topSign = (Sign) topBlock.getState();

        if (topSign.getLine(0).equals(DMessage.SIGN_GLOBAL_NEW_GROUP.getMessage())) {
            if (dungeon == null) {
                MessageUtil.sendMessage(player, DMessage.ERROR_SIGN_WRONG_FORMAT.getMessage());
                return;
            }

            if (groupName != null) {
                group = new DGroup(plugin, groupName, player, dungeon);
            } else {
                group = new DGroup(plugin, player, dungeon);
            }
            update();

        } else if (topSign.getLine(0).equals(DMessage.SIGN_GLOBAL_JOIN_GROUP.getMessage())) {
            group.addPlayer(player);
            update();
        }
    }

    /* Statics */
    /**
     * @param plugin the plugin instance
     * @param block  a block which is protected by the returned GroupSign
     * @return the group sign the block belongs to, null if it belongs to none
     */
    public static GroupSign getByBlock(DungeonsXL plugin, Block block) {
        if (!Category.SIGNS.containsBlock(block)) {
            return null;
        }

        for (GlobalProtection protection : plugin.getGlobalProtectionCache().getProtections(GroupSign.class)) {
            GroupSign groupSign = (GroupSign) protection;
            Block start = groupSign.startSign;
            if (start == block || (start.getX() == block.getX() && start.getZ() == block.getZ() && (start.getY() >= block.getY() && start.getY() - groupSign.verticalSigns <= block.getY()))) {
                return groupSign;
            }
        }

        return null;
    }

    public static GroupSign tryToCreate(DungeonsXL plugin, SignChangeEvent event) {
        if (!event.getLine(0).equalsIgnoreCase(SIGN_TAG)) {
            return null;
        }
        if (!event.getLine(1).equalsIgnoreCase(GROUP_SIGN_TAG)) {
            return null;
        }

        String identifier = event.getLine(2);

        String[] data = event.getLine(3).split(",");
        int maxPlayersPerGroup = 1;
        String groupName = null;
        int startIfElementsAtLeast = -1;
        if (data.length >= 1) {
            maxPlayersPerGroup = NumberUtil.parseInt(data[0], 1);
        }
        if (data.length >= 2) {
            groupName = data[1];
        }
        if (data.length == 3) {
            startIfElementsAtLeast = NumberUtil.parseInt(data[2], -1);
        }

        return tryToCreate(plugin, event.getBlock(), identifier, maxPlayersPerGroup, startIfElementsAtLeast, groupName);
    }

    public static GroupSign tryToCreate(DungeonsXL plugin, Block startSign, String identifier, int maxElements, int startIfElementsAtLeast, String groupName) {
        onCreation(plugin, startSign, identifier, maxElements, startIfElementsAtLeast);
        GroupSign sign = new GroupSign(plugin, plugin.getGlobalProtectionCache().generateId(GroupSign.class, startSign.getWorld()), startSign, identifier,
                maxElements, startIfElementsAtLeast, groupName);
        return sign;
    }

}
