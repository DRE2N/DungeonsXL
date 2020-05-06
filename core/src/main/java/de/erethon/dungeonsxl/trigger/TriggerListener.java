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
package de.erethon.dungeonsxl.trigger;

import de.erethon.caliburn.item.VanillaItem;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.world.GameWorld;
import de.erethon.dungeonsxl.player.DPlayerListener;
import de.erethon.dungeonsxl.world.DGameWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Milan Albrecht, Daniel Saukel
 */
public class TriggerListener implements Listener {

    private DungeonsXL plugin;

    public TriggerListener(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockRedstone(final BlockRedstoneEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                GameWorld gameWorld = plugin.getGameWorld(event.getBlock().getWorld());
                if (gameWorld != null) {
                    RedstoneTrigger.updateAll((DGameWorld) gameWorld);
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        Player player = event.getPlayer();
        if (DPlayerListener.isCitizensNPC(player)) {
            return;
        }
        DGameWorld gameWorld = (DGameWorld) plugin.getGameWorld(player.getWorld());
        if (gameWorld == null) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        String name = null;
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) {
                name = item.getItemMeta().getDisplayName();

            } else if (VanillaItem.WRITTEN_BOOK.is(item) || VanillaItem.WRITABLE_BOOK.is(item)) {
                if (item.getItemMeta() instanceof BookMeta) {
                    BookMeta meta = (BookMeta) item.getItemMeta();
                    if (meta.hasTitle()) {
                        name = meta.getTitle();
                    }
                }
            }
        }
        if (name == null) {
            name = plugin.getCaliburn().getExItem(item).getName();
        }

        UseItemTrigger trigger = UseItemTrigger.getByName(name, gameWorld);
        if (trigger != null) {
            trigger.onTrigger(player);
        }
    }

}
