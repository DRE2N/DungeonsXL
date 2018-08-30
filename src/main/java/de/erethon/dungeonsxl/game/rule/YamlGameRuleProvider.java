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
 * The world configuration is a simple game rule source. Besides game rules, WorldConfig also stores some map specific data such as the invited players. It is
 * used directly in dungeon map config.yml files, but also part of dungeon and main config files.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class YamlGameRuleProvider extends GameRuleProvider {

    protected DungeonsXL plugin;
    protected CaliburnAPI caliburn;

    public YamlGameRuleProvider(DungeonsXL plugin) {
        this.plugin = plugin;
        caliburn = plugin.getCaliburn();
    }

    public YamlGameRuleProvider(DungeonsXL plugin, ConfigurationSection config) {
        this(plugin);
        load(config);
    }

    public void load(ConfigurationSection config) {
        for (GameRule rule : GameRuleDefault.values()) {
            setState(rule, rule.fromConfig(plugin, caliburn, this, config));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
