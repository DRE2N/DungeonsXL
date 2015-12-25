package io.github.dre2n.dungeonsxl.sign;

import java.util.ArrayList;
import java.util.List;

public class DSigns {
	
	private List<DSignType> dSigns = new ArrayList<DSignType>();
	
	public DSigns() {
		for (DSignType type : DSignTypeDefault.values()) {
			dSigns.add(type);
		}
	}
	
	/**
	 * @return the dSigns
	 */
	public List<DSignType> getDSigns() {
		return dSigns;
	}
	
	/**
	 * @param dSign
	 * the dSigns to add
	 */
	public void addDSign(DSignType dSign) {
		dSigns.add(dSign);
	}
	
	/**
	 * @param dSign
	 * the dSigns to remove
	 */
	public void removeDSign(DSignType dSign) {
		dSigns.remove(dSign);
	}
	
}
