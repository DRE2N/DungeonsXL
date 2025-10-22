package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /dxl test <map> command - Test a map in edit mode
 */
public class TestCommand extends DCommand {
    
    public TestCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "test";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.test";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        
        if (args.length < 1) {
            MessageUtil.sendError(sender, "Usage: /dxl test <map>");
            return;
        }
        
        Player player = (Player) sender;
        String mapName = args[0];
        
        if (plugin.getWorldManager().createTestInstance(mapName, player)) {
            MessageUtil.sendSuccess(sender, "Starting test of map: " + mapName);
        } else {
            MessageUtil.sendError(sender, "Failed to start test!");
        }
    }
}
