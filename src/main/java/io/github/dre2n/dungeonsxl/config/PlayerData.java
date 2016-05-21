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
package io.github.dre2n.dungeonsxl.config;

import io.github.dre2n.commons.config.BRConfig;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel Saukel
 */
public class PlayerData extends BRConfig {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public static final int CONFIG_VERSION = 1;

    private Map<String, Long> timeLastPlayed = new HashMap<>();

    public PlayerData(File file) {
        super(file, CONFIG_VERSION);

        if (initialize) {
            initialize();
        }
        load();
    }

    /**
     * @return a map of the player's finished dungeons with dates.
     */
    public Map<String, Long> getTimeLastPlayed() {
        return timeLastPlayed;
    }

    /**
     * @param dungeon
     * the dungeon to check
     * @return the time when the player finished the dungeon for the last time
     */
    public long getTimeLastPlayed(String dungeon) {
        Long time = timeLastPlayed.get(dungeon.toLowerCase());
        if (time == null) {
            return -1;
        } else {
            return time;
        }
    }

    /**
     * @param dungeon
     * the finished dungeon
     * @param time
     * the time when the dungeon was finished
     */
    public void setTimeLastPlayed(String dungeon, long time) {
        timeLastPlayed.put(dungeon.toLowerCase(), time);
        save();
    }

    /**
     * @param dungeon
     * the finished dungeon
     */
    public void logTimeLastPlayed(String dungeon) {
        timeLastPlayed.put(dungeon.toLowerCase(), System.currentTimeMillis());
        save();
    }

    @Override
    public void initialize() {
        if (!config.contains("timeLastPlayed")) {
            config.createSection("timeLastPlayed");
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                MessageUtil.log(plugin, DMessages.LOG_NEW_PLAYER_DATA.getMessage(file.getName()));
            } catch (IOException exception) {
            }
        }

        save();
    }

    @Override
    public void load() {
        if (config.isConfigurationSection("timeLastPlayed")) {
            for (String key : config.getConfigurationSection("timeLastPlayed").getKeys(false)) {
                timeLastPlayed.put(key, config.getLong("timeLastPlayed." + key));
            }
        }
    }

    @Override
    public void save() {
        config.set("timeLastPlayed", timeLastPlayed);
        super.save();
    }

}
