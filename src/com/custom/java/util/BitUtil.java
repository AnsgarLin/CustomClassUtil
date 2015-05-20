package com.custom.java.util;

import java.util.BitSet;

public class BitUtil {
	/**
	 * Convert bit set to integer
	 */
	public static int toInt(BitSet bs) {
		int i = 0;
		for (int pos = -1; (pos = bs.nextSetBit(pos + 1)) != -1;) {
			i |= (1 << pos);
		}
		return i;
	}

	/**
	 * Convert bit string to integer
	 */
	public static int toInt(String bs) {
		int i = 0;
		for (int pos = -1; (pos = bs.indexOf(pos + 1)) != -1;) {
			i |= (1 << pos);
		}
		return i;
	}
}