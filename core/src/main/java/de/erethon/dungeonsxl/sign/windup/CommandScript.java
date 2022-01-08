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
package de.erethon.dungeonsxl.sign.windup;

import de.erethon.dungeonsxl.DungeonsXL;
import java.io.File;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;

/**
 * @author Daniel Saukel
 */
public class CommandScript {

    private String name;
    private File file;
    private List<String> commands;

    public CommandScript(String name, List<String> commands, Permission permission) {
        this.name = name;
        file = new File(DungeonsXL.COMMANDS, name + ".yml");

        setCommands(commands);
    }

    public CommandScript(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        name = file.getName().replace(".yml", "");
        this.file = file;
        setCommands(config.getStringList("commands"));
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

}
