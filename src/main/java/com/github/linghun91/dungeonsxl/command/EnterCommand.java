package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /dxl enter <dungeon> command
 * @author linghun91
 */
public class EnterCommand extends DCommand {
    
    public EnterCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "enter";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.enter";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        
        if (args.length < 1) {
            MessageUtil.sendError(sender, "Usage: /dxl enter <dungeon>");
            return;
        }
        
        Player player = (Player) sender;
        String dungeonName = args[0];
        
        var dungeon = plugin.getDungeonManager().getDungeon(dungeonName);
        if (dungeon.isEmpty()) {
            MessageUtil.sendMessage(sender, "dungeon.not-found", dungeonName);
            return;
        }
        
        MessageUtil.sendMessage(sender, "dungeon.enter", dungeonName);
        // TODO: Implement actual enter logic
    }
}
