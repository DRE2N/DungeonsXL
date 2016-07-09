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
import io.github.dre2n.dungeonsxl.event.gameworld.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Daniel Saukel
 */
public class GameWorldListener implements Listener {

    DungeonsXL plugin = DungeonsXL.getInstance();

    @EventHandler
    public void onLoad(GameWorldLoadEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "GameWorld (name): " + event.getName());
    }

    @EventHandler
    public void onGenerate(GameWorldStartGameEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "GameWorld (ID): " + event.getGameWorld().getId());
        MessageUtil.log(plugin, "Game: " + event.getGame());
    }

    @EventHandler
    public void onUnload(GameWorldUnloadEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "GameWorld (ID): " + event.getGameWorld().getId());
    }

}
