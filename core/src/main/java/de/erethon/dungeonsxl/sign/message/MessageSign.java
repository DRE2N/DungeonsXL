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
package de.erethon.dungeonsxl.sign.message;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.sign.DSign;
import de.erethon.dungeonsxl.sign.DSignType;
import de.erethon.dungeonsxl.sign.DSignTypeDefault;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class MessageSign extends DSign {

    // Variables
    private String msg = "UNKNOWN MESSAGE";
    private boolean initialized;
    private CopyOnWriteArrayList<Player> done = new CopyOnWriteArrayList<>();

    public MessageSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
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
            String msg = getGame().getRules().getMessage(NumberUtil.parseInt(lines[1]));
            if (msg != null) {
                this.msg = msg;
            }
        }

        getSign().getBlock().setType(VanillaItem.AIR.getMaterial());
        initialized = true;
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        if (!initialized) {
            return true;
        }

        if (!done.contains(player)) {
            MessageUtil.sendMessage(player, msg);
            done.add(player);
        }

        if (done.size() >= getGameWorld().getWorld().getPlayers().size()) {
            remove();
        }

        return true;
    }

    @Override
    public void onTrigger() {
        if (initialized) {
            for (Player player : getGameWorld().getWorld().getPlayers()) {
                MessageUtil.sendMessage(player, msg);
            }
            remove();
        }
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.MESSAGE;
    }

}
