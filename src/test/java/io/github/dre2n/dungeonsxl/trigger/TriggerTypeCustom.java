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
package io.github.dre2n.dungeonsxl.trigger;

import io.github.dre2n.dungeonsxl.trigger.Trigger;
import io.github.dre2n.dungeonsxl.trigger.TriggerType;

/**
 * @author Daniel Saukel
 */
public enum TriggerTypeCustom implements TriggerType {

    CUSTOM("C", CustomTrigger.class);

    private String identifier;
    private Class<? extends Trigger> handler;

    TriggerTypeCustom(String identifier, Class<? extends Trigger> handler) {
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
