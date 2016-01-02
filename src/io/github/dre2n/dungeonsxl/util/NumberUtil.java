package io.github.dre2n.dungeonsxl.util;

import java.util.Random;

public class NumberUtil {
	
	// Integer
	
	public static int parseInt(String string) {
		int i;
		try {
			i = Integer.parseInt(string);
		} catch (NumberFormatException exception) {
			i = 0;
		}
		return i;
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
	
	public static int generateRandomInt(int min, int max) {
		Random random = new Random();
		return random.nextInt(max) + min;
	}
	
	// Double
	
	public static double parseDouble(String string) {
		double d;
		try {
			d = Double.parseDouble(string);
		} catch (NumberFormatException exception) {
			d = 0;
		}
		return d;
	}
	
	public static double parseDouble(String string, double defaultReturn) {
		double d;
		try {
			d = Double.parseDouble(string);
		} catch (NumberFormatException exception) {
			d = defaultReturn;
		}
		return d;
	}
	
}
