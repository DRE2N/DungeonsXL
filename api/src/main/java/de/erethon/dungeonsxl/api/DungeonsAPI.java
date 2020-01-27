/*
 * Copyright (C) 2014-2020 Daniel Saukel
 *
 * This library is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNULesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.api;

import de.erethon.commons.misc.Registry;
import de.erethon.dungeonsxl.api.player.PlayerClass;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import java.io.File;
import java.util.Collection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Daniel Saukel
 */
public interface DungeonsAPI extends Plugin {

    static final File PLUGIN_ROOT = new File("plugins/DungeonsXL");
    static final File BACKUPS = new File(PLUGIN_ROOT, "backups");
    static final File LANGUAGES = new File(PLUGIN_ROOT, "languages");
    static final File MAPS = new File(PLUGIN_ROOT, "maps");
    static final File PLAYERS = new File(PLUGIN_ROOT, "players");
    static final File SCRIPTS = new File(PLUGIN_ROOT, "scripts");
    static final File ANNOUNCERS = new File(SCRIPTS, "announcers");
    static final File CLASSES = new File(SCRIPTS, "classes");
    static final File DUNGEONS = new File(SCRIPTS, "dungeons");
    static final File SIGNS = new File(SCRIPTS, "signs");

    /**
     * Returns a {@link Registry} of the loaded classes.
     *
     * @return a {@link Registry} of the loaded classes
     */
    Registry<String, PlayerClass> getClassRegistry();

    /**
     * Returns a {@link Registry} of the sign types.
     *
     * @return a {@link Registry} of the sign types
     */
    Registry<String, Class<? extends DungeonSign>> getSignRegistry();

    /**
     * Returns a {@link Registry} of the trigger types.
     *
     * @return a {@link Registry} of the trigger types
     */
    Registry<String, Class<? extends Trigger>> getTriggerRegistry();

    /**
     * Returns a {@link Registry} of the reward types.
     *
     * @return a {@link Registry} of the reward types
     */
    Registry<String, Class<? extends Reward>> getRewardRegistry();

    /* Object initialization */
    /**
     * Creates a new group.
     *
     * @param leader the leader
     * @return a new group
     */
    PlayerGroup createGroup(Player leader);

    /**
     * Creates a new group.
     *
     * @param leader the leader
     * @param color  the color that represents the group and sets the name
     * @return a new group or null if values are invalid
     */
    PlayerGroup createGroup(Player leader, PlayerGroup.Color color);

    /**
     * Creates a new group.
     *
     * @param leader the leader
     * @param name   the group's name - must be unique
     * @return a new group or null if values are invalid
     */
    PlayerGroup createGroup(Player leader, String name);

    /**
     * Creates a new group.
     *
     * @param leader  the leader
     * @param dungeon the dungeon to play
     * @return a new group or null if values are invalid
     */
    PlayerGroup createGroup(Player leader, Dungeon dungeon);

    /**
     * Creates a new group.
     *
     * @param leader  the leader
     * @param members the group members with or without the leader
     * @param name    the name of the group
     * @param dungeon the dungeon to play
     * @return a new group or null if values are invalid
     */
    PlayerGroup createGroup(Player leader, Collection<Player> members, String name, Dungeon dungeon);

}
