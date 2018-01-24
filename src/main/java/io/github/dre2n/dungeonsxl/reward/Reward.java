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
package io.github.dre2n.dungeonsxl.reward;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.event.reward.RewardRegistrationEvent;
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

    DungeonsXL plugin = DungeonsXL.getInstance();

    public static Reward create(RewardType type) {
        Reward reward = null;

        try {
            Constructor<? extends Reward> constructor = type.getHandler().getConstructor();
            reward = constructor.newInstance();

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

}
