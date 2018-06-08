/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.erethon.dungeonsxl.util;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

/**
 * @author Daniel Saukel
 */
public class LWCUtil {

    public static void removeProtection(Block block) {
        if (!isLWCLoaded()) {
            return;
        }
        Protection protection = LWC.getInstance().getProtectionCache().getProtection(block);
        if (protection != null) {
            protection.remove();
        }
    }

    /**
     * @return
     * true if LWC is loaded
     */
    public static boolean isLWCLoaded() {
        return Bukkit.getPluginManager().getPlugin("LWC") != null;
    }

}
