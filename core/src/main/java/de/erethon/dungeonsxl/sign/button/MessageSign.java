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
import de.erethon.dungeonsxl.api.dungeon.GameRule;
import de.erethon.dungeonsxl.api.sign.Button;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public abstract class MessageSign extends Button {

    protected String text = "UNKNOWN MESSAGE";

    public MessageSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isOnDungeonInit() {
        return true;
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
        return !getLine(1).isEmpty();
    }

    @Override
    public void initialize() {
        String text = getGameWorld().getDungeon().getRules().getState(GameRule.MESSAGES).get(NumberUtil.parseInt(getLine(1)));
        if (text != null) {
            this.text = text;
        } else {
            markAsErroneous("Unknown message, ID: " + getLine(1));
        }
    }

    @Override
    public boolean push(Player player) {
        sendMessage(player);
        return true;
    }

    @Override
    public void push() {
        for (Player player : getGameWorld().getWorld().getPlayers()) {
            sendMessage(player);
        }
        getGameWorld().removeDungeonSign(this);
    }

    public abstract void sendMessage(Player player);

}
