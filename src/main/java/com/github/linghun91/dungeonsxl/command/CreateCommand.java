package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;

/**
 * /dxl create <name> command - Create new dungeon/map
 */
public class CreateCommand extends DCommand {
    
    public CreateCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "create";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.create";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageUtil.sendError(sender, "Usage: /dxl create <name>");
            return;
        }
        
        String name = args[0];
        
        // Check if already exists
        if (plugin.getWorldManager().getResourceWorld(name).isPresent()) {
            MessageUtil.sendError(sender, "A map with that name already exists!");
            return;
        }
        
        // Create new resource world
        if (plugin.getWorldManager().createResourceWorld(name)) {
            MessageUtil.sendSuccess(sender, "Created new map: " + name);
        } else {
            MessageUtil.sendError(sender, "Failed to create map!");
        }
    }
}
