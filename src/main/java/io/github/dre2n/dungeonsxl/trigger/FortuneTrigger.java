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
package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.commons.misc.NumberUtil;
import io.github.dre2n.dungeonsxl.event.trigger.TriggerActionEvent;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import org.bukkit.Bukkit;

/**
 * @author Daniel Saukel
 */
public class FortuneTrigger extends Trigger {

    private TriggerType type = TriggerTypeDefault.FORTUNE;

    private double chance = 0;

    public FortuneTrigger(double chance) {
        this.chance = chance;
    }

    /* Getters and setters */
    /**
     * @return the chance
     */
    public double getChance() {
        return chance;
    }

    /**
     * @param chance
     * the chance to set
     */
    public void setChance(double chance) {
        this.chance = chance;
    }

    @Override
    public TriggerType getType() {
        return type;
    }

    /* Actions */
    public void onTrigger() {
        int random = NumberUtil.generateRandomInt(0, 100);
        if (chance * 100 < random) {
            return;
        }

        TriggerActionEvent event = new TriggerActionEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        setTriggered(true);
        updateDSigns();
    }

    /* Statics */
    public static FortuneTrigger getOrCreate(String chance, DGameWorld gameWorld) {
        return new FortuneTrigger(NumberUtil.parseDouble(chance));
    }

}
