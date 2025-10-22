package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /dxl edit <map> command
 * @author linghun91
 */
public class EditCommand extends DCommand {
    
    public EditCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "edit";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.edit";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        
        if (args.length < 1) {
            MessageUtil.sendError(sender, "Usage: /dxl edit <map>");
            return;
        }
        
        Player player = (Player) sender;
        String mapName = args[0];
        
        var resourceWorld = plugin.getWorldManager().getResourceWorld(mapName);
        if (resourceWorld.isEmpty()) {
            MessageUtil.sendError(sender, "World not found: " + mapName);
            return;
        }
        
        MessageUtil.sendMessage(sender, "edit.started");
        var editWorld = resourceWorld.get().instantiateAsEdit();
        player.teleport(editWorld.getWorld().getSpawnLocation());
    }
}
