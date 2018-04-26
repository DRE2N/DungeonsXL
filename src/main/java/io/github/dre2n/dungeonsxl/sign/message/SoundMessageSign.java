/*
 * Copyright (C) 2012-2018 Frank Baumann
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
package io.github.dre2n.dungeonsxl.sign.message;

import de.erethon.commons.compatibility.CompatibilityHandler;
import de.erethon.commons.compatibility.Internals;
import de.erethon.commons.misc.EnumUtil;
import de.erethon.commons.misc.NumberUtil;
import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.sign.DSignType;
import io.github.dre2n.dungeonsxl.sign.DSignTypeDefault;
import io.github.dre2n.dungeonsxl.world.DGameWorld;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class SoundMessageSign extends DSign {

    private DSignType type = DSignTypeDefault.SOUND_MESSAGE;

    // Variables
    private boolean initialized;
    private String sound;
    private SoundCategory category;
    private float volume;
    private float pitch;
    private CopyOnWriteArrayList<Player> done = new CopyOnWriteArrayList<>();

    public SoundMessageSign(Sign sign, String[] lines, DGameWorld gameWorld) {
        super(sign, lines, gameWorld);
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
            sound = lines[1];
            if (!lines[2].isEmpty()) {
                String[] args = lines[2].split(",");
                if (args.length >= 1 && args.length != 2 && Internals.andHigher(Internals.v1_11_R1).contains(CompatibilityHandler.getInstance().getInternals())) {
                    category = EnumUtil.getEnumIgnoreCase(SoundCategory.class, args[0]);
                    if (category == null) {
                        category = SoundCategory.MASTER;
                    }
                }
                if (args.length == 2) {
                    volume = (float) NumberUtil.parseDouble(args[0], 5.0);
                    pitch = (float) NumberUtil.parseDouble(args[1], 1.0);
                } else if (args.length == 3) {
                    volume = (float) NumberUtil.parseDouble(args[1], 5.0);
                    pitch = (float) NumberUtil.parseDouble(args[2], 1.0);
                }
            }
            getSign().getBlock().setType(Material.AIR);
            initialized = true;
        } else {
            markAsErroneous();
        }
    }

    @Override
    public void onTrigger() {
        if (initialized) {
            for (Player player : getGameWorld().getWorld().getPlayers()) {
                player.playSound(getSign().getLocation(), sound, category, volume, pitch);
            }
            remove();
        }
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        if (initialized) {
            if (!done.contains(player)) {
                done.add(player);
                if (Internals.andHigher(Internals.v1_11_R1).contains(CompatibilityHandler.getInstance().getInternals())) {
                    player.playSound(getSign().getLocation(), sound, category, volume, pitch);
                } else {
                    player.playSound(getSign().getLocation(), sound, volume, pitch);
                }
            }

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
