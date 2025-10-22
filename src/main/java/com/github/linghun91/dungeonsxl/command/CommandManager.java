package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import org.bukkit.command.PluginCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Command manager
 * @author linghun91
 */
public class CommandManager {
    
    private final DungeonsXL plugin;
    private final Map<String, DCommand> commands = new HashMap<>();
    
    public CommandManager(DungeonsXL plugin) {
        this.plugin = plugin;
    }
    
    public void registerAllCommands() {
        // Register main command
        PluginCommand mainCmd = plugin.getCommand("dungeonsxl");
        if (mainCmd != null) {
            MainCommand mainCommand = new MainCommand(plugin, this);
            mainCmd.setExecutor(mainCommand);
            mainCmd.setTabCompleter(mainCommand);
        }
        
        // Register subcommands
        register(new HelpCommand(plugin));
        register(new EnterCommand(plugin));
        register(new LeaveCommand(plugin));
        register(new PlayCommand(plugin));
        register(new EditCommand(plugin));
        register(new SaveCommand(plugin));
        register(new ListCommand(plugin));
        register(new ReloadCommand(plugin));
        register(new CreateCommand(plugin));
        register(new DeleteCommand(plugin));
        register(new GroupCommand(plugin));
        register(new InviteCommand(plugin));
        register(new AcceptCommand(plugin));
        register(new TestCommand(plugin));
        register(new ImportCommand(plugin));
        register(new StatusCommand(plugin));
        
        plugin.getLogger().info("Registered " + commands.size() + " commands");
    }
    
    private void register(DCommand command) {
        commands.put(command.getName().toLowerCase(), command);
    }
    
    public DCommand getCommand(String name) {
        return commands.get(name.toLowerCase());
    }
    
    public Map<String, DCommand> getCommands() {
        return new HashMap<>(commands);
    }
}
