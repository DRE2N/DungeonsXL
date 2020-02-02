/*
 * Copyright (C) 2012-2020 Frank Baumann
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
package de.erethon.dungeonsxl.sign;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.trigger.InteractTrigger;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.black_ixx.bossshop.BossShop;
import org.black_ixx.bossshop.core.BSShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class BossShopSign extends DSign {

    private BossShop bossShop;

    private String shopName;

    public BossShopSign(DungeonsXL plugin, Sign sign, String[] lines, DGameWorld gameWorld) {
        super(plugin, sign, lines, gameWorld);
    }

    /* Getters and setters*/
    /**
     * @return the name of the shop
     */
    public String getShopName() {
        return shopName;
    }

    /**
     * @param name the name of the shop
     */
    public void setShopName(String name) {
        shopName = name;
    }

    /* Actions */
    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void onInit() {
        if (Bukkit.getPluginManager().isPluginEnabled("BossShopPro")) {
            bossShop = (BossShop) Bukkit.getPluginManager().getPlugin("BossShopPro");
        } else {
            bossShop = (BossShop) Bukkit.getPluginManager().getPlugin("BossShop");
        }
        if (bossShop == null) {
            markAsErroneous("BossShop not enabled");
            return;
        } else if (bossShop.getAPI().getShop(lines[1]) == null) {
            markAsErroneous("No such BossShop");
            return;
        }

        shopName = lines[1];

        if (!getTriggers().isEmpty()) {
            getSign().getBlock().setType(VanillaItem.AIR.getMaterial());
            return;
        }

        InteractTrigger trigger = InteractTrigger.getOrCreate(0, getSign().getBlock(), getGameWorld());
        if (trigger != null) {
            trigger.addListener(this);
            addTrigger(trigger);
        }

        getSign().setLine(0, ChatColor.DARK_BLUE + "############");
        getSign().setLine(1, ChatColor.GREEN + lines[1]);
        getSign().setLine(2, ChatColor.GREEN + lines[2]);
        getSign().setLine(3, ChatColor.DARK_BLUE + "############");
        getSign().update();
    }

    @Override
    public boolean onPlayerTrigger(Player player) {
        openShop(player, shopName);
        return true;
    }

    @Override
    public DSignType getType() {
        return DSignTypeDefault.BOSS_SHOP;
    }

    public void openShop(Player player, String shopName) {
        BSShop shop = bossShop.getAPI().getShop(shopName);
        if (shop != null) {
            bossShop.getAPI().openShop(player, shop);
        } else {
            MessageUtil.sendMessage(player, DMessage.ERROR_NO_SUCH_SHOP.getMessage(shopName));
        }
    }

}
