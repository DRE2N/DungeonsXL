package io.github.dre2n.dungeonsxl.sign;

import io.github.dre2n.dungeonsxl.world.GameWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class TeleportSign extends DSign {

	private DSignType type = DSignTypeDefault.TELEPORT;

	private Location location;

	public TeleportSign(Sign sign, GameWorld gameWorld) {
		super(sign, gameWorld);
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	@Override
	public boolean check() {
		String lines[] = getSign().getLines();
		for (int i = 1; i <= 2; i++) {
			if (!lines[i].isEmpty()) {
				if (letterToYaw(lines[i].charAt(0)) == -1) {
					String[] loc = lines[i].split(",");
					if (loc.length != 3) {
						return false;
					}
					try {
						Double.parseDouble(loc[0]);
						Double.parseDouble(loc[1]);
						Double.parseDouble(loc[2]);
					} catch (NumberFormatException e) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public void onInit() {
		location = getSign().getLocation().add(0.5, 0, 0.5);
		location.setYaw(letterToYaw(((org.bukkit.material.Sign) getSign().getData()).getFacing().getOppositeFace().name().charAt(0)));
		String lines[] = getSign().getLines();
		for (int i = 1; i <= 2; i++) {
			if (!lines[i].isEmpty()) {
				int yaw = letterToYaw(lines[i].charAt(0));
				if (yaw != -1) {
					location.setYaw(yaw);
				} else {
					String[] loc = lines[i].split(",");
					if (loc.length == 3) {
						try {
							double x = Double.parseDouble(loc[0]);
							double y = Double.parseDouble(loc[1]);
							double z = Double.parseDouble(loc[2]);

							// If round number, add 0.5 to tp to middle of block
							try {
								x = Integer.parseInt(loc[0]) + 0.5;
							} catch (NumberFormatException ignored) {}
							try {
								z = Integer.parseInt(loc[2]) + 0.5;
							} catch (NumberFormatException ignored) {}

							location.setX(x);
							location.setY(y);
							location.setZ(z);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		getSign().getBlock().setType(Material.AIR);
	}

	@Override
	public void onTrigger() {
		if (location != null) {
			for (Player player : getGameWorld().getWorld().getPlayers()) {
				player.teleport(location);
			}
		}
	}

	@Override
	public boolean onPlayerTrigger(Player player) {
		if (location != null) {
			player.teleport(location);
		}
		return true;
	}

	@Override
	public DSignType getType() {
		return type;
	}

	public static int letterToYaw(char c) {
		switch (c) {
			case 'S':
			case 's':
				return 0;
			case 'W':
			case 'w':
				return 90;
			case 'N':
			case 'n':
				return 180;
			case 'E':
			case 'e':
			case 'O':
			case 'o':
				return -90;
			default:
				return -1;
		}
	}
}
