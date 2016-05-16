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

import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class CheckpointSign extends DSign {

    private DSignType type = DSignTypeDefault.CHECKPOINT;

    // Variables
    private boolean initialized;
    private CopyOnWriteArrayList<DGamePlayer> done = new CopyOnWriteArrayList<>();

    public CheckpointSign(Sign sign, String[] lines, GameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        getSign().getBlock().setType(Material.AIR);

        initialized = true;
    }

    @Override
    public void onTrigger() {
        if (!initialized) {
            return;
        }

        for (DGamePlayer dplayer : DGamePlayer.getByWorld(getGameWorld().getWorld())) {
            dplayer.setCheckpoint(getSign().getLocation());
            MessageUtil.sendMessage(dplayer.getPlayer(), plugin.getMessageConfig().getMessage(DMessages.PLAYER_CHECKPOINT_REACHED));
        }

        remove();
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        if (!initialized) {
            return true;
        }

        DGamePlayer dplayer = DGamePlayer.getByPlayer(player);
        if (dplayer != null) {
            if (!done.contains(dplayer)) {
                done.add(dplayer);
                dplayer.setCheckpoint(getSign().getLocation());
                MessageUtil.sendMessage(player, plugin.getMessageConfig().getMessage(DMessages.PLAYER_CHECKPOINT_REACHED));
            }
        }

        if (done.size() >= DGamePlayer.getByWorld(getGameWorld().getWorld()).size()) {
            remove();
        }

        return true;
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
