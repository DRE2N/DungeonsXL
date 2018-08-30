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
package de.erethon.dungeonsxl.game.rule;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.dungeonsxl.DungeonsXL;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Daniel Saukel
 */
public interface GameRule {

    /**
     * Returns the configuration key of the game rule
     *
     * @return the configuration key of the game rule
     */
    public String getKey();

    /**
     * Returns the value used if nothing is specified by a game rule provider
     *
     * @return the value used if nothing is specified by a game rule provider
     */
    public Object getDefaultValue();

    /**
     * Returns the state of the game rule fetched from the config
     *
     * @param plugin   the plugin instance
     * @param caliburn the CaliburnAPI instance
     * @param provider the game rule provider to set the state
     * @param config   the config to fetch the value from
     * @return the value
     */
    public Object fromConfig(DungeonsXL plugin, CaliburnAPI caliburn, GameRuleProvider provider, ConfigurationSection config);

}
