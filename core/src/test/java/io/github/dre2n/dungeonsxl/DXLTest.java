/*
 * Copyright (C) 2016 Daniel Saukel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dre2n.dungeonsxl;

import io.github.dre2n.dungeonsxl.command.CustomCommand;
import io.github.dre2n.dungeonsxl.game.CustomGameType;
import io.github.dre2n.dungeonsxl.global.ChestProtection;
import io.github.dre2n.dungeonsxl.listener.*;
import io.github.dre2n.dungeonsxl.requirement.RequirementTypeCustom;
import io.github.dre2n.dungeonsxl.reward.RewardTypeCustom;
import io.github.dre2n.dungeonsxl.sign.DSignTypeCustom;
import io.github.dre2n.dungeonsxl.trigger.TriggerTypeCustom;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Daniel Saukel
 */
public class DXLTest extends JavaPlugin {

    @Override
    public void onEnable() {
        // This is how you register /dxl subcommands.
        DungeonsXL.getInstance().getCommands().addCommand(new CustomCommand());

        // Register the DungeonsXL events just like any Bukkit event.
        getServer().getPluginManager().registerEvents(new DGroupListener(), this);
        getServer().getPluginManager().registerEvents(new DMobListener(), this);
        getServer().getPluginManager().registerEvents(new DPlayerListener(), this);
        getServer().getPluginManager().registerEvents(new DSignListener(), this);
        getServer().getPluginManager().registerEvents(new EditWorldListener(), this);
        getServer().getPluginManager().registerEvents(new GameWorldListener(), this);
        getServer().getPluginManager().registerEvents(new RequirementListener(), this);
        getServer().getPluginManager().registerEvents(new RewardListener(), this);
        getServer().getPluginManager().registerEvents(new TriggerListener(), this);

        // Register the custom game type
        DungeonsXL.getInstance().getGameTypes().addGameType(CustomGameType.GHOST);

        // There is currently no persistence API for loading the custom global protection :(
        // New instances get added to the protections, anyways.
        new ChestProtection(Bukkit.getWorlds().get(0).getBlockAt(0, 0, 0));

        // Register the custom requirement type
        DungeonsXL.getInstance().getRequirementTypes().addRequirement(RequirementTypeCustom.AWESOMENESS);

        // Register the custom reward type
        DungeonsXL.getInstance().getRewardTypes().addReward(RewardTypeCustom.HIGHWAY_TO_HELL);

        // Register the custom edit Signs
        DungeonsXL.getInstance().getDSigns().addDSign(DSignTypeCustom.CUSTOM);

        // Register the custom trigger
        DungeonsXL.getInstance().getTriggers().addTrigger(TriggerTypeCustom.CUSTOM);
    }

}
