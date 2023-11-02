/*
 * Copyright (C) 2014-2023 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api.trigger;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.event.trigger.TriggerUnregistrationEvent;
import de.erethon.dungeonsxl.api.world.GameWorld;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Skeletal implementation of {@link Trigger}.
 *
 * @author Daniel Saukel, Frank Baumann, Milan Albrecht
 */
public abstract class AbstractTrigger implements Trigger {

    private Set<TriggerListener> listeners = new HashSet<>();
    private GameWorld gameWorld;
    private LogicalExpression expression;
    private String value;
    private Player player;

    protected AbstractTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        listeners.add(owner);
        gameWorld = owner.getGameWorld();
        this.expression = expression;
        expression.setTrigger(this);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public GameWorld getGameWorld() {
        return gameWorld;
    }

    @Override
    public boolean isTriggered() {
        return expression.isSatisfied();
    }

    @Override
    public void setTriggered(boolean triggered) {
        expression.setSatisfied(triggered);
    }

    @Override
    public Player getTriggeringPlayer() {
        return player;
    }

    @Override
    public void setTriggeringPlayer(Player player) {
        this.player = player;
    }

    @Override
    public Set<TriggerListener> getListeners() {
        return listeners;
    }

    @Override
    public boolean addListener(TriggerListener owner) {
        return listeners.add(owner);
    }

    @Override
    public boolean removeListener(TriggerListener listener) {
        return listeners.remove(listener);
    }

    @Override
    public boolean unregisterTrigger() {
        TriggerUnregistrationEvent event = new TriggerUnregistrationEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            return gameWorld.unregisterTrigger(this);
        }
        return false;
    }

    @Override
    public void updateListeners() {
        for (TriggerListener dSign : listeners.toArray(TriggerListener[]::new)) {
            dSign.updateTriggers(this);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{expression=" + expression + "}";
    }

}
