package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.player.DungeonPlayer;
import com.github.linghun91.dungeonsxl.api.player.PlayerGroup;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /dxl accept command - Accept group invitation
 */
public class AcceptCommand extends DCommand {
    
    public AcceptCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "accept";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.accept";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        
        Player player = (Player) sender;
        DungeonPlayer dPlayer = plugin.getPlayerManager().getOrCreateDungeonPlayer(player);
        
        if (dPlayer.getGroup() != null) {
            MessageUtil.sendError(sender, "You are already in a group!");
            return;
        }
        
        // Find group with invitation
        PlayerGroup invitingGroup = plugin.getPlayerManager().getGroupWithInvitation(player);
        if (invitingGroup == null) {
            MessageUtil.sendError(sender, "You have no pending invitations!");
            return;
        }
        
        invitingGroup.addMember(dPlayer);
        invitingGroup.removeInvitation(player);
        MessageUtil.sendSuccess(sender, "Joined group: " + invitingGroup.getColor().name());
        invitingGroup.sendMessage("&e" + player.getName() + " joined the group!");
    }
}
