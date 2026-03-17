/*
 * Copyright (C) 2012-2013 Frank Baumann; 2015-2026 Daniel Saukel
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
package de.erethon.dungeonsxl.command;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.DependencyVersion;
import static de.erethon.dungeonsxl.util.DependencyVersion.*;
import de.erethon.xlib.chat.MessageUtil;
import de.erethon.xlib.compatibility.Version;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * @author Daniel Saukel
 */
public class StatusCommand extends DCommand {

    public static final String TRUE = ChatColor.GREEN + "\u2714";
    public static final String FALSE = ChatColor.DARK_RED + "\u2718";

    public StatusCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("status");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.CMD_STATUS_HELP.getMessage());
        setPermission(DPermission.STATUS.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        String minecraftVersion = Version.get().toString();
        String bukkitVersion = Bukkit.getBukkitVersion();
        String dungeonsxlVersion = plugin.getDescription().getVersion();

        boolean atLeastMin = Version.isAtLeast(DependencyVersion.META.getMinVersion());
        boolean atMostMax = Version.isAtMost(Version.values()[Version.values().length - 2]);
        String versionCorrect = getSymbol(atLeastMin && atMostMax);
        if (atLeastMin && !atMostMax) {
            versionCorrect += "(possibly works through forward compatibility)";
        }
        String dungeonsxlVersionCorrect = getSymbol(!dungeonsxlVersion.contains("SNAPSHOT"));

        MessageUtil.sendCenteredMessage(sender, "&4&l=> &6STATUS &4&l<=");
        MessageUtil.sendMessage(sender, ChatColor.GRAY + "Version info:");
        MessageUtil.sendMessage(sender, "= Minecraft: " + minecraftVersion + " " + versionCorrect);
        MessageUtil.sendMessage(sender, "= Bukkit: " + bukkitVersion + " " + versionCorrect);
        MessageUtil.sendMessage(sender, "= DungeonsXL: " + dungeonsxlVersion + " " + dungeonsxlVersionCorrect);

        String permissionPlugin = "No plugin found";
        String economyPlugin = "No plugin found";
        if (VAULT.isEnabled()) {
            if (xlib.getPermissionProvider() != null) {
                permissionPlugin = xlib.getPermissionProvider().getName();
            }
            if (xlib.getEconomyProvider() != null) {
                economyPlugin = xlib.getEconomyProvider().getName();
            }
        }
        String permissionPluginCorrect = getSymbol(xlib.getPermissionProvider() != null && xlib.getPermissionProvider().hasGroupSupport());
        String economyPluginCorrect = getSymbol(!plugin.getMainConfig().isEconomyEnabled() || xlib.getEconomyProvider() != null);

        MessageUtil.sendMessage(sender, ChatColor.GRAY + "Dependency info:");
        MessageUtil.sendMessage(sender, statusMsg(VAULT));
        MessageUtil.sendMessage(sender, "  = Permissions: " + permissionPlugin + " " + permissionPluginCorrect);
        MessageUtil.sendMessage(sender, "  = Economy: " + economyPlugin + " " + economyPluginCorrect);
        MessageUtil.sendMessage(sender, statusMsg(XLIB));
        MessageUtil.sendMessage(sender, statusMsg(BOSSSHOP));
        MessageUtil.sendMessage(sender, statusMsg(HOLOGRAPHIC_DISPLAYS));
        MessageUtil.sendMessage(sender, statusMsg(MODERN_LWC));
        MessageUtil.sendMessage(sender, statusMsg(PARTIES));
        MessageUtil.sendMessage(sender, statusMsg(PLACEHOLDER_API));
        MessageUtil.sendMessage(sender, statusMsg(CITIZENS));
        MessageUtil.sendMessage(sender, statusMsg(CUSTOM_MOBS));
        MessageUtil.sendMessage(sender, statusMsg(INSANE_MOBS));
        MessageUtil.sendMessage(sender, statusMsg(MYTHIC_MOBS));
    }

    private static BaseComponent statusMsg(DependencyVersion dependency) {
        boolean check = dependency.check();
        TextComponent text = new TextComponent("= " + dependency.getName() + ": " + dependency.getEnabledVersion() + " " + getSymbol(check));
        if (!check) {
            HoverEvent event = new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("The tested version is: ").color(ChatColor.GRAY)
                            .append(dependency.getSupportedVersion()).color(ChatColor.GREEN).create());
            text.setHoverEvent(event);
        }
        return text;
    }

    private static String getSymbol(boolean value) {
        return value ? TRUE : FALSE;
    }

}
