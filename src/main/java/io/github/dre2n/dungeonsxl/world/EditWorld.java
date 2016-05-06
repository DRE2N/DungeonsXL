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
package io.github.dre2n.dungeonsxl.world;

import io.github.dre2n.commons.util.FileUtil;
import io.github.dre2n.commons.util.messageutil.MessageUtil;
import io.github.dre2n.dungeonsxl.DungeonsXL;
import io.github.dre2n.dungeonsxl.config.WorldConfig;
import io.github.dre2n.dungeonsxl.event.editworld.EditWorldGenerateEvent;
import io.github.dre2n.dungeonsxl.event.editworld.EditWorldLoadEvent;
import io.github.dre2n.dungeonsxl.event.editworld.EditWorldSaveEvent;
import io.github.dre2n.dungeonsxl.event.editworld.EditWorldUnloadEvent;
import io.github.dre2n.dungeonsxl.player.DGamePlayer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class EditWorld {

    static DungeonsXL plugin = DungeonsXL.getInstance();

    // Variables
    private World world;
    private String owner;
    private String name;
    private String mapName;
    private int id;
    private Location lobby;
    private CopyOnWriteArrayList<String> invitedPlayers = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Block> signs = new CopyOnWriteArrayList<>();

    public EditWorld() {
        plugin.getEditWorlds().add(this);

        // ID
        id = -1;
        int i = -1;
        while (id == -1) {
            i++;
            boolean exist = false;
            for (EditWorld editWorld : plugin.getEditWorlds()) {
                if (editWorld.id == i) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                id = i;
            }
        }

        name = "DXL_Edit_" + id;
    }

    /**
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * @param world
     * the world to set
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner
     * the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the mapName
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @param mapName
     * the mapName to set
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     * the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the location of the lobby
     */
    public Location getLobbyLocation() {
        return lobby;
    }

    /**
     * @param lobby
     * the lobby to set
     */
    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    /**
     * @return the invitedPlayers
     */
    public CopyOnWriteArrayList<String> getInvitedPlayers() {
        return invitedPlayers;
    }

    /**
     * @param invitedPlayers
     * the invitedPlayers to set
     */
    public void setInvitedPlayers(CopyOnWriteArrayList<String> invitedPlayers) {
        this.invitedPlayers = invitedPlayers;
    }

    /**
     * @return the signs
     */
    public CopyOnWriteArrayList<Block> getSigns() {
        return signs;
    }

    /**
     * @param sign
     * the sign to set
     */
    public void setSigns(CopyOnWriteArrayList<Block> signs) {
        this.signs = signs;
    }

    public void generate() {
        WorldCreator creator = WorldCreator.name(name);
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);

        EditWorldGenerateEvent event = new EditWorldGenerateEvent(this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        world = plugin.getServer().createWorld(creator);
    }

    public void save() {
        EditWorldSaveEvent event = new EditWorldSaveEvent(this);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        world.save();

        File dir = new File("DXL_Edit_" + id);
        FileUtil.copyDirectory(dir, new File(plugin.getDataFolder(), "/maps/" + mapName), DungeonsXL.EXCLUDED_FILES);
        FileUtil.deleteUnusedFiles(new File(plugin.getDataFolder(), "/maps/" + mapName));

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(plugin.getDataFolder(), "/maps/" + mapName + "/DXLData.data")));
            out.writeInt(signs.size());
            for (Block sign : signs) {
                out.writeInt(sign.getX());
                out.writeInt(sign.getY());
                out.writeInt(sign.getZ());
            }
            out.close();

        } catch (IOException exception) {
        }
    }

    public void checkSign(Block block) {
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            String[] lines = sign.getLines();

            if (lines[0].equalsIgnoreCase("[lobby]")) {
                lobby = block.getLocation();
            }
        }
    }

    public void delete() {
        EditWorldUnloadEvent event = new EditWorldUnloadEvent(this, true);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        plugin.getEditWorlds().remove(this);
        for (Player player : world.getPlayers()) {
            DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
            dPlayer.leave();
        }

        plugin.getServer().unloadWorld(world, true);
        File dir = new File("DXL_Edit_" + id);
        FileUtil.copyDirectory(dir, new File(plugin.getDataFolder(), "/maps/" + mapName), DungeonsXL.EXCLUDED_FILES);
        FileUtil.deleteUnusedFiles(new File(plugin.getDataFolder(), "/maps/" + mapName));
        FileUtil.removeDirectory(dir);
    }

    public void deleteNoSave() {
        EditWorldUnloadEvent event = new EditWorldUnloadEvent(this, false);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        plugin.getEditWorlds().remove(this);
        for (Player player : world.getPlayers()) {
            DGamePlayer dPlayer = DGamePlayer.getByPlayer(player);
            dPlayer.leave();
        }

        File dir = new File("DXL_Edit_" + id);
        FileUtil.copyDirectory(dir, new File(plugin.getDataFolder(), "/maps/" + mapName), DungeonsXL.EXCLUDED_FILES);
        FileUtil.deleteUnusedFiles(new File(plugin.getDataFolder(), "/maps/" + mapName));
        plugin.getServer().unloadWorld(world, true);
        FileUtil.removeDirectory(dir);
    }

    public void sendMessage(String message) {
        for (DGamePlayer dPlayer : DGamePlayer.getByWorld(world)) {
            MessageUtil.sendMessage(dPlayer.getPlayer(), message);
        }
    }

    /* Statics */
    public static EditWorld getByWorld(World world) {
        for (EditWorld editWorld : plugin.getEditWorlds()) {
            if (editWorld.world.equals(world)) {
                return editWorld;
            }
        }

        return null;
    }

    public static EditWorld getByName(String name) {
        for (EditWorld editWorld : plugin.getEditWorlds()) {
            if (editWorld.mapName.equalsIgnoreCase(name)) {
                return editWorld;
            }
        }

        return null;
    }

    public static void deleteAll() {
        for (EditWorld editWorld : plugin.getEditWorlds()) {
            editWorld.delete();
        }
    }

    public static EditWorld load(String name) {
        EditWorldLoadEvent event = new EditWorldLoadEvent(name);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        for (EditWorld editWorld : plugin.getEditWorlds()) {

            if (editWorld.mapName.equalsIgnoreCase(name)) {
                return editWorld;
            }
        }

        File file = new File(plugin.getDataFolder(), "/maps/" + name);

        if (file.exists()) {
            EditWorld editWorld = new EditWorld();
            editWorld.mapName = name;
            // World
            FileUtil.copyDirectory(file, new File("DXL_Edit_" + editWorld.id), DungeonsXL.EXCLUDED_FILES);

            // Id File
            File idFile = new File("DXL_Edit_" + editWorld.id + "/.id_" + name);
            try {
                idFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            editWorld.world = plugin.getServer().createWorld(WorldCreator.name("DXL_Edit_" + editWorld.id));

            try {
                ObjectInputStream os = new ObjectInputStream(new FileInputStream(new File(plugin.getDataFolder(), "/maps/" + editWorld.mapName + "/DXLData.data")));
                int length = os.readInt();
                for (int i = 0; i < length; i++) {
                    int x = os.readInt();
                    int y = os.readInt();
                    int z = os.readInt();
                    Block block = editWorld.world.getBlockAt(x, y, z);
                    editWorld.checkSign(block);
                    editWorld.signs.add(block);
                }
                os.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return editWorld;
        }

        return null;
    }

    public static boolean exists(String name) {
        // Cheack Loaded EditWorlds
        for (EditWorld editWorld : plugin.getEditWorlds()) {
            if (editWorld.mapName.equalsIgnoreCase(name)) {
                return true;
            }
        }

        // Cheack Unloaded Worlds
        File file = new File(plugin.getDataFolder(), "/maps/" + name);

        if (file.exists()) {
            return true;
        }

        return false;
    }

    // Invite
    public static boolean addInvitedPlayer(String editWorldName, UUID uuid) {
        if (!exists(editWorldName)) {
            return false;
        }

        File file = new File(plugin.getDataFolder() + "/maps/" + editWorldName, "config.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();

            } catch (IOException exception) {
                exception.printStackTrace();
                return false;
            }
        }
        WorldConfig config = new WorldConfig(file);
        config.addInvitedPlayer(uuid.toString());
        config.save();

        return true;
    }

    public static boolean removeInvitedPlayer(String editWorldName, UUID uuid, String name) {
        if (!exists(editWorldName)) {
            return false;
        }

        File file = new File(plugin.getDataFolder() + "/maps/" + editWorldName, "config.yml");
        if (!file.exists()) {
            return false;
        }
        WorldConfig config = new WorldConfig(file);
        config.removeInvitedPlayers(uuid.toString(), name.toLowerCase());
        config.save();

        // Kick Player
        EditWorld editWorld = EditWorld.getByName(editWorldName);
        if (editWorld != null) {
            DGamePlayer player = DGamePlayer.getByName(name);

            if (player != null) {
                if (editWorld.world.getPlayers().contains(player.getPlayer())) {
                    player.leave();
                }
            }
        }

        return true;
    }

    public static boolean isInvitedPlayer(String editWorldName, UUID uuid, String name) {
        if (!exists(editWorldName)) {
            return false;
        }

        File file = new File(plugin.getDataFolder() + "/maps/" + editWorldName, "config.yml");
        if (!file.exists()) {
            return false;
        }

        WorldConfig config = new WorldConfig(file);
        // get player from both a 0.9.1 and lower and 0.9.2 and higher file
        if (config.getInvitedPlayers().contains(name.toLowerCase()) || config.getInvitedPlayers().contains(uuid.toString())) {
            return true;

        } else {
            return false;
        }
    }

}
