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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.player.DPermissions;

/**
 * @author Daniel Saukel
 */
public enum DSignTypeCustom implements DSignType {

    CUSTOM("Custom", "custom", false, false, CustomSign.class);

    private String name;
    private String buildPermission;
    private boolean onDungeonInit;
    private boolean isProtected;
    private Class<? extends DSign> handler;

    DSignTypeCustom(String name, String buildPermission, boolean onDungeonInit, boolean isProtected, Class<? extends DSign> handler) {
        this.name = name;
        this.buildPermission = buildPermission;
        this.onDungeonInit = onDungeonInit;
        this.isProtected = isProtected;
        this.handler = handler;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getBuildPermission() {
        return DPermissions.SIGN.getNode() + "." + buildPermission;
    }

    @Override
    public boolean isOnDungeonInit() {
        return onDungeonInit;
    }

    @Override
    public boolean isProtected() {
        return isProtected;
    }

    @Override
    public Class<? extends DSign> getHandler() {
        return handler;
    }

}
