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
package de.erethon.dungeonsxl.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import static org.bukkit.permissions.PermissionDefault.*;

/**
 * @author Daniel Saukel
 */
public enum DPermission {

    // Main nodes
    ANNOUNCE("announce", OP),
    BED("bed", OP),
    BREAK("break", OP),
    BYPASS("bypass", OP),
    CHAT("chat", TRUE),
    CHAT_SPY("chatspy", OP),
    CMD_EDIT("cmdedit", OP),
    CREATE("create", OP),
    DELETE("delete", OP),
    DUNGEON_ITEM("dungeonitem", OP),
    EDIT("edit", OP),
    ENDER_CHEST("enderchest", OP),
    DISPENSER("dispenser", OP),
    ENTER("enter", OP),
    ESCAPE("escape", TRUE),
    GAME("game", TRUE),
    GROUP("group", OP),
    GROUP_ADMIN("group.admin", OP, GROUP),
    HELP("help", TRUE),
    IGNORE_REQUIREMENTS("ignorerequirements", OP),
    IMPORT("IMPORT", OP),
    INVITE("invite", OP),
    INSECURE("insecure", OP),
    JOIN("join", TRUE),
    KICK("kick", OP),
    LEAVE("leave", TRUE),
    LIST("list", OP),
    LIVES("lives", TRUE),
    MAIN("main", TRUE),
    MESSAGE("msg", OP),
    PLAY("play", OP),
    PORTAL("portal", OP),
    RELOAD("reload", OP),
    RENAME("rename", OP),
    RESOURCE_PACK("resourcepack", OP),
    REWARDS("rewards", TRUE),
    SAVE("save", OP),
    STATUS("status", OP),
    /**
     * Allows to open the settings menu.
     */
    SETTINGS("settings", TRUE),
    /**
     * Allows to modify dungeon settings unless they have an own node.
     */
    SETTINGS_EDIT("settings.edit", OP),
    /**
     * Allows to modify global settings.
     */
    SETTINGS_GLOBAL("settings.global", OP),
    /**
     * Allows to modify player settings unless they have an own node.
     */
    SETTINGS_PLAYER("settings.player", TRUE),
    SIGN("sign", OP),
    TEST("test", OP),
    UNINVITE("uninvite", OP),
    // Kits
    ADMINISTRATOR("*", OP),
    HALF_EDITOR("halfeditor", OP, ESCAPE, LIST, MESSAGE, SAVE),
    FULL_EDITOR("fulleditor", OP, HALF_EDITOR, DELETE, EDIT, PLAY, RENAME, SIGN, TEST),
    HALF_PLAYER("halfplayer", TRUE, CHAT, ESCAPE, GAME, HELP, JOIN, LEAVE, LIVES, MAIN, SETTINGS, SETTINGS_PLAYER),
    FULL_PLAYER("fullplayer", OP, HALF_PLAYER, GROUP);

    public static final String PREFIX = "dxl.";

    private Permission node;
    private List<DPermission> children = new ArrayList<>();

    DPermission(String node, PermissionDefault isDefault) {
        this.node = new Permission(PREFIX + node, isDefault);
    }

    DPermission(String node, PermissionDefault isDefault, DPermission... children) {
        this(node, isDefault);
        this.children = Arrays.asList(children);
        for (DPermission child : children) {
            child.node.addParent(node, true);
        }
    }

    /**
     * @return the permission node String
     */
    public String getNode() {
        return node.getName();
    }

    /**
     * @return if a player has the node by default
     */
    public PermissionDefault isDefault() {
        return node.getDefault();
    }

    /**
     * @return if the node has children
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * @return the child permissions
     */
    public List<DPermission> getChildren() {
        return children;
    }

    /**
     * @param node the node String, with or without "dxl."
     * @return the DPermission value
     */
    public static DPermission getByNode(String node) {
        for (DPermission permission : values()) {
            if (permission.getNode().equals(node) || permission.node.equals(node)) {
                return permission;
            }
        }

        return null;
    }

    /**
     * @param sender     the CommandSender
     * @param permission the permission to check
     * @return if the player has the permission
     */
    public static boolean hasPermission(CommandSender sender, DPermission permission) {
        return sender.hasPermission(permission.getNode());
    }

    /**
     * Registers the permissions.
     */
    public static void register() {
        for (DPermission permission : values()) {
            Bukkit.getPluginManager().addPermission(permission.node);
        }
    }

    /**
     * Unregisters the permissions.
     */
    public static void unregister() {
        for (DPermission permission : values()) {
            Bukkit.getPluginManager().removePermission(permission.node);
        }
    }

}
