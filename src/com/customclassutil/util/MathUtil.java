package com.customclassutil.util;

import android.graphics.PointF;

public class MathUtil {
	/**
	 * Ceiling of the number, accurate to given decimal places
	 */
	public static double CeilWithPoint(double num, int decimalAfter) {
		return Math.ceil(num * Math.pow(10, decimalAfter)) / Math.pow(10, decimalAfter);
	}

	/**
	 * Floor of the number, accurate to given decimal places
	 */
	public static double FloorWithPoint(double num, int decimalAfter) {
		return Math.floor(num * Math.pow(10, decimalAfter)) / Math.pow(10, decimalAfter);
	}

	/**
	 * Round of the number, accurate to given decimal places
	 */
	public static double RoundWithPoint(double num, int decimalAfter) {
		return Math.round(num * Math.pow(10, decimalAfter)) / Math.pow(10, decimalAfter);
	}

	/**
	 * Get distance between two points in 2-dimension
	 */
	public static double getDistance(float sX, float tX, float sY, float tY) {
		float x = sX - tX;
		float y = sY - tY;
		return Math.sqrt((x * x) + (y * y));
	}

	/**
	 * Get mid-point between two points in 2-dimension
	 */
	public static PointF getMidPoint(float sX, float tX, float sY, float tY) {
		return new PointF((sX + tX) / 2, (sY + tY) / 2);
	}
}