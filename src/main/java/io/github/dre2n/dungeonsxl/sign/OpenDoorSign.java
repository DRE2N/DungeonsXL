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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.commons.util.BlockUtil;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.material.Openable;

/**
 * @author Daniel Saukel
 */
public class OpenDoorSign extends DSign {

    private DSignType type = DSignTypeDefault.OPEN_DOOR;

    private Openable block;

    public OpenDoorSign(Sign sign, String[] lines, GameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    /* Getters and setters */
    /**
     * @return the door / fence gate / ... to open;
     */
    public Openable getBlock() {
        return block;
    }

    /**
     * @param block
     * the door / fence gate / ... to open
     */
    public void setBlock(Openable block) {
        this.block = block;
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
        if (block instanceof Openable) {
            this.block = (Openable) block;
        }

        getSign().setType(Material.AIR);
    }

    @Override
    public void onTrigger() {
        block.setOpen(true);
    }

    /* Statics */
    /**
     * @param block
     * the block to check
     * @return
     * true if the block is openable only with a sign
     */
    public static boolean isProtected(Block block) {
        if (!(block instanceof Openable)) {
            return false;
        }

        GameWorld gameWorld = GameWorld.getByWorld(block.getWorld());
        if (gameWorld != null) {
            for (DSign dSign : gameWorld.getDSigns()) {
                if (dSign.getType() == DSignTypeDefault.OPEN_DOOR) {
                    if (((OpenDoorSign) dSign).getBlock().equals(block)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
