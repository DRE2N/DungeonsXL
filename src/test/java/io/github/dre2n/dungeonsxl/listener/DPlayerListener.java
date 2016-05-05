/*
 * Copyright (C) 2016 Daniel Saukel
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
package io.github.dre2n.dungeonsxl.listener;

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.event.dplayer.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Daniel Saukel
 */
public class DPlayerListener implements Listener {

    DungeonsXL plugin = DungeonsXL.getInstance();

    @EventHandler
    public void onDeath(DPlayerDeathEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "DPlayer: " + event.getDPlayer().getPlayer().getName());
        MessageUtil.log(plugin, "Lost lives: " + event.getLostLives());
    }

    @EventHandler
    public void onEscape(DPlayerEscapeEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "DPlayer: " + event.getDPlayer().getPlayer().getName());
    }

    @EventHandler
    public void onFinish(DPlayerFinishEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "DPlayer: " + event.getDPlayer().getPlayer().getName());
        MessageUtil.log(plugin, "Player has to wait: " + event.getHasToWait());
    }

    @EventHandler
    public void onJoinDGroup(DPlayerJoinDGroupEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "DPlayer: " + event.getDPlayer().getPlayer().getName());
        MessageUtil.log(plugin, "DGroup: " + event.getDGroup().getName());
    }

    @EventHandler
    public void onKick(DPlayerKickEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "DPlayer: " + event.getDPlayer().getPlayer().getName());
    }

    @EventHandler
    public void onLeaveDGroup(DPlayerLeaveDGroupEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "DPlayer: " + event.getDPlayer().getPlayer().getName());
        MessageUtil.log(plugin, "DGroup: " + event.getDGroup().getName());
    }

    /*This would cause waaay too much console spam...
    @EventHandler
    public void onUpdate(DPlayerUpdateEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "DGamePlayer: " + event.getDPlayer().getPlayer().getName());
    }*/
}
