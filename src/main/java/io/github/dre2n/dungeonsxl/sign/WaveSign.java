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

import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.dungeonsxl.game.GameWorld;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class WaveSign extends DSign {

    private DSignType type = DSignTypeDefault.WAVE;

    private double mobCountIncreaseRate;

    public WaveSign(Sign sign, GameWorld gameWorld) {
        super(sign, gameWorld);
    }

    /**
     * @return the mobCountIncreaseRate
     */
    public double getMobCountIncreaseRate() {
        return mobCountIncreaseRate;
    }

    /**
     * @param mobCountIncreaseRate
     * the mobCountIncreaseRate to set
     */
    public void setMobCountIncreaseRate(double mobCountIncreaseRate) {
        this.mobCountIncreaseRate = mobCountIncreaseRate;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        String[] lines = getSign().getLines();
        if (!lines[1].isEmpty()) {
            mobCountIncreaseRate = NumberUtil.parseDouble(lines[1], 2);
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
        getSign().setLine(1, ChatColor.DARK_GREEN + "START");
        getSign().setLine(2, ChatColor.DARK_GREEN + "NEXT WAVE");
        getSign().setLine(3, ChatColor.DARK_BLUE + "############");
        getSign().update();
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        DGroup dGroup = DGroup.getByPlayer(player);
        if (dGroup == null) {
            return true;
        }

        if (getGameWorld() == null) {
            return true;
        }

        dGroup.finishWave(mobCountIncreaseRate);
        return true;
    }

    @Override
    public void onTrigger() {
        for (DGroup dGroup : plugin.getDGroups()) {
            dGroup.finishWave(mobCountIncreaseRate);
        }
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
