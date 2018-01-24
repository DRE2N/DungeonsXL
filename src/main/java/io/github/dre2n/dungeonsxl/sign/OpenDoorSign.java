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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.commons.misc.BlockUtil;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import io.github.dre2n.dungeonsxl.world.block.LockedDoor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.material.Door;

/**
 * @author Daniel Saukel
 */
public class OpenDoorSign extends DSign {

    private DSignType type = DSignTypeDefault.OPEN_DOOR;

    private LockedDoor door;
    private boolean active = true;

    public OpenDoorSign(Sign sign, String[] lines, DGameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    /* Getters and setters */
    /**
     * @return the door to open;
     */
    public LockedDoor getDoor() {
        return door;
    }

    /**
     * @param door
     * the door to open
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
     * @param active
     * toggle the sign active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public DSignType getType() {
        return type;
    }

    /* Actions */
    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        Block block = BlockUtil.getAttachedBlock(getSign().getBlock());
        if (block.getState().getData() instanceof Door) {
            if (block.getRelative(BlockFace.DOWN).getType() == block.getType()) {
                door = new LockedDoor(block.getRelative(BlockFace.DOWN));
            } else {
                door = new LockedDoor(block);
            }
            getGameWorld().addGameBlock(door);

            getSign().getBlock().setType(Material.AIR);

        } else {
            markAsErroneous();
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
