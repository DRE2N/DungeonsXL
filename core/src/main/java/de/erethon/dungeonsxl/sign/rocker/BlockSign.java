/*
 * Copyright (C) 2012-2023 Frank Baumann
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
package de.erethon.dungeonsxl.sign.rocker;

import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.sign.Rocker;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;

/**
 * Modern Paper API implementation without reflection.
 * Updated for Paper 1.21.8
 *
 * @author Milan Albrecht, Daniel Saukel
 */
public class BlockSign extends Rocker {

    private ExItem offBlock = VanillaItem.AIR;
    private ExItem onBlock = VanillaItem.AIR;
    private BlockData offBlockData = null;
    private BlockData onBlockData = null;

    public BlockSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "Block";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".block";
    }

    @Override
    public boolean isOnDungeonInit() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public boolean isSetToAir() {
        return false;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void initialize() {
        if (getLine(1).isEmpty()) {
            offBlock = VanillaItem.AIR;
        } else {
            String[] line1 = getLine(1).split(",");
            offBlock = api.getCaliburn().getExItem(line1[0]);
            if (offBlock == null) {
                markAsErroneous("Could not recognize offBlock, input: " + getLine(1));
                return;
            }
            if (line1.length > 1) {
                // Parse BlockData string for modern API
                try {
                    offBlockData = getSign().getServer().createBlockData(offBlock.getMaterial(), "[" + line1[1] + "]");
                } catch (IllegalArgumentException exception) {
                    // If parsing fails, use default BlockData
                    offBlockData = offBlock.getMaterial().createBlockData();
                }
            }
        }

        if (getLine(2).isEmpty()) {
            onBlock = VanillaItem.AIR;
        } else {
            String[] line2 = getLine(2).split(",");
            onBlock = api.getCaliburn().getExItem(line2[0]);
            if (onBlock == null) {
                markAsErroneous("Could not recognize onBlock, input: " + getLine(2));
                return;
            }
            if (line2.length > 1) {
                // Parse BlockData string for modern API
                try {
                    onBlockData = getSign().getServer().createBlockData(onBlock.getMaterial(), "[" + line2[1] + "]");
                } catch (IllegalArgumentException exception) {
                    // If parsing fails, use default BlockData
                    onBlockData = onBlock.getMaterial().createBlockData();
                }
            }
        }

        Block block = getSign().getBlock();
        block.setType(offBlock.getMaterial());
        if (offBlockData != null) {
            try {
                block.setBlockData(offBlockData);
            } catch (IllegalArgumentException exception) {
                markAsErroneous("offBlock data cannot be applied to given type " + offBlock.getId());
            }
        }
    }

    @Override
    public void activate() {
        Block block = getSign().getBlock();
        block.setType(onBlock.getMaterial());
        if (onBlockData != null) {
            try {
                block.setBlockData(onBlockData);
            } catch (IllegalArgumentException exception) {
                markAsErroneous("onBlock data cannot be applied to given type " + onBlock.getId());
                return;
            }
        }
        active = true;
    }

    @Override
    public void deactivate() {
        Block block = getSign().getBlock();
        block.setType(offBlock.getMaterial());
        if (offBlockData != null) {
            try {
                block.setBlockData(offBlockData);
            } catch (IllegalArgumentException exception) {
                markAsErroneous("offBlock data cannot be applied to given type " + offBlock.getId());
                return;
            }
        }
        active = false;
    }
}
