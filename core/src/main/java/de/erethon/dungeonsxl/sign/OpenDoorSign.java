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
package de.erethon.dungeonsxl.sign;

import de.erethon.caliburn.category.Category;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.misc.BlockUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.world.DGameWorld;
import de.erethon.dungeonsxl.world.block.LockedDoor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

/**
 * @author Daniel Saukel
 */
public class OpenDoorSign extends DSign {

    private LockedDoor door;
    private boolean active = true;

    public OpenDoorSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    /* Getters and setters */
    /**
     * @return the door to open;
     */
    public LockedDoor getDoor() {
        return door;
    }

    /**
     * @param door the door to open
     */
    public void setDoor(LockedDoor door) {
        this.door = door;
    }

    /**
     * @return if the sign is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active toggle the sign active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.OPEN_DOOR;
    }

    /* Actions */
    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        Block block = BlockUtil.getAttachedBlock(getSign().getBlock());
        if (Category.DOORS.containsBlock(block)) {
            if (block.getRelative(BlockFace.DOWN).getType() == block.getType()) {
                door = new LockedDoor(plugin, block.getRelative(BlockFace.DOWN));
            } else {
                door = new LockedDoor(plugin, block);
            }
            getGameWorld().addGameBlock(door);

            getSign().getBlock().setType(VanillaItem.AIR.getMaterial());

        } else {
            markAsErroneous("No door attached");
        }
    }

    @Override
    public void onTrigger() {
        if (door != null && active) {
            door.open();
            active = false;
        }
    }

}
