/*
 * Copyright (C) 2020-2022 Daniel Saukel
 *
 * All rights reserved.
 */
package de.erethon.dungeonsxxl.sign;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import de.erethon.dungeonsxl.sign.passive.InteractSign;
import de.erethon.dungeonsxl.trigger.InteractTrigger;
import de.erethon.dungeonsxl.util.commons.misc.BlockUtil;
import de.erethon.dungeonsxl.util.commons.misc.NumberUtil;
import de.erethon.dungeonsxl.world.DGameWorld;
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
        InteractTrigger trigger = InteractTrigger.getOrCreate(NumberUtil.parseInt(getSign().getLine(1)),
                BlockUtil.getAttachedBlock(getSign().getBlock()), (DGameWorld) getGameWorld());
        if (trigger != null) {
            trigger.addListener(this);
            addTrigger(trigger);
        }
    }

}
