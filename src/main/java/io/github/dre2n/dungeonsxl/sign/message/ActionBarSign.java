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
package io.github.dre2n.dungeonsxl.sign.message;

import io.github.dre2n.commons.chat.MessageUtil;
import io.github.dre2n.dungeonsxl.sign.DSignType;
import io.github.dre2n.dungeonsxl.sign.DSignTypeDefault;
import io.github.dre2n.dungeonsxl.sign.PerPlayerSign;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class ActionBarSign extends PerPlayerSign {

    private DSignType type = DSignTypeDefault.ACTION_BAR;

    private String text;

    public ActionBarSign(Sign sign, String[] lines, DGameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    /* Getters and setters*/
    /**
     * @return
     * the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text
     * the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /* Actions */
    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        text = lines[1];
        text += lines[2];
        getSign().getBlock().setType(Material.AIR);
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        if (!super.onPlayerTrigger(player)) {
            return false;
        }
        MessageUtil.sendActionBarMessage(player, text);
        return true;
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
