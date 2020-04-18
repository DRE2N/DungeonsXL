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
package de.erethon.dungeonsxl.world;

import de.erethon.dungeonsxl.api.sign.DungeonSign;
import de.erethon.dungeonsxl.api.world.InstanceWorld;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

/**
 * Represents the data file of a dungeon map, mainly to store signs.
 *
 * @author Daniel Saukel
 */
public class SignData {

    public static final String FILE_NAME = "DXLData.data";

    private File file;

    public SignData(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void updateFile(DResourceWorld resource) {
        file = new File(resource.getFolder(), FILE_NAME);
    }

    /**
     * Loads all signs from the file to the instance.
     *
     * @param instance the instance where the signs are
     */
    public void deserializeSigns(InstanceWorld instance) {
        try {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream(file));

            int length = os.readInt();
            for (int i = 0; i < length; i++) {
                int x = os.readInt();
                int y = os.readInt();
                int z = os.readInt();

                Block block = instance.getWorld().getBlockAt(x, y, z);
                BlockState state = block.getState();
                if (state instanceof Sign) {
                    Sign sign = (Sign) state;
                    String[] lines = sign.getLines();
                    instance.createDungeonSign(sign, lines);

                    if (lines[0].equalsIgnoreCase("[lobby]")) {
                        instance.setLobbyLocation(block.getLocation());
                    }
                }
            }

            os.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Saves all signs from an instance to the file.
     *
     * @param instance the instance that contains the signs to serialize
     */
    public void serializeSigns(InstanceWorld instance) {
        serializeSigns(instance.getDungeonSigns());
    }

    /**
     * Saves all signs from the sign list to the file.
     *
     * @param signs the signs to serialize
     */
    public void serializeSigns(Collection<DungeonSign> signs) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeInt(signs.size());

            for (DungeonSign sign : signs) {
                out.writeInt(sign.getSign().getX());
                out.writeInt(sign.getSign().getY());
                out.writeInt(sign.getSign().getZ());
            }

            out.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
