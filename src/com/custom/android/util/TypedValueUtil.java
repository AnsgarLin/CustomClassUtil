package com.custom.android.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

public class TypedValueUtil {
	/**
	 * Transfer value from given dimension unit to pixel, return -1 for unknown dimension
	 */
	public static float toPixel(DisplayMetrics dm, String unit, float value) {
		if ((unit == "dip") || (unit == "dp")) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm);
		} else if (unit == "in") {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, value, dm);
		} else if (unit == "mm") {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, value, dm);
		} else if (unit == "pt") {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, value, dm);
		} else if (unit == "px") {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, value, dm);
		} else if (unit == "sp") {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, dm);
		} else {
			return -1;
		}
	}
}