package com.customclassutil.util;

import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public class RelativeLayoutUtil {
	/**
	 * Return a Center-Button RelativeLayout.LayoutParams
	 */
	public static RelativeLayout.LayoutParams getCenterBottomLayoutParams() {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		return lp;
	}

	/**
	 * Return a Center-Button RelativeLayout.LayoutParams with margin from bottom
	 */
	public static RelativeLayout.LayoutParams getCenterBottomLayoutParams(int bottom) {
		RelativeLayout.LayoutParams lp = getCenterBottomLayoutParams();
		lp.setMargins(0, 0, 0, bottom);
		return lp;
	}

	/**
	 * Return a RelativeLayout.LayoutParams with margin from left, top, right and bottom
	 */
	public static RelativeLayout.LayoutParams getMarginLayoutParams(int left, int top, int right, int bottom) {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(left, top, right, bottom);
		return lp;
	}

}
