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
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Sign;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ChunkUpdaterSign extends DSign {

    private DSignType type = DSignTypeDefault.CHUNK_UPDATER;

    public ChunkUpdaterSign(Sign sign, String[] lines, GameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        Chunk chunk = getGameWorld().getWorld().getChunkAt(getSign().getBlock());

        if (!lines[1].isEmpty()) {
            Integer radius = NumberUtil.parseInt(lines[1]);
            for (int x = -radius; x < radius; x++) {
                for (int z = -radius; z < radius; z++) {
                    Chunk chunk1 = getGameWorld().getWorld().getChunkAt(chunk.getX() - x, chunk.getZ() - z);
                    chunk1.load();
                    getGameWorld().getLoadedChunks().add(chunk1);
                }
            }

        } else {
            chunk.load();
            getGameWorld().getLoadedChunks().add(chunk);
        }

        getSign().getBlock().setType(Material.AIR);
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
