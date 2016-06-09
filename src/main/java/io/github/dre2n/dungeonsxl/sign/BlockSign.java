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

import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.Material;
import org.bukkit.block.Sign;

/**
 * @author Milan Albrecht, Daniel Saukel
 */
public class BlockSign extends DSign {

    private DSignType type = DSignTypeDefault.BLOCK;

    // Variables
    private boolean initialized;
    private boolean active;
    private int offBlockId = 0;
    private int onBlockId = 0;
    private byte offBlockData = 0x0;
    private byte onBlockData = 0x0;

    public BlockSign(Sign sign, String[] lines, GameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        if (!lines[1].isEmpty()) {
            String line1[] = lines[1].split(",");
            Material offBlock = Material.matchMaterial(line1[0]);
            if (offBlock != null) {
                offBlockId = offBlock.getId();
            } else {
                offBlockId = NumberUtil.parseInt(line1[0]);
            }
            if (line1.length > 1) {
                offBlockData = (byte) NumberUtil.parseInt(line1[1]);
            }
        }

        if (!lines[2].isEmpty()) {
            String line2[] = lines[2].split(",");
            Material onBlock = Material.matchMaterial(line2[0]);

            if (onBlock != null) {
                onBlockId = onBlock.getId();

            } else {
                onBlockId = NumberUtil.parseInt(line2[0]);
            }

            if (line2.length > 1) {
                onBlockData = (byte) NumberUtil.parseInt(line2[1]);
            }
        }

        getSign().getBlock().setTypeIdAndData(offBlockId, offBlockData, true);
        initialized = true;
    }

    @Override
    public void onTrigger() {
        if (initialized && !active) {
            getSign().getBlock().setTypeIdAndData(onBlockId, onBlockData, true);
            active = true;
        }
    }

    @Override
    public void onDisable() {
        if (initialized && active) {
            getSign().getBlock().setTypeIdAndData(offBlockId, offBlockData, true);
            active = false;
        }
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
