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
package de.erethon.dungeonsxl.trigger;

import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.event.trigger.TriggerRegistrationEvent;
import de.erethon.dungeonsxl.sign.DSign;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Extend this to create a custom Trigger.
 *
 * @author Frank Baumann, Daniel Saukel
 */
public abstract class Trigger {

    private boolean triggered;
    private Player player; // Holds Player for Player specific TriggerTypes

    private Set<DSign> dSigns = new HashSet<>();

    /**
     * @return the triggered
     */
    public boolean isTriggered() {
        return triggered;
    }

    /**
     * @param triggered the triggered to set
     */
    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @param player the player to set
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * @return the dSigns
     */
    public Set<DSign> getDSigns() {
        return dSigns;
    }

    /**
     * @param dSign the dSign to add
     */
    public void addDSign(DSign dSign) {
        dSigns.add(dSign);
    }

    /**
     * @param dSign the dSign to remove
     */
    public void removeDSign(DSign dSign) {
        dSigns.remove(dSign);
    }

    public void addListener(DSign dSign) {
        if (dSigns.isEmpty()) {
            register(dSign.getGameWorld());
        }
        dSigns.add(dSign);
    }

    public void removeListener(DSign dSign) {
        dSigns.remove(dSign);
        if (dSigns.isEmpty()) {
            unregister(dSign.getGameWorld());
        }
    }

    public void updateDSigns() {
        for (DSign dSign : dSigns.toArray(new DSign[dSigns.size()])) {
            dSign.onUpdate();
        }
    }

    public void register(DGameWorld gameWorld) {
        gameWorld.addTrigger(this);
    }

    public void unregister(DGameWorld gameWorld) {
        gameWorld.removeTrigger(this);
    }

    public static Trigger getOrCreate(DungeonsXL plugin, String identifier, String value, DSign dSign) {
        TriggerType type = plugin.getTriggerCache().getByIdentifier(identifier);
        Trigger trigger = null;

        if (type == TriggerTypeDefault.REDSTONE) {

            trigger = RedstoneTrigger.getOrCreate(dSign.getSign(), dSign.getGameWorld());

        } else if (type == TriggerTypeDefault.DISTANCE) {

            if (value != null) {
                trigger = new DistanceTrigger(NumberUtil.parseInt(value), dSign.getSign().getLocation());

            } else {
                trigger = new DistanceTrigger(dSign.getSign().getLocation());
            }

        } else if (type == TriggerTypeDefault.FORTUNE) {

            if (value != null) {
                trigger = new FortuneTrigger(NumberUtil.parseDouble(value));
            }

        } else if (type == TriggerTypeDefault.SIGN) {

            if (value != null) {
                trigger = SignTrigger.getOrCreate(NumberUtil.parseInt(value), dSign.getGameWorld());
            }

        } else if (type == TriggerTypeDefault.INTERACT) {

            if (value != null) {
                trigger = InteractTrigger.getOrCreate(NumberUtil.parseInt(value), dSign.getGameWorld());
            }

        } else if (type == TriggerTypeDefault.MOB) {

            if (value != null) {
                trigger = MobTrigger.getOrCreate(value, dSign.getGameWorld());
            }

        } else if (type == TriggerTypeDefault.PROGRESS) {

            if (value != null) {
                if (value.matches("[0-99]/[0-999]")) {
                    int floorCount = NumberUtil.parseInt(value.split("/")[0]);
                    int waveCount = NumberUtil.parseInt(value.split("/")[1]);
                    trigger = ProgressTrigger.getOrCreate(floorCount, waveCount, dSign.getGameWorld());

                } else {
                    trigger = ProgressTrigger.getOrCreate(plugin, value, dSign.getGameWorld());
                }
            }

        } else if (type == TriggerTypeDefault.USE_ITEM) {

            if (value != null) {
                trigger = UseItemTrigger.getOrCreate(plugin, value, dSign.getGameWorld());
            }

        } else if (type == TriggerTypeDefault.WAVE) {

            if (value != null) {
                trigger = WaveTrigger.getOrCreate(NumberUtil.parseDouble(value, 1), dSign.getGameWorld());
            }

        } else if (type != null) {

            Method method;
            try {
                method = type.getHandler().getDeclaredMethod("getOrCreate", String.class, DGameWorld.class);
                trigger = (Trigger) method.invoke(value, dSign.getGameWorld());

            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
                MessageUtil.log("An error occurred while accessing the handler class of the sign " + type.getIdentifier() + ": " + exception.getClass().getSimpleName());
                if (!(type instanceof TriggerTypeDefault)) {
                    MessageUtil.log("Please note that this trigger is an unsupported feature added by an addon!");
                }
            }
        }

        TriggerRegistrationEvent event = new TriggerRegistrationEvent(trigger);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        return trigger;
    }

    /* Abstracts */
    public abstract TriggerType getType();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{type=" + getType() + "}";
    }

}
