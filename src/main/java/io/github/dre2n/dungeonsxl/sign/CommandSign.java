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

import io.github.dre2n.commandsxl.CommandsXL;
import io.github.dre2n.commandsxl.command.CCommand;
import io.github.dre2n.commandsxl.command.CCommandExecutorTask;
import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.dungeonsxl.trigger.InteractTrigger;
import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class CommandSign extends DSign {

    private DSignType type = DSignTypeDefault.COMMAND;

    // Variables
    private CCommand cCommand;
    private long delay;

    private String command;
    private String executor;
    private boolean initialized;

    public CommandSign(Sign sign, GameWorld gameWorld) {
        super(sign, gameWorld);
    }

    @Override
    public boolean check() {
        if (lines[1].isEmpty() || lines[2].isEmpty()) {
            return false;
        }

        if (lines[1] == null) {
            return false;
        }

        String[] attributes = lines[2].split(",");
        if (attributes.length == 2) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public void onInit() {
        String[] attributes = lines[2].split(",");

        command = lines[1];
        delay = NumberUtil.parseInt(attributes[0]);
        executor = attributes[1];

        cCommand = CommandsXL.getPlugin().getCCommands().getCCommand(command);

        if (!getTriggers().isEmpty()) {
            getSign().getBlock().setType(Material.AIR);
            return;
        }

        InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), getGameWorld());
        if (trigger != null) {
            trigger.addListener(this);
            getTriggers().add(trigger);
        }

        getSign().setLine(0, ChatColor.DARK_BLUE + "############");
        getSign().setLine(1, ChatColor.DARK_GREEN + command);
        getSign().setLine(2, "");
        getSign().setLine(3, ChatColor.DARK_BLUE + "############");
        getSign().update();

        initialized = true;
    }

    @Override
    public boolean onPlayerTrigger(final Player player) {
        if (executor.equalsIgnoreCase("Console")) {
            new CCommandExecutorTask(player, cCommand, Bukkit.getConsoleSender(), true).runTaskLater(plugin, delay * 20);

        } else if (executor.equalsIgnoreCase("OP")) {
            boolean isOp = player.isOp();

            player.setOp(true);

            new CCommandExecutorTask(player, cCommand, player, true).runTaskLater(plugin, delay * 20);

            if (!isOp) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setOp(false);
                    }
                }.runTaskLater(plugin, delay * 20 + 1);
            }

        } else {
            new CCommandExecutorTask(player, cCommand, player, false).runTaskLater(plugin, delay * 20);
        }

        return true;
    }

    @Override
    public void onTrigger() {
        if (initialized) {
            remove();
        }
    }

    @Override
    public DSignType getType() {
        return type;
    }

}
