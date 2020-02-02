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
package de.erethon.dungeonsxl.sign.lobby;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.game.GameType;
import de.erethon.dungeonsxl.game.GameTypeDefault;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.sign.DSign;
import de.erethon.dungeonsxl.sign.DSignType;
import de.erethon.dungeonsxl.sign.DSignTypeDefault;
import de.erethon.dungeonsxl.trigger.InteractTrigger;
import de.erethon.dungeonsxl.util.ProgressBar;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class ReadySign extends DSign {

    private GameType gameType;
    private double autoStart = -1;
    private boolean triggered = false;
    private ProgressBar bar;

    public ReadySign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    /**
     * @return the gameType
     */
    public GameType getGameType() {
        return gameType;
    }

    /**
     * @param gameType the gameType to set
     */
    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    /**
     * @return the time until the game starts automatically; -1 for no auto start
     */
    public double getTimeToAutoStart() {
        return autoStart;
    }

    /**
     * @param time the time in seconds until the game starts automatically; -1 for no auto start
     */
    public void setTimeToAutoStart(double time) {
        autoStart = time;
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        if (plugin.getGameTypeCache().getBySign(this) != null) {
            gameType = plugin.getGameTypeCache().getBySign(this);

        } else {
            gameType = GameTypeDefault.CUSTOM;
        }

        if (!lines[2].isEmpty()) {
            autoStart = NumberUtil.parseDouble(lines[2], -1);
        }

        if (!getTriggers().isEmpty()) {
            getSign().getBlock().setType(VanillaItem.AIR.getMaterial());
            return;
        }

        InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), getGameWorld());
        if (trigger != null) {
            trigger.addListener(this);
            addTrigger(trigger);
        }

        getSign().setLine(0, ChatColor.DARK_BLUE + "############");
        getSign().setLine(1, DMessage.SIGN_READY.getMessage());
        getSign().setLine(2, ChatColor.DARK_RED + gameType.getSignName());
        getSign().setLine(3, ChatColor.DARK_BLUE + "############");
        getSign().update();
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        ready(DGamePlayer.getByPlayer(player));

        if (!triggered && autoStart >= 0) {
            triggered = true;

            if (!DGroup.getByPlayer(player).isPlaying()) {
                bar = new ProgressBar(getGame().getPlayers(), (int) Math.ceil(autoStart)) {
                    @Override
                    public void onFinish() {
                        onTrigger();
                    }
                };
                bar.send(plugin);
            }
        }

        return true;
    }

    @Override
    public void onTrigger() {
        if (getGame() == null) {
            return;
        }

        if (bar != null) {
            bar.cancel();
        }

        for (Player player : getGame().getPlayers()) {
            ready(DGamePlayer.getByPlayer(player));
        }
    }

    private void ready(DGamePlayer dPlayer) {
        if (dPlayer == null || dPlayer.isReady()) {
            return;
        }

        if (getGameWorld().getClassesSigns().isEmpty() || dPlayer.getDClass() != null) {
            GameType forced = null;
            if (getGameWorld().getConfig() != null) {
                forced = getGameWorld().getConfig().getForcedGameType();
            }
            boolean ready = dPlayer.ready(forced == null ? gameType : forced);
            if (ready && bar != null) {
                bar.cancel();
            }
        }

        dPlayer.sendMessage((dPlayer.isReady() ? DMessage.PLAYER_READY : DMessage.ERROR_READY).getMessage());
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.READY;
    }

}
