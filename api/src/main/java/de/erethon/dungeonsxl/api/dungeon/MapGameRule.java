/*
 * Copyright (C) 2014-2020 Daniel Saukel
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

import java.util.Map;

/**
 * A {@link GameRule} where the value is a {@link java.util.Map}.
 *
 * @param <TK> the type of the map key
 * @param <TV> the type of the map value
 * @param <V>  the type of the game rule value
 * @author Daniel Saukel
 */
public class MapGameRule<TK, TV, V extends Map<TK, TV>> extends GameRule<V> {

    public MapGameRule(Class type, String key, V defaultValue) {
        super(type, key, defaultValue);
    }

    @Override
    public void merge(GameRuleContainer overriding, GameRuleContainer subsidiary, GameRuleContainer writeTo) {
        V write = writeTo.getState(this);
        write.putAll(subsidiary.getState(this));
        write.putAll(overriding.getState(this));
        writeTo.setState(this, write);
    }

}
