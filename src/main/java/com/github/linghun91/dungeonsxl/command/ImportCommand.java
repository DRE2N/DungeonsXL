package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /dxl import <worldName> command - Import existing world as dungeon map
 */
public class ImportCommand extends DCommand {
    
    public ImportCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "import";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.import";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageUtil.sendError(sender, "Usage: /dxl import <worldName>");
            return;
        }
        
        String worldName = args[0];
        World world = plugin.getServer().getWorld(worldName);
        
        if (world == null) {
            MessageUtil.sendError(sender, "World not found: " + worldName);
            return;
        }
        
        if (plugin.getWorldManager().importWorld(world)) {
            MessageUtil.sendSuccess(sender, "Imported world: " + worldName);
        } else {
            MessageUtil.sendError(sender, "Failed to import world!");
        }
    }
}
