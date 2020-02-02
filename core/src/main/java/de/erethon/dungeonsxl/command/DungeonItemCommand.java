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

import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.NBTUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DungeonItemCommand extends DCommand {

    public DungeonItemCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("dungeonItem");
        setAliases("di");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(DMessage.CMD_DUNGEON_ITEM_HELP.getMessage());
        setPermission(DPermission.DUNGEON_ITEM.getNode());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        PlayerInventory inv = player.getInventory();

        ItemStack bukkitStack = inv.getItemInHand();
        if (bukkitStack == null) {
            MessageUtil.sendTitleMessage(player, DMessage.ERROR_NO_ITEM_IN_MAIN_HAND.getMessage());
            return;
        }
        Object tag = NBTUtil.getTag(bukkitStack);

        String action = args.length >= 2 ? args[1] : "info";
        if (action.equalsIgnoreCase("true")) {
            if (tag == null) {
                tag = NBTUtil.createTag();
            }
            NBTUtil.addBoolean(tag, NBTUtil.DUNGEON_ITEM_KEY, true);
            inv.setItemInHand(NBTUtil.setTag(bukkitStack, tag));
            MessageUtil.sendMessage(sender, DMessage.CMD_DUNGEON_ITEM_SET_DUNGEON.getMessage());
            MessageUtil.sendMessage(sender, DMessage.CMD_DUNGEON_ITEM_DUNGEON_ITEM_HELP.getMessage());

        } else if (action.equalsIgnoreCase("false")) {
            if (tag != null) {
                NBTUtil.removeKey(tag, NBTUtil.DUNGEON_ITEM_KEY);
                inv.setItemInHand(NBTUtil.setTag(bukkitStack, tag));
            }
            MessageUtil.sendMessage(sender, DMessage.CMD_DUNGEON_ITEM_SET_GLOBAL.getMessage());
            MessageUtil.sendMessage(sender, DMessage.CMD_DUNGEON_ITEM_GLOBAL_ITEM_HELP.getMessage());

        } else {
            if (tag != null && NBTUtil.hasKey(tag, NBTUtil.DUNGEON_ITEM_KEY)) {
                MessageUtil.sendMessage(sender, DMessage.CMD_DUNGEON_ITEM_INFO_DUNGEON.getMessage());
                MessageUtil.sendMessage(sender, DMessage.CMD_DUNGEON_ITEM_DUNGEON_ITEM_HELP.getMessage());
            } else {
                MessageUtil.sendMessage(sender, DMessage.CMD_DUNGEON_ITEM_INFO_GLOBAL.getMessage());
                MessageUtil.sendMessage(sender, DMessage.CMD_DUNGEON_ITEM_GLOBAL_ITEM_HELP.getMessage());
            }
        }
    }

}
