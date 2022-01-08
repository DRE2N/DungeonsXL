/*
 * Copyright (C) 2012-2022 Frank Baumann
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
import java.util.Map;
import org.bukkit.attribute.Attribute;
import static org.bukkit.attribute.Attribute.*;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class AttributeUtil {

    private static final Map<Attribute, Double> DEFAULT_PLAYER_VALUES = ImmutableMap.of(
            GENERIC_MOVEMENT_SPEED, .1, // .7
            GENERIC_ATTACK_DAMAGE, 1.0 // 2.0
    );

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
