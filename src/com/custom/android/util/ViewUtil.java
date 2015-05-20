package com.custom.android.util;

import android.view.View;

public class ViewUtil {
	/**
	 * Get view left/top/right/bottom in parent
	 */
	public static float[] getViewBoundsInParent(View view) {
		float[] values = new float[4];
		values[0] = view.getX() + (view.getPivotX() * (1 - view.getScaleX()));
		values[1] = view.getY() + (view.getPivotY() * (1 - view.getScaleY()));
		values[2] = values[0] + (view.getWidth() * view.getScaleX());
		values[3] = values[1] + (view.getHeight() * view.getScaleY());
		return values;
	}
}