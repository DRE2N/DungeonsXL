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
package de.erethon.dungeonsxl.global;

import de.erethon.caliburn.category.Category;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.misc.BlockUtil;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.world.DResourceWorld;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Attachable;

/**
 * A sign to form a group and to define its dungeon.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class GroupSign extends GlobalProtection {

    public static final String GROUP_SIGN_TAG = "Group";

    private String groupName;
    private DGroup group;
    private Dungeon dungeon;
    private int maxPlayersPerGroup;
    private Block startSign;
    private int verticalSigns;
    private Set<Block> blocks;

    public GroupSign(int id, Block startSign, String identifier, int maxPlayersPerGroup, String groupName) {
        super(startSign.getWorld(), id);

        this.startSign = startSign;
        dungeon = DungeonsXL.getInstance().getDungeons().getByName(identifier);
        if (dungeon == null) {
            DResourceWorld resource = DungeonsXL.getInstance().getDWorlds().getResourceByName(identifier);
            if (resource != null) {
                dungeon = new Dungeon(resource);
            }
        }
        this.maxPlayersPerGroup = maxPlayersPerGroup;
        verticalSigns = (int) Math.ceil((float) (1 + maxPlayersPerGroup) / 4);
        this.groupName = groupName;

        update();
    }

    /**
     * @return
     * the attached group
     */
    public DGroup getGroup() {
        return group;
    }

    /**
     * @param group
     * the group to set
     */
    public void setGroup(DGroup group) {
        this.group = group;
    }

    /**
     * @return
     * the dungeon
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * @param dungeon
     * the dungeon to set
     */
    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    /**
     * @return
     * the maximum player count per group
     */
    public int getMaxPlayersPerGroup() {
        return maxPlayersPerGroup;
    }

    /**
     * @param maxPlayersPerGroup
     * the maximum player count per group to set
     */
    public void setMaxPlayersPerGroup(int maxPlayersPerGroup) {
        this.maxPlayersPerGroup = maxPlayersPerGroup;
    }

    /**
     * Update this group sign to show the group(s) correctly.
     */
    public void update() {
        if (!(startSign.getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) startSign.getState();

        sign.setLine(0, "");
        sign.setLine(1, "");
        sign.setLine(2, "");
        sign.setLine(3, "");

        int y = -1 * verticalSigns;
        while (startSign.getRelative(0, y, 0).getState() instanceof Sign && y != 0) {
            Sign subsign = (Sign) sign.getBlock().getRelative(0, y, 0).getState();
            subsign.setLine(0, "");
            subsign.setLine(1, "");
            subsign.setLine(2, "");
            subsign.setLine(3, "");
            subsign.update();
            y++;
        }

        // Set Signs
        if (group == null) {
            sign.setLine(0, DMessage.SIGN_GLOBAL_NEW_GROUP.getMessage());
            sign.update();
            return;
        }

        if (group.isPlaying()) {
            sign.setLine(0, DMessage.SIGN_GLOBAL_IS_PLAYING.getMessage());

        } else if (group.getPlayers().size() >= maxPlayersPerGroup) {
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
    public Set<Block> getBlocks() {
        if (blocks == null) {
            blocks = new HashSet<>();

            HashSet<Block> toAdd = new HashSet<>();
            int y = -1 * verticalSigns;
            while (y != 1) {
                blocks.add(startSign.getRelative(0, y, 0));
                y++;
            }

            for (Block block : blocks) {
                int i = verticalSigns;
                do {
                    i--;

                    Block beneath = block.getLocation().add(0, -1 * i, 0).getBlock();
                    toAdd.add(beneath);
                    toAdd.add(BlockUtil.getAttachedBlock(beneath));

                } while (i >= 0);
            }
            blocks.addAll(toAdd);
        }

        return blocks;
    }

    @Override
    public void save(FileConfiguration config) {
        String preString = "protections.groupSigns." + getWorld().getName() + "." + getId();

        config.set(preString + ".x", startSign.getX());
        config.set(preString + ".y", startSign.getY());
        config.set(preString + ".z", startSign.getZ());
        config.set(preString + ".dungeon", dungeon.getName());
        config.set(preString + ".groupName", groupName);
        config.set(preString + ".maxPlayersPerGroup", maxPlayersPerGroup);
    }

    public boolean onPlayerInteract(Block block, Player player) {
        int y = block.getY();
        if (DGroup.getByPlayer(player) != null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_GROUP.getMessage());
            return true;
        }

        int sy1 = startSign.getY();

        Block topBlock = block.getRelative(0, sy1 - y, 0);

        if (!(topBlock.getState() instanceof Sign)) {
            return true;
        }

        Sign topSign = (Sign) topBlock.getState();

        if (topSign.getLine(0).equals(DMessage.SIGN_GLOBAL_NEW_GROUP.getMessage())) {
            if (groupName != null) {
                group = new DGroup(groupName, player, dungeon);
            } else {
                group = new DGroup(player, dungeon);
            }
            update();

        } else if (topSign.getLine(0).equals(DMessage.SIGN_GLOBAL_JOIN_GROUP.getMessage())) {
            group.addPlayer(player);
            update();
        }

        return true;
    }

    /* Statics */
    /**
     * @param block
     * a block which is protected by the returned GroupSign
     */
    public static GroupSign getByBlock(Block block) {
        if (!Category.SIGNS.containsBlock(block)) {
            return null;
        }

        for (GlobalProtection protection : DungeonsXL.getInstance().getGlobalProtections().getProtections(GroupSign.class)) {
            GroupSign groupSign = (GroupSign) protection;
            Block start = groupSign.startSign;
            if (start == block || (start.getX() == block.getX() && start.getZ() == block.getZ() && (start.getY() >= block.getY() && start.getY() - groupSign.verticalSigns <= block.getY()))) {
                return groupSign;
            }
        }

        return null;
    }

    public static GroupSign tryToCreate(SignChangeEvent event) {
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
        if (data.length >= 1) {
            maxPlayersPerGroup = NumberUtil.parseInt(data[0], 1);
        }
        if (data.length == 2) {
            groupName = data[1];
        }

        return tryToCreate(event.getBlock(), identifier, maxPlayersPerGroup, groupName);
    }

    public static GroupSign tryToCreate(Block startSign, String identifier, int maxPlayersPerGroup, String groupName) {
        World world = startSign.getWorld();
        BlockFace facing = ((Attachable) startSign.getState().getData()).getAttachedFace().getOppositeFace();
        int x = startSign.getX(), y = startSign.getY(), z = startSign.getZ();

        int verticalSigns = (int) Math.ceil((float) (1 + maxPlayersPerGroup) / 4);
        while (verticalSigns > 1) {
            Block block = world.getBlockAt(x, y - verticalSigns, z);
            block.setType(VanillaItem.WALL_SIGN.getMaterial(), false);
            org.bukkit.material.Sign signData = new org.bukkit.material.Sign(VanillaItem.WALL_SIGN.getMaterial());
            signData.setFacingDirection(facing);
            org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
            sign.setData(signData);
            sign.update(true, false);

            verticalSigns--;
        }
        GroupSign sign = new GroupSign(DungeonsXL.getInstance().getGlobalProtections().generateId(GroupSign.class, world), startSign, identifier, maxPlayersPerGroup, groupName);

        return sign;
    }

}
