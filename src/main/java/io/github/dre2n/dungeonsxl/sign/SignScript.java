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
package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.commons.util.NumberUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Daniel Saukel
 */
public class SignScript {

    DungeonsXL plugin = DungeonsXL.getInstance();

    private String name;

    private List<String[]> signs;

    /**
     * @param file
     * the script file
     */
    public SignScript(File file) {
        this(file.getName().substring(0, file.getName().length() - 4), YamlConfiguration.loadConfiguration(file));
    }

    /**
     * @param name
     * the name of the Announcer
     * @param config
     * the config that stores the information
     */
    public SignScript(String name, FileConfiguration config) {
        this.name = name;
        signs = new ArrayList<>(config.getKeys(false).size());

        for (String key : config.getKeys(false)) {
            int index = NumberUtil.parseInt(key);
            String[] lines = new String[]{};
            lines = config.getStringList(key).toArray(lines);
            signs.add(index, lines);
        }
    }

    /**
     * @return the name of the announcer
     */
    public String getName() {
        return name;
    }

    /**
     * @return the signs
     */
    public List<String[]> getSigns() {
        return signs;
    }

    /**
     * @param index
     * the index number
     * @return the lines of the sign
     */
    public String[] getLines(int index) {
        return signs.get(index);
    }

    /**
     * @param index
     * the index number
     * @param lines
     * the lines to set
     */
    public void setLines(int index, String[] lines) {
        signs.set(index, lines);
    }

}
