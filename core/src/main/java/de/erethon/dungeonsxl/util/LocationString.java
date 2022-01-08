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
package de.erethon.dungeonsxl.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author Daniel Saukel
 */
public class LocationString {

    private String raw;
    private String world;
    private double x, y, z;
    private float yaw, pitch;
    private Location location;

    private LocationString(String string) {
        raw = string;
    }

    public static LocationString fromString(String string) {
        if (string == null) {
            return null;
        }

        String[] args = string.split(",");
        if (args.length < 4 || args.length == 5 || args.length > 6) {
            return null;
        }

        double x, y, z;
        float yaw, pitch;
        try {
            x = Double.parseDouble(args[1]);
            y = Double.parseDouble(args[2]);
            z = Double.parseDouble(args[3]);
            if (args.length == 6) {
                yaw = Float.parseFloat(args[4]);
                pitch = Float.parseFloat(args[5]);
            } else {
                yaw = 0f;
                pitch = 0f;
            }
        } catch (NumberFormatException exception) {
            return null;
        }

        LocationString locationString = new LocationString(string);
        locationString.world = args[0];
        locationString.x = x;
        locationString.y = y;
        locationString.z = z;
        locationString.yaw = yaw;
        locationString.pitch = pitch;
        return locationString;
    }

    public Location getLocation() {
        if (location == null) {
            World bukkitWorld = Bukkit.getWorld(world);
            if (bukkitWorld == null) {
                return null;
            }
            location = new Location(bukkitWorld, x, y, z, yaw, pitch);
        }
        return location;
    }

    @Override
    public String toString() {
        return raw;
    }

}
