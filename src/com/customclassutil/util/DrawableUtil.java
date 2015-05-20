package com.customclassutil.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DrawableUtil {
	/**
	 * Get a Drawable from URL
	 */
	public static Drawable getDrawableFromURL(URL url) {
		try {
			return Drawable.createFromStream((InputStream) url.getContent(), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get a StateDrawable with press state
	 */
	public static StateListDrawable getStateDrawableWithColor(String pressColor, String defaultColor) {
		StateListDrawable stateListDrawable = new StateListDrawable();
		stateListDrawable.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled },
				new ColorDrawable(Color.parseColor(pressColor)));
		stateListDrawable.addState(new int[] { -android.R.attr.state_enabled }, new ColorDrawable(Color.parseColor(defaultColor)));
		return stateListDrawable;
	}

	/**
	 * Get a StateDrawable with press state
	 */
	public StateListDrawable getStateDrawableWithColor(int pressColor, int defaultColor) {
		StateListDrawable stateListDrawable = new StateListDrawable();
		stateListDrawable.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, new ColorDrawable(pressColor));
		stateListDrawable.addState(new int[] { -android.R.attr.state_enabled }, new ColorDrawable(defaultColor));
		return stateListDrawable;
	}
}

