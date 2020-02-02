/*
 * Copyright (C) 2012-2020 Frank Baumann
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
package de.erethon.dungeonsxl.reward;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.event.reward.RewardRegistrationEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Extend this to create a custom Reward.
 *
 * @author Daniel Saukel
 */
public abstract class Reward {

    protected DungeonsXL plugin;

    protected Reward(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    public static Reward create(DungeonsXL plugin, RewardType type) {
        Reward reward = null;

        try {
            Constructor<? extends Reward> constructor = type.getHandler().getConstructor(DungeonsXL.class);
            reward = constructor.newInstance(plugin);

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
            MessageUtil.log("An error occurred while accessing the handler class of the reward " + type.getIdentifier() + ": " + exception.getClass().getSimpleName());
            if (!(type instanceof RewardTypeDefault)) {
                MessageUtil.log("Please note that this reward is an unsupported feature added by an addon!");
            }
        }

        RewardRegistrationEvent event = new RewardRegistrationEvent(reward);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        return reward;
    }

    /* Abstracts */
    public abstract void giveTo(Player player);

    public abstract RewardType getType();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{type=" + getType() + "}";
    }

}
