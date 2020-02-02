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

/**
 * Default implementation of TriggerType.
 *
 * @author Daniel Saukel
 */
public enum TriggerTypeDefault implements TriggerType {

    DISTANCE("D", DistanceTrigger.class),
    FORTUNE("F", FortuneTrigger.class),
    INTERACT("I", InteractTrigger.class),
    MOB("M", MobTrigger.class),
    PROGRESS("P", ProgressTrigger.class),
    REDSTONE("R", RedstoneTrigger.class),
    SIGN("T", SignTrigger.class),
    USE_ITEM("U", UseItemTrigger.class),
    WAVE("W", WaveTrigger.class);

    private String identifier;
    private Class<? extends Trigger> handler;

    TriggerTypeDefault(String identifier, Class<? extends Trigger> handler) {
        this.identifier = identifier;
        this.handler = handler;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Class<? extends Trigger> getHandler() {
        return handler;
    }

}
