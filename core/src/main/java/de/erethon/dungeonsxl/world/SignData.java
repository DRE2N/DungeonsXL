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

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.sign.DSign;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Represents the data file of a dungeon map, mainly to store signs.
 *
 * @author Daniel Saukel
 */
public class SignData {

    private DungeonsXL plugin;

    private File file;

    public SignData(DungeonsXL plugin, File file) {
        this.plugin = plugin;

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        this.file = file;
    }

    /* Getters and setters */
    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    public void updateFile(DResourceWorld resource) {
        file = new File(resource.getFolder(), "DXLData.data");
    }

    /* Actions */
    /**
     * Applies all signs from the file to the DEditWorld. Also sets the lobby location of the DEditWorld to the location of the lobby sign if one exists.
     *
     * @param editWorld the DEditWorld where the signs are
     */
    public void deserializeSigns(DEditWorld editWorld) {
        try {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream(file));

            int length = os.readInt();
            for (int i = 0; i < length; i++) {
                int x = os.readInt();
                int y = os.readInt();
                int z = os.readInt();

                Block block = editWorld.getWorld().getBlockAt(x, y, z);
                editWorld.getSigns().add(block);

                if (block.getState() instanceof Sign) {
                    Sign sign = (Sign) block.getState();
                    String[] lines = sign.getLines();

                    if (lines[0].equalsIgnoreCase("[lobby]")) {
                        editWorld.setLobbyLocation(block.getLocation());
                    }
                }
            }

            os.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Applies all signs from the file to the DGameWorld.
     *
     * @param gameWorld the DGameWorld where the signs are
     */
    public void deserializeSigns(DGameWorld gameWorld) {
        try {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream(file));

            int length = os.readInt();
            for (int i = 0; i < length; i++) {
                int x = os.readInt();
                int y = os.readInt();
                int z = os.readInt();

                Block block = gameWorld.getWorld().getBlockAt(x, y, z);
                if (block.getState() instanceof Sign) {
                    DSign dSign = DSign.create(plugin, (Sign) block.getState(), gameWorld);
                    gameWorld.getDSigns().add(dSign);
                }
            }

            os.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Applies all signs from the DEditWorld to the file.
     *
     * @param editWorld the DEditWorld that contains the signs to serialize
     */
    public void serializeSigns(DEditWorld editWorld) {
        serializeSigns(editWorld.getSigns());
    }

    /**
     * Applies all signs from the sign list to the file.
     *
     * @param signs the signs to serialize
     */
    public void serializeSigns(List<Block> signs) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeInt(signs.size());

            for (Block sign : signs) {
                out.writeInt(sign.getX());
                out.writeInt(sign.getY());
                out.writeInt(sign.getZ());
            }

            out.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
