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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.player.GamePlayer;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.sign.Button;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.trigger.InteractTrigger;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.util.commons.misc.ProgressBar;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class ReadySign extends Button {

    private double autoStart = -1;
    private boolean triggered = false;
    private ProgressBar bar;

    public ReadySign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    public double getTimeToAutoStart() {
        return autoStart;
    }

    public void setTimeToAutoStart(double time) {
        autoStart = time;
    }

    @Override
    public String getName() {
        return "Ready";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".ready";
    }

    @Override
    public boolean isOnDungeonInit() {
        return true;
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public boolean isSetToAir() {
        return false;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void initialize() {
        ((DGameWorld) getGameWorld()).setReadySign(this);
        if (!getLine(2).isEmpty()) {
            autoStart = NumberUtil.parseDouble(getLine(2), -1);
        }

        if (!getTriggers().isEmpty()) {
            setToAir();
            return;
        }

        InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), (DGameWorld) getGameWorld());
        if (trigger != null) {
            trigger.addListener(this);
            addTrigger(trigger);
        }

        getSign().setLine(0, ChatColor.DARK_BLUE + "############");
        getSign().setLine(1, DMessage.SIGN_READY.getMessage());
        getSign().setLine(2, "");
        getSign().setLine(3, ChatColor.DARK_BLUE + "############");
        getSign().update();
    }

    @Override
    public void push() {
        if (getGame() == null) {
            return;
        }

        if (bar != null) {
            bar.cancel();
        }

        readyAll();
    }

    @Override
    public boolean push(Player player) {
        GamePlayer gamePlayer = api.getPlayerCache().getGamePlayer(player);
        ready(gamePlayer);

        if (!triggered && autoStart >= 0) {
            triggered = true;

            if (gamePlayer != null && !gamePlayer.getGroup().isPlaying()) {
                bar = new ProgressBar(getGame().getPlayers(), (int) Math.ceil(autoStart)) {
                    @Override
                    public void onFinish() {
                        push();
                    }
                };
                bar.send(api);
            }
        }

        return true;
    }

    private void readyAll() {
        for (PlayerGroup group : getGame().getGroups()) {
            for (UUID memberId : group.getMembers()) {
                Player player = Bukkit.getPlayer(memberId);
                if (player != null) {
                    GamePlayer gamePlayer = api.getPlayerCache().getGamePlayer(player);
                    if (gamePlayer == null) {
                        gamePlayer = new DGamePlayer((DungeonsXL) api, player, getGameWorld());
                    }
                    ready(gamePlayer);
                } else {
                    group.getMembers().remove(memberId);
                }
            }
        }
    }

    private void ready(GamePlayer player) {
        if (player == null) {
            return;
        }
        boolean wasReady = player.isReady();

        if (!getGameWorld().areClassesEnabled() || player.getPlayerClass() != null) {
            if (player.ready()) {
                getGame().start();
                if (bar != null) {
                    bar.cancel();
                }
            }
        }

        if (!wasReady) {
            if (player.isReady()) {
                player.sendMessage(DMessage.PLAYER_READY.getMessage());
            } else if (getGameWorld().areClassesEnabled()) {
                player.sendMessage(DMessage.ERROR_READY.getMessage());
            }
        }
    }

}
