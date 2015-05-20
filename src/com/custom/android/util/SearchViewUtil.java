package com.custom.android.util;

import android.content.Context;
import android.widget.ImageView;
import android.widget.SearchView;

public class SearchViewUtil {
	/**
	 * SearchView will always expand to fix the width of screen
	 */
	public static void setWidthToFitWindow(Context context, SearchView searchView) {
		searchView.setMaxWidth(context.getResources().getDisplayMetrics().widthPixels);
	}

	/**
	 * Customized SearView button with image resource
	 */
	public static void setSearchButton(SearchView searchView, int resourceId) {
		ImageView searchHintIcon = (ImageView) searchView.findViewById(searchView.getContext().getResources()
				.getIdentifier("android:id/search_button", null, null));
		if (searchHintIcon != null) {
			searchHintIcon.setImageResource(resourceId);
		}
	}
}
