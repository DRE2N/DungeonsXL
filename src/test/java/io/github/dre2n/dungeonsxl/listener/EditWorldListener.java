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
import io.github.dre2n.dungeonsxl.event.editworld.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Daniel Saukel
 */
public class EditWorldListener implements Listener {

    DungeonsXL plugin = DungeonsXL.getInstance();

    @EventHandler
    public void onGenerate(EditWorldGenerateEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "EditWorld (ID): " + event.getEditWorld().getId());
    }

    @EventHandler
    public void onLoad(EditWorldLoadEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "EditWorld (name): " + event.getName());
    }

    @EventHandler
    public void onSave(EditWorldGenerateEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "EditWorld (ID): " + event.getEditWorld().getId());
    }

    @EventHandler
    public void onUnload(EditWorldUnloadEvent event) {
        MessageUtil.log(plugin, "&b== " + event.getEventName() + "==");
        MessageUtil.log(plugin, "EditWorld (ID): " + event.getEditWorld().getId());
        MessageUtil.log(plugin, "Save?: " + event.getSave());
    }

}
