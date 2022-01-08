/*
 * Copyright (C) 2012-2022 Frank Baumann
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
package de.erethon.dungeonsxl.sign.passive;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.sign.Passive;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import de.erethon.dungeonsxl.player.DPermission;
import org.bukkit.block.Sign;

/**
 * @author Daniel Saukel
 */
public class ScriptSign extends Passive {

    private String scriptName;

    public ScriptSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
        scriptName = lines[1];
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String name) {
        scriptName = name;
    }

    @Override
    public String getName() {
        return "Script";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".script";
    }

    @Override
    public boolean isOnDungeonInit() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isSetToAir() {
        return false;
    }

    @Override
    public boolean validate() {
        return ((DungeonsXL) api).getSignScriptRegistry().get(scriptName) != null;
    }

    @Override
    public void initialize() {
        SignScript script = ((DungeonsXL) api).getSignScriptRegistry().get(scriptName);
        if (script == null) {
            markAsErroneous("The script \"" + scriptName + "\" could not be found.");
            return;
        }

        DungeonSign dSign = null;
        for (String[] lines : script.getSigns()) {
            dSign = getGameWorld().createDungeonSign(getSign(), lines);
            if (dSign.isErroneous()) {
                getGameWorld().removeDungeonSign(dSign);
                continue;
            }

            try {
                dSign.initialize();
            } catch (Exception exception) {
                dSign.markAsErroneous("An error occurred while initializing a sign of the type " + dSign.getName()
                        + ". This is not a user error. Please report the following stacktrace to the developer of the plugin:");
                exception.printStackTrace();
            }
            if (!dSign.hasTriggers()) {
                try {
                    dSign.trigger(null);
                } catch (Exception exception) {
                    markAsErroneous("An error occurred while triggering a sign of the type " + getName()
                            + ". This is not a user error. Please report the following stacktrace to the developer of the plugin:");
                    exception.printStackTrace();
                }
            }
        }

        if (dSign == null) {
            markAsErroneous("The script \"" + scriptName + "\" could not be found.");
            return;
        }
        if (dSign.isSetToAir()) {
            dSign.setToAir();
        }
    }

}
