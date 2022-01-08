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
package de.erethon.dungeonsxl.sign.windup;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.sign.Windup;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.trigger.InteractTrigger;
import de.erethon.dungeonsxl.util.commons.misc.EnumUtil;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class CommandSign extends Windup {

    public enum Executor {
        DEFAULT,
        OP,
        CONSOLE
    }

    private CommandScript script;
    private Executor executor;

    public CommandSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    public CommandScript getScript() {
        return script;
    }

    @Override
    public String getName() {
        return "CMD";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".cmd";
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

    public Executor getExecutor() {
        return executor;
    }

    @Override
    public boolean validate() {
        script = ((DungeonsXL) api).getCommandScriptRegistry().get(getLine(1));
        return script != null && !script.getCommands().isEmpty();
    }

    @Override
    public void initialize() {
        script = ((DungeonsXL) api).getCommandScriptRegistry().get(getLine(1));
        String[] attributes = getLine(2).split(",");

        if (attributes.length == 3) {
            delay = NumberUtil.parseDouble(attributes[0]);
            interval = NumberUtil.parseDouble(attributes[1]);
            executor = EnumUtil.getEnumIgnoreCase(Executor.class, attributes[2]);
            if (executor == null) {
                executor = Executor.DEFAULT;
            }

        } else if (attributes.length == 2) {
            delay = NumberUtil.parseDouble(attributes[0]);
            interval = NumberUtil.parseDouble(attributes[1], -1);
            if (interval == -1) {
                interval = delay;
                executor = EnumUtil.getEnumIgnoreCase(Executor.class, attributes[1]);
            }
            if (executor == null) {
                executor = Executor.DEFAULT;
            }

        } else if (attributes.length == 1) {
            delay = NumberUtil.parseDouble(attributes[0], -1);
            if (delay == -1) {
                delay = 0;
                interval = 0;
                executor = EnumUtil.getEnumIgnoreCase(Executor.class, attributes[0]);
            }
            if (executor == null) {
                executor = Executor.DEFAULT;
            }

        } else if (attributes.length == 0) {
            executor = Executor.DEFAULT;
        }
        n = script.getCommands().size();

        setRunnable(new CommandTask(this, Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")));

        if (!getTriggers().isEmpty()) {
            setToAir();
            return;
        }

        InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), (DGameWorld) getGameWorld());
        if (trigger != null) {
            trigger.addListener(this);
            getTriggers().add(trigger);
        }

        getSign().setLine(0, ChatColor.DARK_BLUE + "############");
        getSign().setLine(1, ChatColor.GREEN + script.getName());
        getSign().setLine(2, "");
        getSign().setLine(3, ChatColor.DARK_BLUE + "############");
        getSign().update();
    }

    @Override
    public void activate() {
        if (executor == Executor.CONSOLE) {
            ((CommandTask) getRunnable()).setSender(Bukkit.getConsoleSender(), false);
            startTask();
            active = true;
        } else {
            markAsErroneous("Sign is set to be performed by a player but is triggered by a trigger that cannot be attributed to a player (e.g. mob)");
        }
    }

    @Override
    public boolean activate(Player player) {
        CommandSender sender = player;
        boolean wasOp = player.isOp();
        if (executor == Executor.CONSOLE) {
            sender = Bukkit.getConsoleSender();
        }
        ((CommandTask) getRunnable()).setSender(sender, wasOp);
        ((CommandTask) getRunnable()).setPlayer(player);
        startTask();
        active = true;
        return true;
    }

}
