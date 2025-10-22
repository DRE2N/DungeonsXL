package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.player.DungeonPlayer;
import com.github.linghun91.dungeonsxl.api.player.PlayerGroup;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /dxl invite <player> command - Invite player to group
 */
public class InviteCommand extends DCommand {
    
    public InviteCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "invite";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.invite";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        
        if (args.length < 1) {
            MessageUtil.sendError(sender, "Usage: /dxl invite <player>");
            return;
        }
        
        Player player = (Player) sender;
        DungeonPlayer dPlayer = plugin.getPlayerManager().getOrCreateDungeonPlayer(player);
        
        PlayerGroup group = dPlayer.getGroup();
        if (group == null) {
            MessageUtil.sendError(sender, "You are not in a group!");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtil.sendError(sender, "Player not found!");
            return;
        }
        
        group.invite(target, dPlayer);
        MessageUtil.sendSuccess(sender, "Invited " + target.getName() + " to your group!");
        MessageUtil.send(target, "&e" + player.getName() + " invited you to their group!");
        MessageUtil.send(target, "&eUse /dxl accept to join!");
    }
}
