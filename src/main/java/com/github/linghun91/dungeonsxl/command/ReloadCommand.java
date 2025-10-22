package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;

/**
 * /dxl reload command
 * @author linghun91
 */
public class ReloadCommand extends DCommand {
    
    public ReloadCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "reload";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.reload";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.getMainConfig().load();
        plugin.getMessageConfig().load();
        plugin.getDungeonManager().loadDungeons();
        
        MessageUtil.sendSuccess(sender, "DungeonsXL reloaded!");
    }
}
