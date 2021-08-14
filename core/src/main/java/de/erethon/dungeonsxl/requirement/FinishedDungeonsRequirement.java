/*
 * Copyright (C) 2012-2021 Frank Baumann
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
package de.erethon.dungeonsxl.requirement;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DPlayerData;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.util.commons.misc.SimpleDateUtil;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * The dungeons that need to be finished before this one may be played.
 *
 * @author Daniel Saukel
 */
public class FinishedDungeonsRequirement implements Requirement {

    private static final long HOUR_IN_MILLIS = 3600000L;

    private class DungeonAndTime {
        String dungeon;
        double time = Double.NaN;

        @Override
        public String toString() {
            return dungeon + (!Double.isNaN(time) ? " " + DMessage.REQUIREMENT_FINISHED_DUNGEONS_WITHIN_TIME.getMessage(SimpleDateUtil.decimalToSexagesimalTime(time, 2)) : "");
        }
    }

    private DungeonsAPI api;

    public FinishedDungeonsRequirement(DungeonsAPI api) {
        this.api = api;
    }

    /*
     * finishedDungeons: # all of:
     *   - 7:vdf    # vdf within the last 7 hours
     *   - sku/test # one of sku and test
     */
    private List<List<DungeonAndTime>> dungeons;

    @Override
    public void setup(ConfigurationSection config) {
        List<String> entries = config.getStringList("finishedDungeons");
        dungeons = new ArrayList<>(entries.size());
        for (String entry : entries) {
            List<DungeonAndTime> alternatives = new ArrayList<>();
            for (String string : entry.split("/")) {
                String[] split = string.split(":");
                DungeonAndTime dat = new DungeonAndTime();
                if (split.length > 1) {
                    dat.time = NumberUtil.parseDouble(split[0], Double.NaN);
                    dat.dungeon = split[1];
                } else {
                    dat.dungeon = split[0];
                }
                alternatives.add(dat);
            }
            dungeons.add(alternatives);
        }
    }

    @Override
    public boolean check(Player player) {
        DPlayerData data = ((DGlobalPlayer) api.getPlayerCache().get(player)).getData();
        allOf:
        for (List<DungeonAndTime> dats : dungeons) {
            oneOf:
            for (DungeonAndTime dat : dats) {
                if (Double.isNaN(dat.time)) {
                    if (data.getTimeLastFinished(dat.dungeon) != -1) {
                        continue allOf;
                    }
                } else if (data.getTimeLastFinished(dat.dungeon) + dat.time * HOUR_IN_MILLIS >= System.currentTimeMillis()) {
                    continue allOf;
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public BaseComponent[] getCheckMessage(Player player) {
        DPlayerData data = ((DGlobalPlayer) api.getPlayerCache().get(player)).getData();
        ComponentBuilder builder = new ComponentBuilder(DMessage.REQUIREMENT_FINISHED_DUNGEONS_NAME.getMessage() + ":\n").color(ChatColor.GOLD);
        boolean firstAnd = true;
        for (List<DungeonAndTime> dats : dungeons) {
            // GREEN if the dungeon is finished within the timeframe
            // WHITE if the dungeon is not finished, but part of a list where to have another one finished is sufficient
            // RED if no dungeon of the list is finished within the timeframe
            List<DungeonAndTime> finished = new ArrayList<>();
            List<DungeonAndTime> notFinished = new ArrayList<>();
            for (DungeonAndTime dat : dats) {
                if ((Double.isNaN(dat.time) && data.getTimeLastFinished(dat.dungeon) != -1)
                        || !Double.isNaN(dat.time) && data.getTimeLastFinished(dat.dungeon) + dat.time * HOUR_IN_MILLIS
                        >= System.currentTimeMillis()) {
                    finished.add(dat);
                } else {
                    notFinished.add(dat);
                }
            }
            if (!firstAnd) {
                builder.append(";\n" + DMessage.REQUIREMENT_FINISHED_DUNGEONS_AND.getMessage() + " ").color(ChatColor.GOLD);
            } else {
                firstAnd = false;
            }
            boolean firstOr = true;
            for (DungeonAndTime dat : finished) {
                if (!firstOr) {
                    builder.append("\n" + DMessage.REQUIREMENT_FINISHED_DUNGEONS_OR.getMessage() + " ").color(ChatColor.GOLD);
                } else {
                    firstOr = false;
                }
                builder.append(dat.toString()).color(ChatColor.GREEN);
            }
            for (DungeonAndTime dat : notFinished) {
                if (!firstOr) {
                    builder.append("\n" + DMessage.REQUIREMENT_FINISHED_DUNGEONS_OR.getMessage() + " ").color(ChatColor.GOLD);
                } else {
                    firstOr = false;
                }
                builder.append(dat.toString()).color(finished.isEmpty() ? ChatColor.DARK_RED : ChatColor.WHITE);
            }
        }
        return builder.create();
    }

    @Override
    public void demand(Player player) {
    }

}
