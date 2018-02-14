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
package io.github.dre2n.dungeonsxl.sign.lobby;

import io.github.dre2n.dungeonsxl.player.DClass;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.sign.DSignType;
import io.github.dre2n.dungeonsxl.sign.DSignTypeDefault;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ClassesSign extends DSign {

    private DSignType type = DSignTypeDefault.CLASSES;

    private DClass dClass;

    public ClassesSign(Sign sign, String[] lines, DGameWorld gameWorld) {
        super(sign, lines, gameWorld);
        dClass = plugin.getDClasses().getByName(sign.getLine(1));
    }

    /* Getters and setters */
    /**
     * @return the DClass of the sign
     */
    public DClass getDClass() {
        return dClass;
    }

    /**
     * @param dClass
     * the DClass to set
     */
    public void setDClass(DClass dClass) {
        this.dClass = dClass;
    }

    /* Actions */
    @Override
    public boolean check() {
        return plugin.getDClasses().getByName(lines[1]) != null;
    }

    @Override
    public void onInit() {
        if (dClass != null) {
            getSign().setLine(0, ChatColor.DARK_BLUE + "############");
            getSign().setLine(1, ChatColor.DARK_GREEN + dClass.getName());
            getSign().setLine(2, "");
            getSign().setLine(3, ChatColor.DARK_BLUE + "############");
            getSign().update();
            getGameWorld().getClassesSigns().add(getSign());

        } else {
            markAsErroneous();
        }
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
