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
import de.erethon.commons.misc.EnumUtil;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGroup;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class LivesModifierSign extends DSign {

    public enum Target {
        GAME,
        GROUP,
        PLAYER,
    }

    private int lives;
    private Target target;

    public LivesModifierSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    /* Getters and setters */
    /**
     * @return the lives to add / remove
     */
    public int getLives() {
        return lives;
    }

    /**
     * @param lives the lives to add / remove
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /* Actions */
    @Override
    public boolean check() {
        return NumberUtil.parseInt(lines[1]) != 0;
    }

    @Override
    public void onInit() {
        lives = NumberUtil.parseInt(lines[1]);
        if (EnumUtil.isValidEnum(Target.class, lines[2].toUpperCase())) {
            target = Target.valueOf(lines[2].toUpperCase());
        }

        getSign().getBlock().setType(VanillaItem.AIR.getMaterial());
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        switch (target) {
            case GAME:
                for (Player gamePlayer : Game.getByPlayer(player).getPlayers()) {
                    DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
                    if (gamePlayer != null) {
                        modifyLives(dPlayer);
                    }
                }
                break;

            case GROUP:
                modifyLives(DGroup.getByPlayer(player));
                break;

            case PLAYER:
                modifyLives(DGamePlayer.getByPlayer(player));
        }

        return true;
    }

    public void modifyLives(DGamePlayer dPlayer) {
        dPlayer.setLives(dPlayer.getLives() + lives);
        if (lives > 0) {
            MessageUtil.sendMessage(dPlayer.getPlayer(), DMessage.PLAYER_LIVES_ADDED.getMessage(String.valueOf(lives)));

        } else {
            MessageUtil.sendMessage(dPlayer.getPlayer(), DMessage.PLAYER_LIVES_REMOVED.getMessage(String.valueOf(-1 * lives)));
        }

        if (dPlayer.getLives() <= 0) {
            dPlayer.kill();
        }
    }

    public void modifyLives(DGroup dGroup) {
        dGroup.setLives(dGroup.getLives() + lives);
        if (lives > 0) {
            dGroup.sendMessage(DMessage.GROUP_LIVES_ADDED.getMessage(String.valueOf(lives)));

        } else {
            dGroup.sendMessage(DMessage.GROUP_LIVES_REMOVED.getMessage(String.valueOf(-1 * lives)));
        }
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.LIVES_MODIFIER;
    }

}
