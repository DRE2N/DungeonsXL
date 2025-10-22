package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * Main /dxl command
 * @author linghun91
 */
public class MainCommand implements CommandExecutor, TabCompleter {
    
    private final DungeonsXL plugin;
    private final CommandManager commandManager;
    
    public MainCommand(DungeonsXL plugin, CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        DCommand subCommand = commandManager.getCommand(args[0]);
        if (subCommand == null) {
            MessageUtil.sendError(sender, "Unknown command: " + args[0]);
            return true;
        }
        
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);
        
        subCommand.execute(sender, subArgs);
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(commandManager.getCommands().keySet());
        }
        return List.of();
    }
    
    private void sendHelp(CommandSender sender) {
        MessageUtil.send(sender, "&6=== DungeonsXL Commands ===");
        MessageUtil.send(sender, "&e/dxl enter <dungeon> &7- Enter a dungeon");
        MessageUtil.send(sender, "&e/dxl leave &7- Leave current dungeon");
        MessageUtil.send(sender, "&e/dxl edit <map> &7- Edit a map");
        MessageUtil.send(sender, "&e/dxl save &7- Save current edit");
        MessageUtil.send(sender, "&e/dxl list &7- List all dungeons");
        MessageUtil.send(sender, "&e/dxl reload &7- Reload configuration");
    }
}
