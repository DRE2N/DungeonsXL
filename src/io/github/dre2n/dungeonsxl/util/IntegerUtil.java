package io.github.dre2n.dungeonsxl.util;

import java.util.Random;

public class IntegerUtil {
	
	public static int parseInt(String string) {
		int i;
		try {
			i = Integer.parseInt(string);
		} catch (NumberFormatException exception) {
			i = 0;
		}
		return i;
	}
	
	public static int generateRandomInt(int min, int max) {
		Random random = new Random();
		return random.nextInt(max) + min;
	}
	
	public static int parseInt(String string, int defaultReturn) {
		int i;
		try {
			i = Integer.parseInt(string);
		} catch (NumberFormatException exception) {
			i = defaultReturn;
		}
		return i;
	}
	
}
