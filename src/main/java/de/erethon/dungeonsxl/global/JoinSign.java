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

import de.erethon.commons.misc.BlockUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.dungeon.Dungeon;
import de.erethon.dungeonsxl.world.DResourceWorld;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Daniel Saukel
 */
public class JoinSign extends GlobalProtection {

    protected Dungeon dungeon;
    protected int maxElements;
    protected Block startSign;
    protected int verticalSigns;
    protected Set<Block> blocks;

    protected JoinSign(DungeonsXL plugin, int id, Block startSign, String identifier, int maxElements) {
        super(plugin, startSign.getWorld(), id);

        this.startSign = startSign;
        dungeon = plugin.getDungeonCache().getByName(identifier);
        if (dungeon == null) {
            DResourceWorld resource = plugin.getDWorldCache().getResourceByName(identifier);
            if (resource != null) {
                dungeon = new Dungeon(plugin, resource);
            }
        }

        verticalSigns = (int) Math.ceil((float) (1 + maxElements) / 4);

        this.maxElements = maxElements;

        update();
    }

    /**
     * @return the dungeon
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * @param dungeon the dungeon to set
     */
    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    /**
     * @return the maximum element count per sign
     */
    public int getMaxElements() {
        return maxElements;
    }

    /**
     * @param maxElements the maximum element count per sign
     */
    public void setMaxElements(int maxElements) {
        this.maxElements = maxElements;
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

    /**
     * Clears signs
     */
    public void update() {
        int y = -1 * verticalSigns;
        while (startSign.getRelative(0, y + 1, 0).getState() instanceof Sign && y != 0) {
            Sign subsign = (Sign) startSign.getRelative(0, y + 1, 0).getState();
            subsign.setLine(0, "");
            subsign.setLine(1, "");
            subsign.setLine(2, "");
            subsign.setLine(3, "");
            subsign.update();
            y++;
        }
    }

    @Override
    public void save(FileConfiguration config) {
    }

}
