package com.github.linghun91.dungeonsxl.command;

import com.github.linghun91.dungeonsxl.DungeonsXL;
import com.github.linghun91.dungeonsxl.api.player.DungeonPlayer;
import com.github.linghun91.dungeonsxl.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /dxl status command - Show current dungeon status
 */
public class StatusCommand extends DCommand {
    
    public StatusCommand(DungeonsXL plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "status";
    }
    
    @Override
    public String getPermission() {
        return "dungeonsxl.status";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!requirePlayer(sender)) return;
        
        Player player = (Player) sender;
        DungeonPlayer dPlayer = plugin.getPlayerManager().getDungeonPlayer(player);
        
        if (dPlayer == null || !dPlayer.isInDungeon()) {
            MessageUtil.send(sender, "&eYou are not in a dungeon!");
            return;
        }
        
        MessageUtil.send(sender, "&6=== Dungeon Status ===");
        MessageUtil.send(sender, "&eDungeon: " + dPlayer.getGameWorld().getGame().getDungeon().getName());
        MessageUtil.send(sender, "&ePhase: " + dPlayer.getGameWorld().getGame().getPhase());
        MessageUtil.send(sender, "&eFloor: " + (dPlayer.getGameWorld().getGame().getCurrentFloor() + 1));
        MessageUtil.send(sender, "&eWave: " + dPlayer.getGameWorld().getGame().getCurrentWave());
        
        if (dPlayer.getGroup() != null) {
            MessageUtil.send(sender, "&eGroup: " + dPlayer.getGroup().getColor().name());
            MessageUtil.send(sender, "&eScore: " + dPlayer.getGroup().getScore());
            MessageUtil.send(sender, "&eLives: " + dPlayer.getGroup().getLives());
        }
    }
}
