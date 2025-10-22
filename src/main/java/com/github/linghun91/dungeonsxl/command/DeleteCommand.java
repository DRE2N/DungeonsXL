package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;

/**
 * /dxl delete <name> command - Delete a dungeon/map
 */
public class DeleteCommand extends DCommand {
    
    public DeleteCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "delete";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.delete";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageUtil.sendError(sender, "Usage: /dxl delete <name>");
            return;
        }
        
        String name = args[0];
        
        if (plugin.getWorldManager().deleteResourceWorld(name)) {
            MessageUtil.sendSuccess(sender, "Deleted map: " + name);
        } else {
            MessageUtil.sendError(sender, "Failed to delete map or map doesn't exist!");
        }
    }
}
