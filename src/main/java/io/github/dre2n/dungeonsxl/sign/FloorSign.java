/*
 * Copyright (C) 2012-2016 Frank Baumann
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

import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DPlayer;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class FloorSign extends DSign {

    private DSignType type = DSignTypeDefault.FLOOR;

    private String floor;

    public FloorSign(Sign sign, GameWorld gameWorld) {
        super(sign, gameWorld);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        String[] lines = getSign().getLines();
        if (!lines[1].isEmpty()) {
            floor = lines[1];
        }

        if (!getTriggers().isEmpty()) {
            getSign().getBlock().setType(Material.AIR);
            return;
        }

        InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), getGameWorld());
        if (trigger != null) {
            trigger.addListener(this);
            addTrigger(trigger);
        }

        getSign().setLine(0, ChatColor.DARK_BLUE + "############");
        getSign().setLine(1, ChatColor.DARK_GREEN + "ENTER");
        if (floor == null) {
            getSign().setLine(2, ChatColor.DARK_GREEN + "NEXT FLOOR");
        } else {
            getSign().setLine(2, ChatColor.DARK_GREEN + floor.replaceAll("_", " "));
        }
        getSign().setLine(3, ChatColor.DARK_BLUE + "############");
        getSign().update();
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        DPlayer dPlayer = DPlayer.getByPlayer(player);
        if (dPlayer != null) {
            if (!dPlayer.isFinished()) {
                dPlayer.finishFloor(floor);
            }
        }

        return true;
    }

    @Override
    public void onTrigger() {
        for (DPlayer dPlayer : plugin.getDPlayers()) {
            dPlayer.finish();
        }
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
