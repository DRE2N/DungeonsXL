/*
 * Copyright (C) 2012-2013 Frank Baumann; 2015-2026 Daniel Saukel
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
package de.erethon.dungeonsxl.util;

import com.google.common.collect.ImmutableMap;
import de.erethon.xlib.compatibility.Version;
import java.util.Map;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class AttributeUtil {

    public static final Attribute ATTACK_DAMAGE = Attribute.valueOf(Version.isAtLeast(Version.MC1_21_2) ? "ATTACK_DAMAGE" : "GENERIC_ATTACK_DAMAGE");
    // 1.21 port: fixed copy-paste bug (was "GENERIC_MOVEMENT_SPEED" for MAX_HEALTH fallback)
    public static final Attribute MAX_HEALTH = Attribute.valueOf(Version.isAtLeast(Version.MC1_21_2) ? "MAX_HEALTH" : "GENERIC_MAX_HEALTH");
    public static final Attribute MOVEMENT_SPEED = Attribute.valueOf(Version.isAtLeast(Version.MC1_21_2) ? "MOVEMENT_SPEED" : "GENERIC_MOVEMENT_SPEED");

    private static final Map<Attribute, Double> DEFAULT_PLAYER_VALUES = ImmutableMap.of(
            MOVEMENT_SPEED, .1, // .7
            ATTACK_DAMAGE, 1.0 // 2.0
    );

    /**
     * Returns the attribute represented by the key.
     *
     * @param key the key; not null
     * @return the attribute represented by the key
     */
    public static Attribute get(String key) {
        Attribute attribute;
        if (Version.isAtLeast(Version.MC1_21_2)) {
            attribute = Registry.ATTRIBUTE.match(key);
            if (attribute == null) {
                // Compatibility upon Minecraft updates: strip legacy GENERIC_ prefix
                // 1.21 port: replace() called with wrong 2nd arg (was the whole key);
                // must be the replacement string, i.e. empty to drop the prefix.
                attribute = Registry.ATTRIBUTE.match(key.replace("GENERIC_", ""));
            }
        } else {
            try {
                attribute = Attribute.valueOf(key);
            } catch (Exception exception) {
                attribute = null;
            }
        }
        return attribute;
    }

    /**
     * Returns the default value that a player entity has.
     *
     * @param attribute the attribute instance to check
     * @return the default value that a player entity has
     */
    public static final Double getDefaultPlayerValue(AttributeInstance attribute) {
        return DEFAULT_PLAYER_VALUES.getOrDefault(attribute.getAttribute(), attribute.getDefaultValue());
    }

    /**
     * Resets a player's attributes.
     *
     * @param player the player
     */
    public static void resetPlayerAttributes(Player player) {
        for (Attribute attribute : Attribute.values()) {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) {
                continue;
            }
            instance.setBaseValue(getDefaultPlayerValue(instance));
            instance.getModifiers().forEach(instance::removeModifier);
        }
    }

}
