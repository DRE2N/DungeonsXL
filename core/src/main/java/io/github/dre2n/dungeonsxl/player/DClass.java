/*
 * Copyright (C) 2012-2016 Frank Baumann
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
package io.github.dre2n.dungeonsxl.player;

import io.github.dre2n.commons.compatibility.CompatibilityHandler;
import io.github.dre2n.commons.compatibility.Version;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.util.DeserialisazionUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DClass {

    DungeonsXL plugin = DungeonsXL.getInstance();
    CompatibilityHandler compat = CompatibilityHandler.getInstance();

    private String name;

    private List<ItemStack> items = new ArrayList<>();
    private boolean dog;

    public DClass(File file) {
        this(file.getName().substring(0, file.getName().length() - 4), YamlConfiguration.loadConfiguration(file));
    }

    public DClass(String name, FileConfiguration config) {
        this.name = name;

        if (config.contains("items")) {
            List<String> itemList = config.getStringList("items");

            if (Version.andHigher(Version.MC1_9).contains(compat.getVersion())) {
                items = plugin.getCaliburnAPI().getItems().deserializeStackList(itemList);
            } else {
                items = DeserialisazionUtil.deserializeStackList(itemList);
            }
        }

        if (config.contains("dog")) {
            dog = config.getBoolean("dog");
        }
    }

    public DClass(String name, List<ItemStack> items, boolean dog) {
        this.items = items;
        this.name = name;
        this.dog = dog;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the items
     */
    public List<ItemStack> getItems() {
        return items;
    }

    /**
     * @param itemStack
     * the ItemStack to add
     */
    public void addItem(ItemStack itemStack) {
        items.add(itemStack);
    }

    /**
     * @param itemStack
     * the ItemStack to remove
     */
    public void removeItem(ItemStack itemStack) {
        items.remove(itemStack);
    }

    /**
     * @return if the class has a dog
     */
    public boolean hasDog() {
        return dog;
    }

    /**
     * @param dog
     * set if the class has a dog
     */
    public void setDog(boolean dog) {
        this.dog = dog;
    }

}
