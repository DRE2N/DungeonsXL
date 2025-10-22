package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /dxl save command
 * @author linghun91
 */
public class SaveCommand extends DCommand {
    
    public SaveCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "save";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.save";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        
        Player player = (Player) sender;
        var instance = plugin.getWorldManager().getInstance(player.getWorld());
        
        if (instance.isEmpty()) {
            MessageUtil.sendError(sender, "You are not in an edit world!");
            return;
        }
        
        if (instance.get() instanceof com.github.linghun91.dungeonsxl.api.world.EditWorld editWorld) {
            if (editWorld.save()) {
                MessageUtil.sendMessage(sender, "edit.saved");
            } else {
                MessageUtil.sendError(sender, "Failed to save world!");
            }
        }
    }
}
