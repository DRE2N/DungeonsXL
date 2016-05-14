/*
 * Copyright (C) 2016 Daniel Saukel
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
package io.github.dre2n.dungeonsxl.player;

import io.github.dre2n.commons.util.EnumUtil;
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
public enum DPermissions {

    // Main nodes
    BREAK("break", OP),
    BYPASS("bypass", OP),
    CHAT("chat", TRUE),
    CHAT_SPY("chatspy", OP),
    CMD_EDIT("cmdedit", OP),
    CREATE("create", OP),
    EDIT("edit", OP),
    ENTER("enter", OP),
    ESCAPE("escape", TRUE),
    GAME("game", TRUE),
    GROUP("group", OP),
    GROUP_ADMIN("group.admin", OP, GROUP),
    HELP("help", TRUE),
    IGNORE_REQUIREMENTS("ignorerequirements", OP),
    IGNORE_TIME_LIMIT("ignoretimelimit", OP),
    INVITE("invite", OP),
    INSECURE("insecure", OP),
    JOIN("join", TRUE),
    LEAVE("leave", TRUE),
    LIST("list", OP),
    LIVES("lives", TRUE),
    MAIN("main", TRUE),
    MESSAGE("msg", OP),
    PLAY("play", OP),
    PORTAL("portal", OP),
    RELOAD("reload", OP),
    SAVE("save", OP),
    SIGN("sign", OP),
    TEST("test", OP),
    UNINVITE("uninvite", OP),
    // Kits
    ADMINISTRATOR("*", OP),
    HALF_EDITOR("halfeditor", OP, ESCAPE, LIST, MESSAGE, SAVE),
    FULL_EDITOR("fulleditor", OP, HALF_EDITOR, EDIT, PLAY, SIGN, TEST),
    HALF_PLAYER("halfplayer", TRUE, CHAT, ESCAPE, GAME, HELP, JOIN, LEAVE, LIVES, MAIN),
    FULL_PLAYER("fullplayer", OP, HALF_PLAYER, GROUP);

    public static final String PREFIX = "dxl.";

    private String node;
    private PermissionDefault isDefault;
    private List<DPermissions> children = new ArrayList<>();

    DPermissions(String node, PermissionDefault isDefault) {
        this.node = node;
        this.isDefault = isDefault;
    }

    DPermissions(String node, PermissionDefault isDefault, DPermissions... children) {
        this(node, isDefault);
        this.children = Arrays.asList(children);
    }

    /**
     * @return the permission node String
     */
    public String getNode() {
        return PREFIX + node;
    }

    /**
     * @return if a player has the node by default
     */
    public PermissionDefault isDefault() {
        return isDefault;
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
    public List<DPermissions> getChildren() {
        return children;
    }

    /**
     * @param node
     * the node String, with or without "dxl."
     * @return
     * the DPermissions value
     */
    public static DPermissions getByNode(String node) {
        for (DPermissions permission : values()) {
            if (permission.getNode().equals(node) || permission.node.equals(node)) {
                return permission;
            }
        }

        return null;
    }

    /**
     * @param permission
     * the permission to check
     * @return if the player has the permission
     */
    public static boolean hasPermission(CommandSender sender, DPermissions permission) {
        if (sender.hasPermission(permission.getNode())) {
            return true;
        }

        for (DPermissions parent : DPermissions.values()) {
            if (parent.getChildren().contains(permission) && sender.hasPermission(parent.getNode())) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param permission
     * the permission to check
     * @return if the player has the permission
     */
    public static boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            return true;
        }

        DPermissions dPermission = null;
        if (EnumUtil.isValidEnum(DPermissions.class, permission)) {
            dPermission = DPermissions.valueOf(permission);

        } else if (DPermissions.getByNode(permission) != null) {
            dPermission = DPermissions.getByNode(permission);
        }

        if (dPermission == null) {
            return false;
        }

        for (DPermissions parent : DPermissions.values()) {
            if (parent.getChildren().contains(dPermission) && sender.hasPermission(parent.getNode())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Registers the permissions.
     */
    public static void register() {
        for (DPermissions permission : values()) {
            Bukkit.getPluginManager().addPermission(new Permission(permission.getNode(), permission.isDefault()));
        }
    }

}
