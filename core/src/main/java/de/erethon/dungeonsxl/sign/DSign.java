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

import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.event.dsign.DSignRegistrationEvent;
import de.erethon.dungeonsxl.game.Game;
import de.erethon.dungeonsxl.trigger.Trigger;
import de.erethon.dungeonsxl.world.DGameWorld;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Extend this to create a custom DSign.
 *
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public abstract class DSign {

    protected DungeonsXL plugin;

    public static final String ERROR_0 = ChatColor.DARK_RED + "## ERROR ##";
    public static final String ERROR_1 = ChatColor.WHITE + "Please";
    public static final String ERROR_2 = ChatColor.WHITE + "contact an";
    public static final String ERROR_3 = ChatColor.WHITE + "Admin!";

    private Sign sign;
    protected String[] lines;
    private DGameWorld gameWorld;

    // List of Triggers
    private Set<Trigger> triggers = new HashSet<>();

    private boolean erroneous;

    protected DSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        this.plugin = plugin;

        this.sign = sign;
        this.lines = lines;
        this.gameWorld = gameWorld;

        // Check Trigger
        if (gameWorld == null) {
            return;
        }

        String line3 = lines[3].replaceAll("\\s", "");
        String[] triggerTypes = line3.split(",");

        for (String triggerString : triggerTypes) {
            if (triggerString.isEmpty()) {
                continue;
            }

            String type = triggerString.substring(0, 1);
            String value = null;
            if (triggerString.length() > 1) {
                value = triggerString.substring(1);
            }

            Trigger trigger = Trigger.getOrCreate(plugin, type, value, this);
            if (trigger != null) {
                trigger.addListener(this);
                addTrigger(trigger);
            }
        }
    }

    /* Getters and setters */
    /**
     * @return the sign
     */
    public Sign getSign() {
        return sign;
    }

    /**
     * @param sign the sign to set
     */
    public void setSign(Sign sign) {
        this.sign = sign;
    }

    /**
     * @return the sign lines
     */
    public String[] getLines() {
        return lines;
    }

    /**
     * @param lines the sign lines to set
     */
    public void setLines(String[] lines) {
        this.lines = lines;
    }

    /**
     * @return the gameWorld
     */
    public DGameWorld getGameWorld() {
        return gameWorld;
    }

    /**
     * @return the game
     */
    public Game getGame() {
        return Game.getByGameWorld(gameWorld);
    }

    /**
     * @return the triggers
     */
    public Set<Trigger> getTriggers() {
        return triggers;
    }

    /**
     * @param trigger the trigger to add
     */
    public void addTrigger(Trigger trigger) {
        triggers.add(trigger);
    }

    /**
     * @param trigger the trigger to remove
     */
    public void removeTrigger(Trigger trigger) {
        triggers.remove(trigger);
    }

    /* Actions */
    public void onInit() {
    }

    public void onTrigger() {
    }

    public boolean onPlayerTrigger(Player player) {
        return false;
    }

    public void onDisable() {
    }

    public void onUpdate() {
        if (erroneous) {
            return;
        }

        for (Trigger trigger : triggers) {
            if (!trigger.isTriggered()) {
                onDisable();
                return;
            }

            if (trigger.getPlayer() == null) {
                continue;
            }

            if (onPlayerTrigger(trigger.getPlayer())) {
                return;
            }
        }

        onTrigger();
    }

    public void remove() {
        for (Trigger trigger : triggers) {
            trigger.removeListener(this);
        }
        gameWorld.getDSigns().remove(this);
    }

    public boolean hasTriggers() {
        return !triggers.isEmpty();
    }

    public boolean isErroneous() {
        return erroneous;
    }

    /**
     * Set a placeholder to show that the sign is setup incorrectly.
     *
     * @param reason the reason why the sign is marked as erroneous
     */
    public void markAsErroneous(String reason) {
        erroneous = true;
        sign.setLine(0, ERROR_0);
        sign.setLine(1, ERROR_1);
        sign.setLine(2, ERROR_2);
        sign.setLine(3, ERROR_3);
        sign.update();

        MessageUtil.log(plugin, "&4A sign at &6" + sign.getX() + ", " + sign.getY() + ", " + sign.getZ() + "&4 is erroneous!");
        MessageUtil.log(plugin, getType().getName() + ": " + reason);
    }

    /* Statics */
    public static DSign create(DungeonsXL plugin, Sign sign, DGameWorld gameWorld) {
        return create(plugin, sign, sign.getLines(), gameWorld);
    }

    public static DSign create(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        DSign dSign = null;

        for (DSignType type : plugin.getDSignCache().getDSigns()) {
            if (!lines[0].equalsIgnoreCase("[" + type.getName() + "]")) {
                continue;
            }

            try {
                Constructor<? extends DSign> constructor = type.getHandler().getConstructor(DungeonsXL.class, Sign.class, String[].class, DGameWorld.class);
                dSign = constructor.newInstance(plugin, sign, lines, gameWorld);

            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
                MessageUtil.log("An error occurred while accessing the handler class of the sign " + type.getName() + ": " + exception.getClass().getSimpleName());
                if (!(type instanceof DSignTypeDefault)) {
                    MessageUtil.log("Please note that this sign is an unsupported feature added by an addon!");
                }
                exception.printStackTrace();
            }
        }

        if (gameWorld != null) {
            DSignRegistrationEvent event = new DSignRegistrationEvent(sign, gameWorld, dSign);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return null;
            }
        }

        if (!(dSign != null && gameWorld != null)) {
            return dSign;
        }

        if (dSign.getType().isOnDungeonInit()) {
            dSign.onInit();
        }

        return dSign;
    }

    /* Abstracts */
    public abstract boolean check();

    public abstract DSignType getType();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{sign=" + sign + "; gameWorld=" + gameWorld + "}";
    }

}
