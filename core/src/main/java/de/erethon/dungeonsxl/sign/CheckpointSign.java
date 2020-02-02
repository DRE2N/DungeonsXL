/*
 * Copyright (C) 2012-2020 Frank Baumann
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
package de.erethon.dungeonsxl.sign;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class CheckpointSign extends DSign {

    // Variables
    private boolean initialized;
    private CopyOnWriteArrayList<DGamePlayer> done = new CopyOnWriteArrayList<>();

    public CheckpointSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        getSign().getBlock().setType(VanillaItem.AIR.getMaterial());

        initialized = true;
    }

    @Override
    public void onTrigger() {
        if (!initialized) {
            return;
        }

        for (DGamePlayer dplayer : DGamePlayer.getByWorld(getGameWorld().getWorld())) {
            dplayer.setCheckpoint(getSign().getLocation());
            MessageUtil.sendMessage(dplayer.getPlayer(), DMessage.PLAYER_CHECKPOINT_REACHED.getMessage());
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
                MessageUtil.sendMessage(player, DMessage.PLAYER_CHECKPOINT_REACHED.getMessage());
            }
        }

        if (done.size() >= DGamePlayer.getByWorld(getGameWorld().getWorld()).size()) {
            remove();
        }

        return true;
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.CHECKPOINT;
    }

}
