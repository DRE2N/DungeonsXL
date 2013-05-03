package com.dre.dungeonsxl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.dre.dungeonsxl.P;

public class DUtility {
	public static P p = P.p;

	public static void convertOldSigns() {
		File file = new File(p.getDataFolder(), "/dungeons/");

		if (file.exists()) {
			for (File dungeonFolder : file.listFiles()) {
				if (dungeonFolder.isDirectory()) {
					p.copyDirectory(dungeonFolder, new File("DXL_TMP"));

					World world = p.getServer().createWorld(WorldCreator.name("DXL_TMP"));

					// World
					try {
						ObjectInputStream os = new ObjectInputStream(new FileInputStream(new File(p.getDataFolder(), "/dungeons/" + dungeonFolder.getName() + "/DXLData.data")));
						int length = os.readInt();
						for (int i = 0; i < length; i++) {
							int x = os.readInt();
							int y = os.readInt();
							int z = os.readInt();
							Block block = world.getBlockAt(x, y, z);

							if (block.getState() instanceof Sign) {
								Sign sign = (Sign) block.getState();

								if (sign.getLine(0).equalsIgnoreCase("[dxl]")) {
									sign.setLine(0, "[" + sign.getLine(1) + "]");
									sign.setLine(1, sign.getLine(2));
									sign.setLine(2, sign.getLine(3));
									sign.update();

									if (sign.getLine(0).equalsIgnoreCase("[mob]")) {
										String[] splitted = sign.getLine(2).split(",");
										if (splitted.length >= 3) {
											sign.setLine(2, splitted[0] + "," + splitted[1]);
											sign.setLine(3, "D " + splitted[2]);
										}
									}

									if (sign.getLine(0).equalsIgnoreCase("[msg]") || sign.getLine(0).equalsIgnoreCase("[soundmsg]")) {
										sign.setLine(3, "D " + sign.getLine(2));
										sign.setLine(2, "");
									}

									if (sign.getLine(0).equalsIgnoreCase("[checkpoint]")) {
										sign.setLine(3, "D " + sign.getLine(1));
										sign.setLine(1, "");
									}

									sign.update();
								}
							}
						}

						os.close();
						p.getServer().unloadWorld(world, true);
						p.copyDirectory(new File("DXL_TMP"), dungeonFolder);
						p.removeDirectory(new File("DXL_TMP"));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
