/*
 * Copyright (C) 2014-2020 Daniel Saukel
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
package de.erethon.dungeonsxl.api.sign;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.Trigger;
import de.erethon.dungeonsxl.api.world.GameWorld;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

/**
 * Skeletal implementation of {@link DungeonSign}.
 *
 * @author Daniel Saukel
 */
public abstract class AbstractDSign implements DungeonSign {

    public static final String ERROR_0 = ChatColor.DARK_RED + "## ERROR ##";
    public static final String ERROR_1 = ChatColor.WHITE + "Please";
    public static final String ERROR_2 = ChatColor.WHITE + "contact an";
    public static final String ERROR_3 = ChatColor.WHITE + "Admin!";

    protected DungeonsAPI api;
    private Sign sign;
    private String[] lines;
    private GameWorld gameWorld;
    private Set<Trigger> triggers = new HashSet<>();
    private boolean initialized;
    private boolean erroneous;

    protected AbstractDSign(DungeonsAPI api, Sign sign, String[] lines, GameWorld gameWorld) {
        this.api = api;
        this.sign = sign;
        this.lines = lines;
        this.gameWorld = gameWorld;
    }

    @Override
    public Sign getSign() {
        return sign;
    }

    @Override
    public String[] getLines() {
        return lines;
    }

    @Override
    public GameWorld getGameWorld() {
        return gameWorld;
    }

    @Override
    public Set<Trigger> getTriggers() {
        return triggers;
    }

    @Override
    public void addTrigger(Trigger trigger) {
        triggers.add(trigger);
    }

    @Override
    public void removeTrigger(Trigger trigger) {
        triggers.remove(trigger);
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean setToAir() {
        sign.getBlock().setType(VanillaItem.AIR.getMaterial());
        return true;
    }

    @Override
    public boolean isErroneous() {
        return erroneous;
    }

    @Override
    public void markAsErroneous(String reason) {
        erroneous = true;
        sign.setLine(0, ERROR_0);
        sign.setLine(1, ERROR_1);
        sign.setLine(2, ERROR_2);
        sign.setLine(3, ERROR_3);
        sign.update();

        MessageUtil.log(api, "&4A sign at &6" + sign.getX() + ", " + sign.getY() + ", " + sign.getZ() + "&4 is erroneous!");
        MessageUtil.log(api, getName() + ": " + reason);
    }

}
