/*
 * Copyright (C) 2012-2016 Frank Baumann
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
package io.github.dre2n.dungeonsxl.command;

import io.github.dre2n.commons.command.BRCommand;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.DMessages;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupCreateEvent;
import io.github.dre2n.dungeonsxl.event.dgroup.DGroupDisbandEvent;
import io.github.dre2n.dungeonsxl.event.dplayer.DPlayerKickEvent;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import io.github.dre2n.dungeonsxl.player.DGroup;
import io.github.dre2n.dungeonsxl.player.DPermissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class GroupCommand extends BRCommand {

    DungeonsXL plugin = DungeonsXL.getInstance();

    public GroupCommand() {
        setCommand("group");
        setMinArgs(0);
        setMaxArgs(2);
        setHelp(DMessages.HELP_CMD_GROUP.getMessage());
        setPermission(DPermissions.GROUP.getNode());
        setPlayerCommand(true);
    }

    private CommandSender sender;
    private Player player;
    private String[] args;

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        this.sender = sender;
        this.player = (Player) sender;
        this.args = args;

        DGroup dGroup = DGroup.getByPlayer(player);

        if (args.length == 2) {

            if (args[1].equalsIgnoreCase("disband")) {
                disbandGroup(dGroup);
                return;

            } else if (args[1].equalsIgnoreCase("show")) {
                showGroup(dGroup);
                return;
            }

        } else if (args.length >= 3) {

            if (args[1].equalsIgnoreCase("kick")) {
                kickPlayer(dGroup);
                return;

            } else if (args[1].equalsIgnoreCase("invite")) {
                invitePlayer(dGroup);
                return;

            } else if (args[1].equalsIgnoreCase("uninvite")) {
                uninvitePlayer(dGroup);
                return;

            } else if (args[1].equalsIgnoreCase("help")) {
                showHelp(args[2]);
                return;

            } else if (args[1].equalsIgnoreCase("create")) {
                createGroup();
                return;

            } else if (args[1].equalsIgnoreCase("disband") && DPermissions.hasPermission(sender, DPermissions.GROUP_ADMIN)) {
                disbandGroup(DGroup.getByName(args[2]));
                return;

            } else if (args[1].equalsIgnoreCase("join")) {
                joinGroup(DGroup.getByName(args[2]));
                return;

            } else if (args[1].equalsIgnoreCase("show") && DPermissions.hasPermission(sender, DPermissions.GROUP_ADMIN)) {
                showGroup(DGroup.getByName(args[2]));
                return;
            }
        }

        showHelp("1");
    }

    public void createGroup() {
        if (DGroup.getByPlayer(player) != null) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_LEAVE_GROUP.getMessage());
            return;
        }

        if (DGroup.getByName(args[2]) != null) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NAME_IN_USE.getMessage(args[2]));
            return;
        }

        DGroup dGroup = new DGroup(args[2], player);
        DGroupCreateEvent event = new DGroupCreateEvent(dGroup, player, DGroupCreateEvent.Cause.COMMAND);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            dGroup.delete();
            dGroup = null;

        } else {
            MessageUtil.sendMessage(sender, DMessages.GROUP_CREATED.getMessage(sender.getName(), args[2]));
        }
    }

    public void disbandGroup(DGroup dGroup) {
        if (dGroup == null) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NO_SUCH_GROUP.getMessage());
            return;
        }

        if (dGroup.isPlaying()) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_LEAVE_DUNGEON.getMessage());
            return;
        }

        DGroupDisbandEvent event = new DGroupDisbandEvent(dGroup, player, DGroupDisbandEvent.Cause.COMMAND);
        plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            dGroup.delete();
            MessageUtil.sendMessage(sender, DMessages.GROUP_DISBANDED.getMessage(sender.getName(), dGroup.getName()));
            dGroup = null;
        }
    }

    public void invitePlayer(DGroup dGroup) {
        if (dGroup == null) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_JOIN_GROUP.getMessage());
            return;
        }

        Player toInvite = plugin.getServer().getPlayer(args[2]);

        if (toInvite != null) {
            dGroup.addInvitedPlayer(toInvite, false);

        } else {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NO_SUCH_PLAYER.getMessage(args[2]));
        }
    }

    public void uninvitePlayer(DGroup dGroup) {
        if (dGroup == null) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_JOIN_GROUP.getMessage());
            return;
        }

        dGroup.clearOfflineInvitedPlayers();

        Player toUninvite = plugin.getServer().getPlayer(args[2]);

        if (toUninvite != null) {
            if (dGroup.getInvitedPlayers().contains(toUninvite)) {
                dGroup.removeInvitedPlayer(toUninvite, false);

            } else {
                MessageUtil.sendMessage(sender, DMessages.ERROR_NOT_IN_GROUP.getMessage(args[2]));
            }

        } else {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NO_SUCH_PLAYER.getMessage(args[2]));
        }
    }

    public void joinGroup(DGroup dGroup) {
        if (dGroup == null) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NO_SUCH_GROUP.getMessage(args[2]));
            return;
        }

        if (DGroup.getByPlayer(player) != null) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_LEAVE_GROUP.getMessage());
            return;
        }

        if (!dGroup.getInvitedPlayers().contains(player) && !DPermissions.hasPermission(player, DPermissions.BYPASS)) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NOT_INVITED.getMessage(args[2]));
            return;
        }

        dGroup.addPlayer(player);
        dGroup.removeInvitedPlayer(player, true);
    }

    public void kickPlayer(DGroup dGroup) {
        if (dGroup == null) {
            MessageUtil.sendMessage(sender, DMessages.ERROR_JOIN_GROUP.getMessage());
        }

        Player toKick = plugin.getServer().getPlayer(args[2]);
        if (toKick != null) {
            DPlayerKickEvent event = new DPlayerKickEvent(DGamePlayer.getByPlayer(toKick.getPlayer()), DPlayerKickEvent.Cause.COMMAND);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                if (dGroup.getPlayers().contains(toKick)) {
                    dGroup.removePlayer(toKick);
                    MessageUtil.sendMessage(sender, DMessages.GROUP_KICKED_PLAYER.getMessage(sender.getName(), args[2], dGroup.getName()));

                } else {
                    MessageUtil.sendMessage(sender, DMessages.ERROR_NOT_IN_GROUP.getMessage(args[2], dGroup.getName()));
                }
            }

        } else {
            MessageUtil.sendMessage(sender, DMessages.ERROR_NO_SUCH_PLAYER.getMessage(args[2]));
        }
    }

    public void showGroup(DGroup dGroup) {
        if (dGroup == null) {
            if (args.length == 3) {
                MessageUtil.sendMessage(sender, DMessages.ERROR_NO_SUCH_GROUP.getMessage(args[2]));

            } else if (args.length == 2) {
                MessageUtil.sendMessage(sender, DMessages.ERROR_JOIN_GROUP.getMessage());
            }

            return;
        }

        MessageUtil.sendCenteredMessage(sender, "&4&l[ &6" + dGroup.getName() + " &4&l]");
        MessageUtil.sendMessage(sender, "&bCaptain: &e" + dGroup.getCaptain().getName());
        String players = "";
        for (Player player : dGroup.getPlayers()) {
            players += (player == dGroup.getPlayers().get(0) ? "" : "&b, &e") + player.getName();
        }
        MessageUtil.sendMessage(sender, "&bPlayers: &e" + players);
        MessageUtil.sendMessage(sender, "&bDungeon: &e" + (dGroup.getDungeonName() == null ? "N/A" : dGroup.getDungeonName()));
        MessageUtil.sendMessage(sender, "&bMap: &e" + (dGroup.getMapName() == null ? "N/A" : dGroup.getMapName()));
    }

    public void showHelp(String page) {
        MessageUtil.sendPluginTag(sender, plugin);
        switch (page) {
            default:
                MessageUtil.sendCenteredMessage(sender, "&4&l[ &61-5 &4/ &67 &4| &61 &4&l]");
                MessageUtil.sendMessage(sender, "&bcreate" + "&7 - " + DMessages.HELP_CMD_GROUP_CREATE.getMessage());
                MessageUtil.sendMessage(sender, "&bdisband" + "&7 - " + DMessages.HELP_CMD_GROUP_DISBAND.getMessage());
                MessageUtil.sendMessage(sender, "&binvite" + "&7 - " + DMessages.HELP_CMD_GROUP_INVITE.getMessage());
                MessageUtil.sendMessage(sender, "&buninvite" + "&7 - " + DMessages.HELP_CMD_GROUP_UNINVITE.getMessage());
                MessageUtil.sendMessage(sender, "&bjoin" + "&7 - " + DMessages.HELP_CMD_GROUP_JOIN.getMessage());
                break;
            case "2":
                MessageUtil.sendCenteredMessage(sender, "&4&l[ &66-10 &4/ &67 &4| &62 &4&l]");
                MessageUtil.sendMessage(sender, "&bkick" + "&7 - " + DMessages.HELP_CMD_GROUP_KICK.getMessage());
                MessageUtil.sendMessage(sender, "&bshow" + "&7 - " + DMessages.HELP_CMD_GROUP_SHOW.getMessage());
                break;
        }

    }

}
