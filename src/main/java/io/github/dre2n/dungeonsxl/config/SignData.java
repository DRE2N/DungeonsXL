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
package io.github.dre2n.dungeonsxl.config;

import io.github.dre2n.dungeonsxl.sign.DSign;
import io.github.dre2n.dungeonsxl.world.EditWorld;
import io.github.dre2n.dungeonsxl.world.GameWorld;
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
 * @author Daniel Saukel
 */
public class SignData {

    private File file;

    public SignData(File file) {
        this.file = file;
    }

    /* Getters and setters */
    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /* Actions */
    /**
     * Applies all signs from the file to the EditWorld.
     * Also sets the lobby location of the EditWorld to the location of the lobby sign if one exists.
     *
     * @param editWorld
     * the EditWorld where the signs are
     * @throws IOException
     */
    public void deserializeSigns(EditWorld editWorld) throws IOException {
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
    }

    /**
     * Applies all signs from the file to the GameWorld.
     *
     * @param gameWorld
     * the GameWorld where the signs are
     * @return a Set of all DSign blocks
     * @throws IOException
     */
    public void deserializeSigns(GameWorld gameWorld) throws IOException {
        ObjectInputStream os = new ObjectInputStream(new FileInputStream(file));

        int length = os.readInt();
        for (int i = 0; i < length; i++) {
            int x = os.readInt();
            int y = os.readInt();
            int z = os.readInt();

            Block block = gameWorld.getWorld().getBlockAt(x, y, z);
            if (block.getState() instanceof Sign) {
                DSign dSign = DSign.create((Sign) block.getState(), gameWorld);
                gameWorld.getDSigns().add(dSign);
            }
        }

        os.close();
    }

    /**
     * Applies all signs from the EditWorld to the file.
     *
     * @param editWorld
     * the EditWorld that contains the signs to serialize
     * @throws IOException
     */
    public void serializeSigns(EditWorld editWorld) throws IOException {
        serializeSigns(editWorld.getSigns());
    }

    /**
     * Applies all signs from the sign list to the file.
     *
     * @param signs
     * the signs to serialize
     * @throws IOException
     */
    public void serializeSigns(List<Block> signs) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeInt(signs.size());

        for (Block sign : signs) {
            out.writeInt(sign.getX());
            out.writeInt(sign.getY());
            out.writeInt(sign.getZ());
        }

        out.close();
    }

}
