package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /dxl leave command
 * @author linghun91
 */
public class LeaveCommand extends DCommand {
    
    public LeaveCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "leave";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.leave";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        
        Player player = (Player) sender;
        MessageUtil.sendMessage(sender, "dungeon.leave");
        // TODO: Implement actual leave logic
    }
}
