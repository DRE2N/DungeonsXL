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

import de.erethon.dungeonsxl.api.DungeonsAPI;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A {@link GameRule} where the value is a {@link java.util.Map}.
 *
 * @param <TK> the type of the map key
 * @param <TV> the type of the map value
 * @param <V>  the type of the game rule value
 * @author Daniel Saukel
 */
public class MapGameRule<TK, TV, V extends Map<TK, TV>> extends GameRule<V> {

    protected Copier<V> copier;

    /**
     * @param key          the configuration key of the game rule
     * @param defaultValue the default value that is used when nothing is set; not null
     * @param reader       a functional interface that loads the value from config
     * @param copier       a method to copy the map
     */
    public MapGameRule(String key, V defaultValue, ConfigReader<V> reader, Copier<V> copier) {
        super(null, key, defaultValue, reader);
        Validate.notNull(defaultValue, "defaultValue must not be null");
        this.copier = copier;
    }

    /**
     * This implementation uses more expensive casting + catching the ClassCastException.
     * Developers should consider doing that themselves instead of wasting this cast.
     *
     * @param value the value
     * @return if the given value is an instance of {@link V}
     */
    @Override
    public boolean isValidValue(Object value) {
        try {
            V v = (V) value;
            return true;
        } catch (ClassCastException exception) {
            return false;
        }
    }

    @Override
    public V fromConfig(DungeonsAPI api, GameRuleContainer container, ConfigurationSection config) {
        if (reader == null) {
            return null;
        }

        V v = reader.read(api, config.getConfigurationSection(getKey()));
        if (v == null) {
            return null;
        }
        v.remove(null); // Do not allow null values
        container.setState(this, v);
        return v;
    }

    @Override
    public void merge(GameRuleContainer overriding, GameRuleContainer subsidiary, GameRuleContainer writeTo) {
        V writeToState = writeTo.getState(this);
        V write = writeToState != null ? copier.copy(writeTo.getState(this)) : null;

        V subsidiaryState = subsidiary.getState(this);
        if (subsidiaryState != null) {
            if (write == null) {
                write = copier.copy(subsidiaryState);
            } else {
                write.putAll(subsidiaryState);
            }
        }

        V overridingState = overriding.getState(this);
        if (overridingState != null) {
            if (write == null) {
                write = copier.copy(overridingState);
            } else {
                write.putAll(overridingState);
            }
        }
        writeTo.setState(this, write);
    }

}
