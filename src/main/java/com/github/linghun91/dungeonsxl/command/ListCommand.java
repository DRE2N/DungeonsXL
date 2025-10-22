package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;

/**
 * /dxl list command
 * @author linghun91
 */
public class ListCommand extends DCommand {
    
    public ListCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "list";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.list";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        var dungeons = plugin.getDungeonManager().getDungeons();
        
        if (dungeons.isEmpty()) {
            MessageUtil.send(sender, "No dungeons found!");
            return;
        }
        
        MessageUtil.send(sender, "Available dungeons:");
        for (var dungeon : dungeons) {
            MessageUtil.send(sender, "- " + dungeon.getName());
        }
    }
}
