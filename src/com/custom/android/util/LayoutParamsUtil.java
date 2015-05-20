package com.custom.android.util;

import android.view.View;

public class LayoutParamsUtil {
	/**
	 * Swap LayoutParams with target view
	 * 
	 * @param leftLayoutParams
	 *            The LayoutParams that will be set to the target view.
	 * @param right
	 *            The target view.
	 * @return The target view's old LayoutParams.
	 */
	public static android.view.ViewGroup.LayoutParams swapLayoutParam(android.view.ViewGroup.LayoutParams leftLayoutParams, View right) {
		android.view.ViewGroup.LayoutParams rightLayoutParams = right.getLayoutParams();
		right.setLayoutParams(leftLayoutParams);
		return rightLayoutParams;
	}
}
