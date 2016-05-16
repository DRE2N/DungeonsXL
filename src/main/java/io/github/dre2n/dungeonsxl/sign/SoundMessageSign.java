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
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class SoundMessageSign extends DSign {

    private DSignType type = DSignTypeDefault.SOUND_MESSAGE;

    // Variables
    private boolean initialized;
    private String msg;
    private CopyOnWriteArrayList<Player> done = new CopyOnWriteArrayList<>();

    public SoundMessageSign(Sign sign, GameWorld gameWorld) {
        super(sign, gameWorld);
    }

    @Override
    public boolean check() {
        if (getSign().getLine(1).isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public void onInit() {
        if (!lines[1].isEmpty()) {
            String msg = getGame().getRules().getMsg(NumberUtil.parseInt(lines[1]), true);
            if (msg != null) {
                this.msg = msg;
                getSign().getBlock().setType(Material.AIR);
            }
        }

        initialized = true;
    }

    @Override
    public void onTrigger() {
        if (initialized) {
            remove();
        }
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        if (initialized) {
            remove();
            if (done.size() >= getGameWorld().getWorld().getPlayers().size()) {
                remove();
            }
        }

        return true;
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
