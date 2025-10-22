package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Base command class
 * @author linghun91
 */
public abstract class DCommand implements CommandExecutor {
    
    protected final DungeonsXL plugin;
    
    public DCommand(DungeonsXL plugin) {
        this.plugin = plugin;
    }
    
    public abstract String getName();
    public abstract String getPermission();
    public abstract void execute(CommandSender sender, String[] args);
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (getPermission() != null && !sender.hasPermission(getPermission())) {
            MessageUtil.sendMessage(sender, "no-permission");
            return true;
        }
        
        execute(sender, args);
        return true;
    }
    
    protected boolean requirePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendError(sender, "This command can only be used by players!");
            return false;
        }
        return true;
    }
}
