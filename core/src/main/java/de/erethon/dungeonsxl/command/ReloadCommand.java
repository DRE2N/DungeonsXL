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
package de.erethon.dungeonsxl.command;

import de.erethon.commons.chat.DefaultFontInfo;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.compatibility.CompatibilityHandler;
import de.erethon.commons.compatibility.Internals;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.event.DataReloadEvent;
import de.erethon.dungeonsxl.player.DInstancePlayer;
import de.erethon.dungeonsxl.player.DPermission;
import java.util.List;
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
        List<DInstancePlayer> dPlayers = this.dPlayers.getDInstancePlayers();
        if (!dPlayers.isEmpty() && args.length == 1 && sender instanceof Player) {
            MessageUtil.sendMessage(sender, DMessage.CMD_RELOAD_PLAYERS.getMessage());
            ClickEvent onClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dungeonsxl reload -force");
            String message = DefaultFontInfo.center(DMessage.MISC_OKAY.getMessage());
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

        for (DInstancePlayer dPlayer : dPlayers) {
            dPlayer.leave();
        }

        int maps = DungeonsXL.MAPS.listFiles().length - 1;
        int dungeons = DungeonsXL.DUNGEONS.listFiles().length;
        int loaded = instances.getEditWorlds().size() + instances.getGameWorlds().size();
        int players = this.dPlayers.getDGamePlayers().size();
        Internals internals = CompatibilityHandler.getInstance().getInternals();
        String vault = "";
        if (plugins.getPlugin("Vault") != null) {
            vault = plugins.getPlugin("Vault").getDescription().getVersion();
        }
        String ixl = "";
        if (plugins.getPlugin("ItemsXL") != null) {
            ixl = plugins.getPlugin("ItemsXL").getDescription().getVersion();
        }

        plugin.onDisable();
        plugin.initFolders();
        plugin.loadConfig();
        plugin.createCaches();
        plugin.initCaches();
        plugin.loadData();

        MessageUtil.sendPluginTag(sender, plugin);
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_RELOAD_SUCCESS.getMessage());
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_MAIN_LOADED.getMessage(String.valueOf(maps), String.valueOf(dungeons), String.valueOf(loaded), String.valueOf(players)));
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_MAIN_COMPATIBILITY.getMessage(String.valueOf(internals), vault, ixl));
    }

}
