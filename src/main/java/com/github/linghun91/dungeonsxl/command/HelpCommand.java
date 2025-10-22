package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;

/**
 * /dxl help command - Show help information
 */
public class HelpCommand extends DCommand {
    
    public HelpCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "help";
    }
    
    @Override
    public String getPermission() {
        return null; // No permission required
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageUtil.send(sender, "&6=== DungeonsXL Help ===");
        MessageUtil.send(sender, "&e/dxl enter <dungeon> &7- Enter a dungeon");
        MessageUtil.send(sender, "&e/dxl leave &7- Leave current dungeon");
        MessageUtil.send(sender, "&e/dxl play <dungeon> &7- Start playing a dungeon");
        MessageUtil.send(sender, "&e/dxl list &7- List all dungeons");
        MessageUtil.send(sender, "&e/dxl group <create|info|leave> &7- Manage groups");
        MessageUtil.send(sender, "&e/dxl invite <player> &7- Invite to group");
        MessageUtil.send(sender, "&e/dxl accept &7- Accept group invitation");
        
        if (sender.hasPermission("dungeonsxl.edit")) {
            MessageUtil.send(sender, "&6=== Admin Commands ===");
            MessageUtil.send(sender, "&e/dxl create <name> &7- Create new map");
            MessageUtil.send(sender, "&e/dxl edit <map> &7- Edit a map");
            MessageUtil.send(sender, "&e/dxl save &7- Save current edit");
            MessageUtil.send(sender, "&e/dxl test <map> &7- Test a map");
            MessageUtil.send(sender, "&e/dxl delete <name> &7- Delete a map");
            MessageUtil.send(sender, "&e/dxl import <world> &7- Import world");
            MessageUtil.send(sender, "&e/dxl reload &7- Reload configuration");
        }
    }
}
