package com.customclassutil.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class InflateUtil {
	/**
	 * Inflate the specific layout, default attach to root is false
	 */
	public static View InflateReource(Context context, int resourceID, ViewGroup parent) {
		return SystemUtil.getLayoutInflater(context).inflate(resourceID, parent, false);
	}
}
