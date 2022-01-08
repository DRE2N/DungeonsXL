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
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.util.commons.misc.SimpleDateUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Time in hours when the game may be played again since it has been started the last time.
 *
 * @author Daniel Saukel
 */
public class TimeSinceStartRequirement implements Requirement {

    private static final long HOUR_IN_MILLIS = 3600000L;

    private DungeonsAPI api;

    private double time;

    public TimeSinceStartRequirement(DungeonsAPI api) {
        this.api = api;
    }

    @Override
    public void setup(ConfigurationSection config) {
        time = config.getDouble("timeSinceStart", 0.0);
    }

    @Override
    public boolean check(Player player) {
        DGlobalPlayer globalPlayer = (DGlobalPlayer) api.getPlayerCache().get(player);
        return (globalPlayer.getData().getTimeLastStarted(globalPlayer.getGroup().getDungeon().getName())
                + time * HOUR_IN_MILLIS) < System.currentTimeMillis();
    }

    @Override
    public BaseComponent[] getCheckMessage(Player player) {
        ComponentBuilder builder = new ComponentBuilder(DMessage.REQUIREMENT_TIME_SINCE_START
                .getMessage(SimpleDateUtil.decimalToSexagesimalTime(time, 2)) + ":\n").color(ChatColor.GOLD);

        DGlobalPlayer globalPlayer = (DGlobalPlayer) api.getPlayerCache().get(player);
        String dungeonName = globalPlayer.getGroup().getDungeon().getName();
        long lastTime = globalPlayer.getData().getTimeLastStarted(dungeonName);
        if (lastTime == -1) {
            builder.append(DMessage.REQUIREMENT_TIME_SINCE_NEVER.getMessage()).color(ChatColor.GREEN);
        } else {
            ChatColor color = lastTime + time * HOUR_IN_MILLIS < System.currentTimeMillis() ? ChatColor.GREEN : ChatColor.DARK_RED;
            builder.append(SimpleDateUtil.ddMMMMyyyyhhmmss(lastTime)).color(color);
        }

        return builder.create();
    }

    @Override
    public void demand(Player player) {
    }

}
