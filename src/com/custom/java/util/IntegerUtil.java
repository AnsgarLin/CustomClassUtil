package com.custom.java.util;

import java.util.BitSet;

public class IntegerUtil {
	/**
	 * Convert integer to bit set
	 */
	public static BitSet toBitSet(int number) {
		BitSet bs = new BitSet(Integer.SIZE);
		for (int k = 0; k < Integer.SIZE; k++) {
			if ((number & (1 << k)) != 0) {
				bs.set(k);
			}
		}
		return bs;
	}

	/**
	 * Convert integer to bit string
	 */
	public static String toBitString(int number) {
		String str = "";
		for (int k = 0; k < Integer.SIZE; k++) {
			if ((number & (1 << k)) != 0) {
				str += "1";
			} else {
				str += "0";
			}
		}
		return str;
	}
}
