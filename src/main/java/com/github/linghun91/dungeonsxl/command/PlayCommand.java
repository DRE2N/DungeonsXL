package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.dungeon.Dungeon;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /dxl play <dungeon> command - Start playing a dungeon
 */
public class PlayCommand extends DCommand {
    
    public PlayCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "play";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.play";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        
        if (args.length < 1) {
            MessageUtil.sendError(sender, "Usage: /dxl play <dungeon>");
            return;
        }
        
        Player player = (Player) sender;
        String dungeonName = args[0];
        
        Dungeon dungeon = plugin.getDungeonManager().getDungeon(dungeonName);
        if (dungeon == null) {
            MessageUtil.sendError(sender, "Dungeon not found: " + dungeonName);
            return;
        }
        
        // Check requirements
        if (!dungeon.canPlayerJoin(player)) {
            MessageUtil.sendError(sender, "You don't meet the requirements for this dungeon!");
            return;
        }
        
        // Create game instance
        if (plugin.getDungeonManager().createGame(dungeon, player)) {
            MessageUtil.sendSuccess(sender, "Starting dungeon: " + dungeonName);
        } else {
            MessageUtil.sendError(sender, "Failed to start dungeon!");
        }
    }
}
