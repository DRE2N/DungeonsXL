/*
 * Copyright (C) 2020-2023 Daniel Saukel
 *
 * All rights reserved.
 */
package de.erethon.dungeonsxxl.sign;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.sign.passive.InteractSign;
import de.erethon.dungeonsxl.trigger.InteractTrigger;
import de.erethon.dungeonsxl.api.trigger.LogicalExpression;
import de.erethon.dungeonsxl.util.BlockUtilCompat;
import org.bukkit.block.Sign;

/**
 * This sign adds an interact trigger to an attached block, like a "suspicious wall".
 *
 * @author Daniel Saukel
 */
public class InteractWallSign extends InteractSign {

    public InteractWallSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "InteractWall";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".interactwall";
    }

    @Override
    public boolean isOnDungeonInit() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public boolean isSetToAir() {
        return true;
    }

    @Override
    public void initialize() {
        String id = getSign().getLine(1);
        InteractTrigger trigger = (InteractTrigger) getGameWorld().createTrigger(this, LogicalExpression.parse("I" + id));
        trigger.setInteractBlock(BlockUtilCompat.getAttachedBlock(getSign().getBlock()));
    }

}
