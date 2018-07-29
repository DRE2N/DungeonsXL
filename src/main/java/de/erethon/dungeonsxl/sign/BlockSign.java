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
package de.erethon.dungeonsxl.sign;

import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;

/**
 * @author Milan Albrecht, Daniel Saukel
 */
public class BlockSign extends DSign {

    private DSignType type = DSignTypeDefault.BLOCK;

    // Variables
    private boolean initialized;
    private boolean active;
    private ExItem offBlock;
    private ExItem onBlock;
    private BlockData offBlockData;
    private BlockData onBlockData;

    public BlockSign(Sign sign, String[] lines, DGameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        if (lines[1].isEmpty()) {
            offBlock = VanillaItem.AIR;

        } else {
            offBlock = plugin.getCaliburn().getExItem(lines[1]);
            if (offBlock == null) {
                try {
                    offBlockData = Bukkit.createBlockData(lines[1]);
                } catch (IllegalArgumentException exception) {
                    markAsErroneous("Could not recognize offBlock, input: " + lines[1]);
                    return;
                }
            }
        }

        if (lines[2].isEmpty()) {
            onBlock = VanillaItem.AIR;

        } else {
            onBlock = plugin.getCaliburn().getExItem(lines[2]);
            if (onBlock == null) {
                try {
                } catch (IllegalArgumentException exception) {
                    markAsErroneous("Could not recognize onBlock, input: " + lines[2]);
                    return;
                }
            }
        }

        if (offBlock != null) {
            getSign().getBlock().setType(offBlock.getMaterial());

        } else if (offBlockData != null) {
            try {
                getSign().getBlock().setBlockData(offBlockData);

            } catch (IllegalArgumentException exception) {
                markAsErroneous("offBlock data value " + offBlockData + " cannot be applied.");
                return;
            }
        }

        initialized = true;
    }

    @Override
    public void onTrigger() {
        if (!initialized || active) {
            return;
        }

        if (onBlock != null) {
            getSign().getBlock().setType(onBlock.getMaterial());

        } else if (onBlockData != null) {
            try {
                getSign().getBlock().setBlockData(onBlockData);
            } catch (IllegalArgumentException exception) {
                markAsErroneous("onBlock data value " + onBlockData + " cannot be applied to given type " + onBlock.getId());
                return;
            }
        }

        active = true;
    }

    @Override
    public void onDisable() {
        if (!initialized || !active) {
            return;
        }

        if (offBlock != null) {
            getSign().getBlock().setType(offBlock.getMaterial());
        } else if (offBlockData != null) {
            try {
                getSign().getBlock().setBlockData(offBlockData);
            } catch (IllegalArgumentException exception) {
            }
        }

        active = false;
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
