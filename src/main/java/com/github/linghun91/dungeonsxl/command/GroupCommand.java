package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.player.DungeonPlayer;
import com.github.linghun91.dungeonsxl.api.player.GroupColor;
import com.github.linghun91.dungeonsxl.api.player.PlayerGroup;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /dxl group <create|disband|info> command - Manage player groups
 */
public class GroupCommand extends DCommand {
    
    public GroupCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "group";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.group";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        
        if (args.length < 1) {
            MessageUtil.sendError(sender, "Usage: /dxl group <create|disband|info|leave>");
            return;
        }
        
        Player player = (Player) sender;
        DungeonPlayer dPlayer = plugin.getPlayerManager().getOrCreateDungeonPlayer(player);
        
        String subCmd = args[0].toLowerCase();
        
        switch (subCmd) {
            case "create" -> {
                if (dPlayer.getGroup() != null) {
                    MessageUtil.sendError(sender, "You are already in a group!");
                    return;
                }
                
                PlayerGroup group = plugin.getPlayerManager().createGroup(dPlayer);
                MessageUtil.sendSuccess(sender, "Created group: " + group.getColor().name());
            }
            
            case "disband" -> {
                PlayerGroup group = dPlayer.getGroup();
                if (group == null) {
                    MessageUtil.sendError(sender, "You are not in a group!");
                    return;
                }
                
                if (!group.getLeader().equals(dPlayer)) {
                    MessageUtil.sendError(sender, "Only the group leader can disband!");
                    return;
                }
                
                plugin.getPlayerManager().disbandGroup(group);
                MessageUtil.sendSuccess(sender, "Group disbanded!");
            }
            
            case "info" -> {
                PlayerGroup group = dPlayer.getGroup();
                if (group == null) {
                    MessageUtil.sendError(sender, "You are not in a group!");
                    return;
                }
                
                MessageUtil.send(sender, "&6=== Group Info ===");
                MessageUtil.send(sender, "&eColor: " + group.getColor().name());
                MessageUtil.send(sender, "&eLeader: " + group.getLeader().getPlayer().getName());
                MessageUtil.send(sender, "&eMembers: " + group.getMembers().size());
                MessageUtil.send(sender, "&eScore: " + group.getScore());
            }
            
            case "leave" -> {
                PlayerGroup group = dPlayer.getGroup();
                if (group == null) {
                    MessageUtil.sendError(sender, "You are not in a group!");
                    return;
                }
                
                group.removeMember(dPlayer);
                MessageUtil.sendSuccess(sender, "Left the group!");
            }
            
            default -> MessageUtil.sendError(sender, "Unknown subcommand: " + subCmd);
        }
    }
}
