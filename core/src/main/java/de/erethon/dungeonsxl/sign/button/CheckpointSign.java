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
import de.erethon.dungeonsxl.api.player.GamePlayer;
import de.erethon.dungeonsxl.api.player.InstancePlayer;
import de.erethon.dungeonsxl.api.sign.Button;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class CheckpointSign extends Button {

    private List<GamePlayer> done = new ArrayList<>();

    public CheckpointSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "Checkpoint";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".checkpoint";
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
        return true;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void push() {
        for (InstancePlayer instancePlayer : getGameWorld().getPlayers()) {
            GamePlayer gamePlayer = (GamePlayer) instancePlayer;
            if (done.contains(gamePlayer)) {
                continue;
            }
            gamePlayer.setLastCheckpoint(getSign().getLocation());
            gamePlayer.sendMessage(DMessage.PLAYER_CHECKPOINT_REACHED.getMessage());
        }

        getGameWorld().removeDungeonSign(this);
    }

    @Override
    public boolean push(Player player) {
        GamePlayer gamePlayer = api.getPlayerCache().getGamePlayer(player);
        if (!done.contains(gamePlayer)) {
            done.add(gamePlayer);
            gamePlayer.setLastCheckpoint(getSign().getLocation());
            MessageUtil.sendMessage(player, DMessage.PLAYER_CHECKPOINT_REACHED.getMessage());
        }

        if (done.size() >= getGameWorld().getPlayers().size()) {
            getGameWorld().removeDungeonSign(this);
        }
        return true;
    }

}
