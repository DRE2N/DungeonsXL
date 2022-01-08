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
package de.erethon.dungeonsxl.command;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.event.DataReloadEvent;
import de.erethon.dungeonsxl.api.player.GroupAdapter;
import de.erethon.dungeonsxl.api.player.InstancePlayer;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.chat.DefaultFontInfo;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.util.commons.compatibility.CompatibilityHandler;
import de.erethon.dungeonsxl.util.commons.compatibility.Internals;
import java.util.Collection;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ReloadCommand extends DCommand {

    public ReloadCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("reload");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(DMessage.CMD_RELOAD_HELP.getMessage());
        setPermission(DPermission.RELOAD.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        if (args.length >= 2 && (args[1].equalsIgnoreCase("-caliburn") || args[1].equalsIgnoreCase("-c"))) {
            plugin.getCaliburn().reload();
            MessageUtil.sendCenteredMessage(sender, DMessage.CMD_RELOAD_SUCCESS.getMessage());
            String ci = String.valueOf(plugin.getCaliburn().getCustomItems().size());
            String cm = String.valueOf(plugin.getCaliburn().getCustomMobs().size());
            String lt = String.valueOf(plugin.getCaliburn().getLootTables().size());
            MessageUtil.sendCenteredMessage(sender, DMessage.CMD_RELOAD_CALIBURN.getMessage(ci, cm, lt));
            return;
        }

        Collection<InstancePlayer> dPlayers = this.dPlayers.getAllInstancePlayers();
        if (!dPlayers.isEmpty() && args.length == 1 && sender instanceof Player) {
            MessageUtil.sendMessage(sender, DMessage.CMD_RELOAD_PLAYERS.getMessage());
            ClickEvent onClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dungeonsxl reload -force");
            String message = DefaultFontInfo.center(DMessage.BUTTON_OKAY.getMessage());
            TextComponent text = new TextComponent(message);
            text.setClickEvent(onClick);
            MessageUtil.sendMessage(sender, text);
            return;
        }

        PluginManager plugins = Bukkit.getPluginManager();

        DataReloadEvent event = new DataReloadEvent();
        plugins.callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        dPlayers.forEach(InstancePlayer::leave);

        int maps = DungeonsXL.MAPS.listFiles().length - 1;
        int dungeons = DungeonsXL.DUNGEONS.listFiles().length;
        int loaded = plugin.getInstanceCache().size();
        int players = this.dPlayers.getAllGamePlayers().size();
        Internals internals = CompatibilityHandler.getInstance().getInternals();
        String vault = "";
        if (plugins.getPlugin("Vault") != null) {
            vault = plugins.getPlugin("Vault").getDescription().getVersion();
        }
        String ixl = "";
        if (plugins.getPlugin("ItemsXL") != null) {
            ixl = plugins.getPlugin("ItemsXL").getDescription().getVersion();
        }

        plugin.saveData();
        plugin.initFolders();
        plugin.initCaches();
        plugin.checkState();
        plugin.getGroupAdapters().forEach(GroupAdapter::clear);

        MessageUtil.sendPluginTag(sender, plugin);
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_RELOAD_SUCCESS.getMessage());
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_MAIN_LOADED.getMessage(String.valueOf(maps), String.valueOf(dungeons), String.valueOf(loaded), String.valueOf(players)));
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_MAIN_COMPATIBILITY.getMessage(String.valueOf(internals), vault, ixl));
        ClickEvent onClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dungeonsxl reload -caliburn");
        String message = DefaultFontInfo.center(DMessage.CMD_RELOAD_BUTTON_CALIBURN.getMessage());
        TextComponent text = new TextComponent(message);
        text.setClickEvent(onClick);
        MessageUtil.sendMessage(sender, text);
    }

}
