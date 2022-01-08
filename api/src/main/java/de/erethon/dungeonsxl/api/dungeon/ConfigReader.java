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

import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.caliburn.mob.ExMob;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.world.GameWorld;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * A functional interface to deserialize a raw value read from a configuration.
 *
 * @param <V> the type of the object to read
 * @author Daniel Saukel
 */
@FunctionalInterface
public interface ConfigReader<V> {

    /**
     * Reads a set of Caliburn items.
     */
    static final ConfigReader<Set<ExItem>> EX_ITEM_SET_READER = (api, value) -> {
        if (!(value instanceof Collection)) {
            return null;
        }
        Set<ExItem> set = new HashSet<>();
        for (Object entry : (Collection) value) {
            set.add(api.getCaliburn().getExItem(entry));
        }
        return set;
    };
    /**
     * Reads a set of Caliburn mobs.
     */
    static final ConfigReader<Set<ExMob>> EX_MOB_SET_READER = (api, value) -> {
        if (!(value instanceof Collection)) {
            return null;
        }
        Set<ExMob> set = new HashSet<>();
        for (Object entry : (Collection) value) {
            set.add(api.getCaliburn().getExMob(entry));
        }
        return set;
    };
    /**
     * Reads a map of Caliburn items as tool keys and a set of Caliburn items as block values.
     */
    static final ConfigReader<Map<ExItem, HashSet<ExItem>>> TOOL_BLOCK_MAP_READER = (api, value) -> {
        if (!(value instanceof ConfigurationSection)) {
            return null;
        }
        ConfigurationSection section = (ConfigurationSection) value;
        Map<ExItem, HashSet<ExItem>> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            ExItem tool = api.getCaliburn().getExItem(entry.getKey());
            if (tool == null) {
                continue;
            }
            HashSet<ExItem> blocks = new HashSet<>();
            blocks.addAll(api.getCaliburn().deserializeExItemList(section, entry.getKey()));
            map.put(tool, blocks);
        }
        return map;
    };
    static final ConfigReader<BuildMode> BUILD_MODE_READER = (api, value) -> {
        if (value instanceof Boolean) {
            return (Boolean) value ? BuildMode.TRUE : BuildMode.FALSE;
        } else if (value instanceof String) {
            return BuildMode.Registry.ENTRIES.get(((String) value).toLowerCase());
        } else if (value instanceof List) {
            return (Player p, GameWorld w, Block b) -> ((List) value).contains(VanillaItem.get(b.getType()).getId());
        } else {
            Map<ExItem, HashSet<ExItem>> whitelist = TOOL_BLOCK_MAP_READER.read(api, value);
            if (whitelist == null) {
                return null;
            }
            return (Player p, GameWorld w, Block b) -> {
                ExItem type = VanillaItem.get(b.getType());
                ExItem breakTool = api.getCaliburn().getExItem(p.getItemInHand());
                return whitelist.containsKey(type)
                        && (whitelist.get(type) == null
                        || whitelist.get(type).isEmpty()
                        || whitelist.get(type).contains(breakTool));
            };
        }
    };

    /**
     * Reads a game rule state from the configuration.
     *
     * @param api   the DungeonsAPI instance
     * @param value the configuration object. This is the object received from using {@link org.bukkit.configuration.ConfigurationSection#get(String)}
     *              with the String being {@link GameRule#getKey()}.
     * @return the game rule state read from configuration
     */
    V read(DungeonsAPI api, Object value);

}
