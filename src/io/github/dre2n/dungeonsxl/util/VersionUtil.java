package io.github.dre2n.dungeonsxl.util;

import java.util.ArrayList;
import java.util.List;

public class VersionUtil {
	
	public static enum Internals {
		v1_9_R1, v1_8_R3, v1_8_R2, v1_8_R1, v1_7_R4, v1_7_R3, OUTDATED, UNKNOWN
	}
	
	private Internals internals;
	
	public VersionUtil() {
		if (Package.getPackage("net.minecraft.server.v1_9_R1") != null) {
			internals = Internals.v1_9_R1;
			
		} else if (Package.getPackage("net.minecraft.server.v1_8_R3") != null) {
			internals = Internals.v1_8_R3;
			
		} else if (Package.getPackage("net.minecraft.server.v1_8_R2") != null) {
			internals = Internals.v1_8_R2;
			
		} else if (Package.getPackage("net.minecraft.server.v1_8_R1") != null) {
			internals = Internals.v1_8_R1;
			
		} else if (Package.getPackage("net.minecraft.server.v1_7_R4") != null) {
			internals = Internals.v1_7_R4;
			
		} else if (Package.getPackage("net.minecraft.server.v1_7_R3") != null) {
			internals = Internals.v1_7_R3;
			
		} else {
			for (Package internal : Package.getPackages()) {
				if (internal.getName().matches("net.minecraft.server.v1_[4-6]_.*")) {
					internals = Internals.OUTDATED;
				}
			}
			internals = Internals.UNKNOWN;
		}
	}
	
	public Internals getInternals() {
		return internals;
	}
	
	public static List<Internals> andHigher(Internals internals) {
		List<Internals> andHigher = new ArrayList<Internals>();
		
		switch (internals) {
			case v1_7_R3:
				andHigher.add(Internals.v1_7_R3);
			case v1_7_R4:
				andHigher.add(Internals.v1_7_R4);
			case v1_8_R1:
				andHigher.add(Internals.v1_8_R1);
			case v1_8_R2:
				andHigher.add(Internals.v1_8_R2);
			case v1_8_R3:
				andHigher.add(Internals.v1_8_R3);
			case v1_9_R1:
				andHigher.add(Internals.v1_9_R1);
			default:
				break;
		}
		return andHigher;
	}
	
}
