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

import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.util.MagicValueUtil;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.block.Sign;

/**
 * @author Milan Albrecht, Daniel Saukel
 */
public class BlockSign extends DSign {

    // Variables
    private boolean initialized;
    private boolean active;
    private ExItem offBlock = VanillaItem.AIR;
    private ExItem onBlock = VanillaItem.AIR;
    private byte offBlockData = 0x0;
    private byte onBlockData = 0x0;

    public BlockSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
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
            String[] line1 = lines[1].split(",");
            offBlock = plugin.getCaliburn().getExItem(line1[0]);
            if (offBlock == null) {
                markAsErroneous("Could not recognize offBlock, input: " + lines[1]);
                return;
            }
            if (line1.length > 1) {
                offBlockData = (byte) NumberUtil.parseInt(line1[1]);
            }
        }

        if (lines[2].isEmpty()) {
            onBlock = VanillaItem.AIR;

        } else {
            String[] line2 = lines[2].split(",");
            onBlock = plugin.getCaliburn().getExItem(line2[0]);
            if (onBlock == null) {
                markAsErroneous("Could not recognize onBlock, input: " + lines[2]);
                return;
            }
            if (line2.length > 1) {
                onBlockData = (byte) NumberUtil.parseInt(line2[1]);
            }
        }

        getSign().getBlock().setType(offBlock.getMaterial());
        try {
            MagicValueUtil.setBlockData(getSign().getBlock(), offBlockData);
        } catch (IllegalArgumentException exception) {
            markAsErroneous("offBlock data value " + offBlockData + " cannot be applied to given type " + offBlock.getId());
            return;
        }
        initialized = true;
    }

    @Override
    public void onTrigger() {
        if (initialized && !active) {
            getSign().getBlock().setType(onBlock.getMaterial());
            try {
                MagicValueUtil.setBlockData(getSign().getBlock(), onBlockData);
            } catch (IllegalArgumentException exception) {
                markAsErroneous("onBlock data value " + onBlockData + " cannot be applied to given type " + onBlock.getId());
                return;
            }
            active = true;
        }
    }

    @Override
    public void onDisable() {
        if (initialized && active) {
            getSign().getBlock().setType(offBlock.getMaterial());
            MagicValueUtil.setBlockData(getSign().getBlock(), offBlockData);
            active = false;
        }
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.BLOCK;
    }

}
