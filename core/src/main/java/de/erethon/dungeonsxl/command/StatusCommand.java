/*
 * Copyright (C) 2012-2023 Frank Baumann
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
import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.compatibility.CompatibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * @author Daniel Saukel
 */
public class StatusCommand extends DCommand {

    private CompatibilityHandler compat = CompatibilityHandler.getInstance();
    private PluginManager manager = Bukkit.getPluginManager();

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
        String minecraftVersion = compat.getVersion().toString();
        String bukkitVersion = Bukkit.getName() + (compat.isSpigot() ? " (Spigot)" : "") + " " + Bukkit.getBukkitVersion();
        String internalsVersion = compat.getInternals().toString();
        String dungeonsxlVersion = plugin.getDescription().getVersion();

        String internalsVersionCorrect = getSymbol(plugin.getSettings().getInternals().contains(compat.getInternals()));
        String dungeonsxlVersionCorrect = getSymbol(!dungeonsxlVersion.contains("SNAPSHOT"));

        MessageUtil.sendCenteredMessage(sender, "&4&l=> &6STATUS &4&l<=");
        MessageUtil.sendMessage(sender, ChatColor.GRAY + "Version info:");
        MessageUtil.sendMessage(sender, "= Minecraft: " + minecraftVersion + " " + internalsVersionCorrect);
        MessageUtil.sendMessage(sender, "= Bukkit: " + bukkitVersion + " " + internalsVersionCorrect);
        MessageUtil.sendMessage(sender, "= Internals (package version): " + internalsVersion + " " + internalsVersionCorrect);
        MessageUtil.sendMessage(sender, "= DungeonsXL: " + dungeonsxlVersion + " " + dungeonsxlVersionCorrect);

        Plugin vault = manager.getPlugin("Vault");
        Plugin itemsxl = manager.getPlugin("ItemsXL");
        Plugin citizens = manager.getPlugin("Citizens");
        Plugin custommobs = manager.getPlugin("CustomMobs");
        Plugin mythicmobs = manager.getPlugin("MythicMobs");
        Plugin holographicdisplays = manager.getPlugin("HolographicDisplays");

        String vaultVersion = "Not enabled";
        String permissionPlugin = "No plugin found";
        String economyPlugin = "No plugin found";
        String itemsxlVersion = "Not enabled";
        String citizensVersion = "Not enabled";
        String custommobsVersion = "Not enabled";
        String insanemobsVersion = "Not enabled";
        String mythicmobsVersion = "Not enabled";
        String holographicdisplaysVersion = "Not enabled";

        if (vault != null) {
            vaultVersion = vault.getDescription().getVersion();
            if (plugin.getPermissionProvider() != null) {
                permissionPlugin = plugin.getPermissionProvider().getName();
            }
            if (plugin.getEconomyProvider() != null) {
                economyPlugin = plugin.getEconomyProvider().getName();
            }
        }
        if (itemsxl != null) {
            itemsxlVersion = itemsxl.getDescription().getVersion();
        }
        if (citizens != null) {
            citizensVersion = citizens.getDescription().getVersion();
        }
        if (custommobs != null) {
            custommobsVersion = custommobs.getDescription().getVersion();
        }
        if (mythicmobs != null) {
            mythicmobsVersion = mythicmobs.getDescription().getVersion();
        }
        if (holographicdisplays != null) {
            holographicdisplaysVersion = holographicdisplays.getDescription().getVersion();
        }

        String vaultVersionCorrect = getSymbol(vaultVersion.startsWith("1.7"));
        String permissionPluginCorrect = getSymbol(plugin.getPermissionProvider() != null && plugin.getPermissionProvider().hasGroupSupport());
        String economyPluginCorrect = getSymbol(!plugin.getMainConfig().isEconomyEnabled() || plugin.getEconomyProvider() != null);
        String itemsxlVersionCorrect = getSymbol(itemsxlVersion.equals(DungeonsXL.LATEST_IXL));
        String citizensVersionCorrect = getSymbol(citizensVersion.startsWith("2.0"));
        String custommobsVersionCorrect = getSymbol(custommobsVersion.startsWith("4."));
        String insanemobsVersionCorrect = getSymbol(insanemobsVersion.startsWith("3."));
        String mythicmobsVersionCorrect = getSymbol(mythicmobsVersion.startsWith("4."));
        String holographicdisplaysVersionCorrect = getSymbol(holographicdisplaysVersion.startsWith("2.4"));

        MessageUtil.sendMessage(sender, ChatColor.GRAY + "Dependency info:");
        MessageUtil.sendMessage(sender, "= Vault: " + vaultVersion + " " + vaultVersionCorrect);
        MessageUtil.sendMessage(sender, "  = Permissions: " + permissionPlugin + " " + permissionPluginCorrect);
        MessageUtil.sendMessage(sender, "  = Economy: " + economyPlugin + " " + economyPluginCorrect);
        MessageUtil.sendMessage(sender, "= ItemsXL: " + itemsxlVersion + " " + itemsxlVersionCorrect);
        MessageUtil.sendMessage(sender, "= Citizens: " + citizensVersion + " " + citizensVersionCorrect);
        MessageUtil.sendMessage(sender, "= CustomMobs: " + custommobsVersion + " " + custommobsVersionCorrect);
        MessageUtil.sendMessage(sender, "= InsaneMobs: " + insanemobsVersion + " " + insanemobsVersionCorrect);
        MessageUtil.sendMessage(sender, "= MythicMobs: " + mythicmobsVersion + " " + mythicmobsVersionCorrect);
        MessageUtil.sendMessage(sender, "= HolographicDisplays: " + holographicdisplaysVersion + " " + holographicdisplaysVersionCorrect);
    }

    public static String getSymbol(boolean value) {
        return value ? TRUE : FALSE;
    }

}
