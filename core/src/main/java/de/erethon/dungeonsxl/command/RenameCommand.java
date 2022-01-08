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
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.world.ResourceWorld;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.dungeon.DDungeon;
import de.erethon.dungeonsxl.dungeon.DungeonConfig;
import de.erethon.dungeonsxl.global.GlobalProtection;
import de.erethon.dungeonsxl.global.JoinSign;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.util.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.world.DResourceWorld;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Daniel Saukel
 */
public class RenameCommand extends DCommand {

    public RenameCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("rename");
        setMinArgs(2);
        setMaxArgs(2);
        setHelp(DMessage.CMD_RENAME_HELP.getMessage());
        setPermission(DPermission.RENAME.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        DResourceWorld resource = (DResourceWorld) plugin.getMapRegistry().get(args[1]);
        if (resource == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_MAP.getMessage(args[1]));
            return;
        }

        Dungeon sfd = resource.getSingleFloorDungeon();
        resource.setName(args[2]);
        resource.getFolder().renameTo(new File(DungeonsXL.MAPS, args[2]));
        resource.getSignData().updateFile(resource);

        if (resource.getEditWorld() != null) {
            resource.getEditWorld().delete(true);
        }

        for (Dungeon dungeon : plugin.getDungeonRegistry()) {
            if (!dungeon.isMultiFloor()) {
                continue;
            }
            DungeonConfig dConfig = ((DDungeon) dungeon).getConfig();
            FileConfiguration config = dConfig.getConfig();
            File file = dConfig.getFile();

            if (dConfig.getStartFloor() == resource) {
                config.set("startFloor", args[2]);
            }

            if (dConfig.getEndFloor() == resource) {
                config.set("endFloor", args[2]);
            }

            List<String> list = config.getStringList("floors");
            int i = 0;
            for (ResourceWorld floor : dConfig.getFloors()) {
                if (floor == resource) {
                    list.set(i, args[2]);
                }
                i++;
            }
            config.set("floors", list);

            try {
                config.save(file);
            } catch (IOException ex) {
            }
        }
        sfd.setName(args[2]);
        plugin.getDungeonRegistry().removeKey(args[1]);
        plugin.getDungeonRegistry().add(args[2], sfd);
        plugin.getMapRegistry().removeKey(args[1]);
        plugin.getMapRegistry().add(args[2], resource);

        boolean changed = false;
        for (GlobalProtection protection : plugin.getGlobalProtectionCache().getProtections().toArray(new GlobalProtection[]{})) {
            if (!(protection instanceof JoinSign)) {
                continue;
            }
            Dungeon dungeon = ((JoinSign) protection).getDungeon();
            if (dungeon == null) {
                protection.delete();
                continue;
            }
            if (dungeon.getName().equals(args[1])) {
                dungeon.setName(args[2]);
                changed = true;
            }
        }

        if (changed) {
            plugin.getGlobalProtectionCache().saveAll();
        }

        MessageUtil.sendMessage(sender, DMessage.CMD_RENAME_SUCCESS.getMessage(args[1], args[2]));
    }

}
