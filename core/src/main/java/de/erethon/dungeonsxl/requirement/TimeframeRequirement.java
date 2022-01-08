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
package de.erethon.dungeonsxl.requirement;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Requirement;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.util.commons.misc.EnumUtil;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class TimeframeRequirement implements Requirement {

    public enum Weekday {
        SUNDAY, // *angry German noises*
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY;

        @Override
        public String toString() {
            return DMessage.valueOf("DAY_OF_WEEK_" + ordinal()).getMessage();
        }
    }

    public static class Timeframe<T> {

        private T start, end;

        public Timeframe(T start, T end) {
            this.start = start;
            this.end = end;
        }

        public T getStart() {
            return start;
        }

        public T getEnd() {
            return end;
        }

    }

    private List<Timeframe<Weekday>> weekdays = new ArrayList<>();
    private List<Timeframe<Integer>> daytimes = new ArrayList<>();

    public TimeframeRequirement(DungeonsAPI api) {
    }

    @Override
    public void setup(ConfigurationSection config) {
        List<String> dates = config.getStringList("timeframe");
        for (String date : dates) {
            String[] time = date.split("-");

            Weekday firstDay = EnumUtil.getEnumIgnoreCase(Weekday.class, time[0]);
            if (firstDay != null) {
                Weekday secondDay = firstDay;
                if (time.length == 2) {
                    secondDay = EnumUtil.getEnumIgnoreCase(Weekday.class, time[1]);
                }
                if (secondDay.ordinal() >= firstDay.ordinal()) {
                    weekdays.add(new Timeframe<>(firstDay, secondDay));
                }
            }

            if (time.length < 2) {
                continue;
            }
            int firstHour = NumberUtil.parseInt(time[0], 0);
            int secondHour = NumberUtil.parseInt(time[1], -1);
            if (secondHour > firstHour) {
                daytimes.add(new Timeframe<>(firstHour, secondHour));
            }
        }
    }

    @Override
    public boolean check(Player player) {
        boolean match = weekdays.isEmpty();
        for (Timeframe<Weekday> timeframe : weekdays) {
            if (isInDayTimeframe(timeframe)) {
                match = true;
                break;
            }
        }
        if (!match) {
            return false;
        }

        match = daytimes.isEmpty();
        for (Timeframe<Integer> timeframe : daytimes) {
            if (isInHourTimeframe(timeframe)) {
                match = true;
                break;
            }
        }
        return match;
    }

    private boolean isInDayTimeframe(Timeframe<Weekday> timeframe) {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1; // Calendar days start with 1...
        return day >= timeframe.getStart().ordinal() && day <= timeframe.getEnd().ordinal();
    }

    private boolean isInHourTimeframe(Timeframe<Integer> timeframe) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return hour >= timeframe.getStart() && hour < timeframe.getEnd();
    }

    @Override
    public BaseComponent[] getCheckMessage(Player player) {
        ComponentBuilder builder = new ComponentBuilder(DMessage.REQUIREMENT_TIMEFRAME.getMessage() + ": ").color(ChatColor.GOLD);
        boolean first = true;
        for (Timeframe<Weekday> timeframe : weekdays) {
            ChatColor color = isInDayTimeframe(timeframe) ? ChatColor.GREEN : ChatColor.DARK_RED;
            if (!first) {
                builder.append(" & ").color(ChatColor.WHITE);
            } else {
                first = false;
            }
            if (timeframe.getStart() != timeframe.getEnd()) {
                builder.append(timeframe.getStart() + "-" + timeframe.getEnd()).color(color);
            } else {
                builder.append(timeframe.getStart().toString()).color(color);
            }
        }

        first = true;
        for (Timeframe<Integer> timeframe : daytimes) {
            ChatColor color = isInHourTimeframe(timeframe) ? ChatColor.GREEN : ChatColor.DARK_RED;
            if (!first) {
                builder.append(" & ").color(ChatColor.WHITE);
            } else {
                first = false;
                if (!weekdays.isEmpty()) {
                    builder.append(" | ").color(ChatColor.WHITE);
                }
            }
            builder.append(timeframe.getStart() + "-" + timeframe.getEnd()).color(color);
        }

        return builder.create();
    }

    @Override
    public void demand(Player player) {
    }

}
