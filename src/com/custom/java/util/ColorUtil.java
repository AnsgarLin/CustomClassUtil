package com.custom.java.util;

import android.content.res.ColorStateList;
import android.graphics.Color;

public class ColorUtil {
	public static String WHITE = "#ffffff";

	/**
	 * Mix two color with the front in alpha
	 */
	public static String mixColor(String back, String cover, float coverAlpha) {
		int[] backRGB = hexToRGB(back);
		int[] coverRGB = hexToRGB(cover);
		int[] mixRGB = new int[3];
		for (int i = 0; i < backRGB.length; i++) {
			mixRGB[i] = backRGB[i] + Math.round(((coverRGB[i] - backRGB[i]) * coverAlpha));
		}
		return rgbToHex(mixRGB);
	}

	/**
	 * Transform hex color string to integer RBG array
	 */
	public static int[] hexToRGB(String color) {
		int[] numRGB = new int[3];
		numRGB[0] = Integer.parseInt(color.substring(1, 3), 16);
		numRGB[1] = Integer.parseInt(color.substring(3, 5), 16);
		numRGB[2] = Integer.parseInt(color.substring(5, 7), 16);
		return numRGB;
	}

	/**
	 * Transform integer RBG to hex color string
	 */
	public static String rgbToHex(int[] color) {
		String colorString = "#";
		for (int element : color) {
			if (element < 16) {
				colorString += "0";
			}
			colorString += Integer.toHexString(element);
		}
		return colorString;
	}

	/**
	 * Get color state list with press state. Press color will be mix with white in given alpha
	 */
	public static ColorStateList getColorStateList(String pressColor, String defaultColor, float mixAlpha) {
		int[][] states = new int[][] { new int[] { android.R.attr.state_enabled, android.R.attr.state_pressed },
				new int[] { android.R.attr.state_enabled } };
		int[] colors = new int[] { Color.parseColor(mixColor(pressColor, WHITE, mixAlpha)), Color.parseColor(defaultColor) };
		return new ColorStateList(states, colors);
	}
}
