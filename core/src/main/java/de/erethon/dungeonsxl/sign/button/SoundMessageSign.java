/*
 * Copyright (C) 2012-2022 Frank Baumann
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
package de.erethon.dungeonsxl.sign.button;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.sign.Button;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.compatibility.Internals;
import de.erethon.dungeonsxl.util.commons.misc.EnumUtil;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import org.bukkit.SoundCategory;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class SoundMessageSign extends Button {

    private String sound;
    private SoundCategory category;
    private float volume;
    private float pitch;

    public SoundMessageSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "SoundMSG";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".soundmsg";
    }

    @Override
    public boolean isOnDungeonInit() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isSetToAir() {
        return true;
    }

    @Override
    public boolean validate() {
        if (getLine(1).isEmpty()) {
            markAsErroneous("1. Line is empty; expected input: sound name");
            return false;
        }

        return true;
    }

    @Override
    public void initialize() {
        sound = getLine(1);
        if (getLine(2).isEmpty()) {
            return;
        }

        String[] args = getLine(2).split(",");
        if (args.length >= 1 && args.length != 2 && Internals.isAtLeast(Internals.v1_11_R1)) {
            category = EnumUtil.getEnumIgnoreCase(SoundCategory.class, args[0]);
            if (category == null) {
                category = SoundCategory.MASTER;
            }
        }
        if (args.length == 2) {
            volume = (float) NumberUtil.parseDouble(args[0], 5.0);
            pitch = (float) NumberUtil.parseDouble(args[1], 1.0);
        } else if (args.length == 3) {
            volume = (float) NumberUtil.parseDouble(args[1], 5.0);
            pitch = (float) NumberUtil.parseDouble(args[2], 1.0);
        }
    }

    @Override
    public void push() {
        for (Player player : getGameWorld().getWorld().getPlayers()) {
            playSound(player);
        }
        getGameWorld().removeDungeonSign(this);
    }

    @Override
    public boolean push(Player player) {
        playSound(player);
        return true;
    }

    private void playSound(Player player) {
        if (Internals.isAtLeast(Internals.v1_11_R1)) {
            player.playSound(getSign().getLocation(), sound, category, volume, pitch);
        } else {
            player.playSound(getSign().getLocation(), sound, volume, pitch);
        }
    }

}
