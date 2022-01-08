/*
 * Copyright (C) 2014-2022 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api.dungeon;

import de.erethon.dungeonsxl.api.world.GameWorld;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Checks for whether a block may be broken.
 *
 * @author Daniel Saukel
 */
public interface BuildMode {

    /**
     * Stores the pre-set breaking rules.
     */
    static class Registry {
        /**
         * Entry keys must be lowercase.
         */
        public static final Map<String, BuildMode> ENTRIES = new HashMap<>();

        static {
            ENTRIES.put("true", TRUE);
            ENTRIES.put("false", FALSE);
            ENTRIES.put("placed", PLACED);
        }
    }

    /**
     * All blocks except for protected ones may be broken.
     */
    static final BuildMode TRUE = (Player player, GameWorld gameWorld, Block block) -> true;
    /**
     * Blocks may not be broken.
     */
    static final BuildMode FALSE = (Player player, GameWorld gameWorld, Block block) -> false;
    /**
     * Blocks placed by players may be broken.
     */
    static final BuildMode PLACED = (Player player, GameWorld gameWorld, Block block) -> gameWorld.getPlacedBlocks().contains(block);

    /**
     * Returns if the block can be broken or placed by the player.
     * <p>
     * The plugin protects dungeon signs before checking this.
     *
     * @param player    the player who breaks or places the block
     * @param gameWorld the world the block is in
     * @param block     the block
     * @return if the block can be broken or placed by the player
     */
    boolean check(Player player, GameWorld gameWorld, Block block);

}
