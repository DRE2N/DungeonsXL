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

import io.github.dre2n.commons.util.EnumUtil;
import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.game.Game;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.Material;
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

    private DSignType type = DSignTypeDefault.LIVES_MODIFIER;

    private int lives;
    private Target target;

    public LivesModifierSign(Sign sign, String[] lines, GameWorld gameWorld) {
        super(sign, lines, gameWorld);
    }

    /* Getters and setters */
    /**
     * @return the lives to add / remove
     */
    public int getLives() {
        return lives;
    }

    /**
     * @param lives
     * the lives to add / remove
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

        getSign().getBlock().setType(Material.AIR);
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
                for (DGamePlayer dPlayer : DGroup.getByPlayer(player).getDGamePlayers()) {
                    modifyLives(dPlayer);
                }
                break;

            case PLAYER:
                modifyLives(DGamePlayer.getByPlayer(player));
        }

        return true;
    }

    public void modifyLives(DGamePlayer dPlayer) {
        dPlayer.setLives(dPlayer.getLives() + lives);
        if (lives > 0) {
            MessageUtil.sendMessage(dPlayer.getPlayer(), DMessages.PLAYER_LIVES_ADDED.getMessage(String.valueOf(lives)));

        } else {
            MessageUtil.sendMessage(dPlayer.getPlayer(), DMessages.PLAYER_LIVES_REMOVED.getMessage(String.valueOf(-1 * lives)));
        }

        if (dPlayer.getLives() <= 0) {
            dPlayer.kill();
        }
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
